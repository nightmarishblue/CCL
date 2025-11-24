package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.statement.Assign;
import org.example.ast.node.statement.Statement;
import org.example.helper.Option;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// an experimental optimiser that removes unused variables and functions from the tree
public class UnusedPruner extends AstVisitor<Node> {
    final private SymbolTable<Boolean> symbols = new SymbolTable<>();

    public static UnusedPruner withBuiltins(Collection<Identifier> builtins) {
        UnusedPruner output = new UnusedPruner();
        output.symbols.pushScope();
        builtins.forEach(output::add);
        return output;
    }

    private void add(Identifier id) {
        symbols.putSymbol(id, false);
    }

    private void use(Identifier id) {
        // need to get the current value of id and mutate that
        Option<Map<Identifier, Boolean>> scope = symbols.getSymbolScope(id);
        if (!(scope instanceof Option.Some<Map<Identifier, Boolean>>(Map<Identifier, Boolean> map)))
            throw new RuntimeException("Undeclared identifier used");
        map.put(id, true);
    }

    private Set<Identifier> used() {
        return symbols.peekScope().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected Node defaultValue(Node node) {
        return node;
    }

    @Override
    protected Node aggregate(Node current, Node next) {
        return current; // keep the one on the left (parent)
    }

    // everything that starts a scope
    @Override
    public Program visitProgram(Program program) {
        symbols.pushScope();

        program.declarations.forEach(this::visitDeclaration);
        final List<Function> optimisedFunctions =  program.functions.stream().map(this::visitFunction).toList();
        final Main main = visitMain(program.main);

        Set<Identifier> used = used();
        List<Declaration> usedDeclarations = program.declarations.stream()
                .filter(d -> used.contains(d.variable.name())).toList();
        List<Function> usedFunctions = optimisedFunctions.stream().filter(f -> used.contains(f.name)).toList();

        symbols.popScope();
        return new Program(usedDeclarations, usedFunctions, main);
    }

    @Override
    public Main visitMain(Main main) {
        symbols.pushScope();
        visitChildren(main);

        Set<Identifier> used = used();
        Set<Identifier> toPrune = main.declarations.stream()
                .map(Declaration::variable)
                .map(Variable::name)
                .filter(id -> !used.contains(id))
                .collect(Collectors.toUnmodifiableSet());

        List<Declaration> usedDeclarations = main.declarations.stream()
                .filter(d -> !toPrune.contains(d.variable.name())).toList();

        // any writes to a pruned variable also have to be removed
        List<Statement> cleanStatements = main.statements.stream()
                .filter(s -> !(s instanceof Assign assign && toPrune.contains(assign.variable)))
                .toList();

        symbols.popScope();
        return new Main(usedDeclarations, cleanStatements);
    }

    // everything that defines an identifier
    @Override
    public Function visitFunction(Function function) { // function does both
        add(function.name); // add name to parent's scope
        symbols.pushScope();
        function.parameters.stream().map(Variable::name).forEach(this::add); // add parameters, even though we won't remove them
        visitChildren(function);

        Set<Identifier> used = used();
        Set<Identifier> toPrune = function.declarations.stream()
                .map(Declaration::variable)
                .map(Variable::name)
                .filter(id -> !used.contains(id))
                .collect(Collectors.toUnmodifiableSet());

        List<Declaration> usedDeclarations = function.declarations.stream()
                .filter(d -> !toPrune.contains(d.variable.name())).toList();

        // any writes to a pruned variable also have to be removed
        List<Statement> cleanStatements = function.statements.stream()
                .filter(s -> !(s instanceof Assign assign && toPrune.contains(assign.variable)))
                .toList();

        symbols.popScope();
        return new Function(function.type, function.name, function.parameters,
                usedDeclarations, cleanStatements, function.output);
    }

    @Override
    public Node visitDeclaration(Declaration node) {
        add(node.variable.name());
        visitChildren(node);
        return node;
    }

    // everything that uses an identifier
    @Override
    public Node visitCall(Call node) {
        use(node.function);
        node.arguments.forEach(this::use);
        return node;
    }

    @Override
    public Node visitReference(Reference node) {
        // reference could point to a
        use(node.variable);
        return node;
    }
}
