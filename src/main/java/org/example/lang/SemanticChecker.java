package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.declaration.Declaration;

public class SemanticChecker extends AstVisitor<Void> {
    private final SymbolTable<Void> environment = new SymbolTable<>();

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

    private void add(final Identifier id) {
        if (environment.peekScope().containsKey(id)) {
            System.err.printf("Identifier %s is already defined in this scope", id);
        }
        environment.putSymbol(id, null);
    }

    private void get(final Identifier id) {
        if (!environment.getSymbol(id).present()) {
            System.err.printf("Identifier %s is undefined\n", id);
        }
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
        add(node.name); // functions add their name to the parent scope
        push();
        // functions add their parameters to their scope
        node.parameters.stream().map(Variable::name).forEach(this::add);
        visitChildren(node);
        pop();
        return null;
    }

    // declarations are the only other source of symbols in the table
    @Override
    public Void visitDeclaration(Declaration node) {
        add(node.variable.name());
        return super.visitDeclaration(node);
    }

    @Override
    public Void visitReference(Reference node) {
        get(node.variable);
        return super.visitReference(node);
    }

    @Override
    public Void visitCall(Call node) {
        get(node.function);
        node.arguments.forEach(this::get);
        return super.visitCall(node);
    }
}
