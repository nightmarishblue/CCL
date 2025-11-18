package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.node.Function;
import org.example.ast.node.Main;
import org.example.ast.node.Node;
import org.example.ast.node.Program;
import org.example.helper.Option;

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
        environment.putSymbol(id, null);
    }

    private Option<Void> get(final Identifier id) {
        return environment.getSymbol(id);
    }

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
        wrapScope(node);
        return null;
    }
}
