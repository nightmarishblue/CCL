package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.statement.Assign;

import java.util.function.BiConsumer;

public class SemanticChecker extends AstVisitor<Void> {
    private final SymbolTable<Void> environment = new SymbolTable<>();
    public final BiConsumer<Node, String> onError;

    public SemanticChecker(BiConsumer<Node, String> onError) {
        this.onError = onError;
    }

    private void error(Node node, String message) {
        onError.accept(node, message);
    }

    private void wrapScope(Node node) {
        push();
        visitChildren(node);
        pop();
    }

    private void push() {
        environment.pushScope();
    }

    private void pop() {
        environment.popScope();
    }

    private void add(Node node, final Identifier id) {
        if (environment.peekScope().containsKey(id))
            error(node, String.format("Identifier %s is already defined in this scope", id));
        environment.putSymbol(id, null);
    }

    private void get(Node node, final Identifier id) {
        if (!environment.getSymbol(id).present())
            error(node, String.format("Identifier %s is undefined\n", id));
    }

    // create & destroy scopes
    @Override
    public Void visitProgram(Program node) {
        wrapScope(node);
        return null;
    }

    @Override
    public Void visitMain(Main node) {
        wrapScope(node);
        return null;
    }

    @Override
    public Void visitFunction(Function node) {
        add(node, node.name); // functions add their name to the parent scope
        push();
        // functions add their parameters to their scope
        node.parameters.stream().map(Variable::name).forEach(p -> add(node, p));
        visitChildren(node);
        pop();
        return null;
    }

    // declarations are the only other source of symbols in the table
    @Override
    public Void visitDeclaration(Declaration node) {
        add(node, node.variable.name());
        return super.visitDeclaration(node);
    }

    @Override
    public Void visitReference(Reference node) {
        get(node, node.variable);
        return super.visitReference(node);
    }

    @Override
    public Void visitCall(Call node) {
        get(node, node.function);
        node.arguments.forEach(a -> get(node, a));
        return super.visitCall(node);
    }

    @Override
    public Void visitAssign(Assign node) {
        get(node, node.variable);
        return super.visitAssign(node);
    }
}
