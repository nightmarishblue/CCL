package org.example.ast.declaration;

import org.example.grammar.CCLParser;

public class Var extends Declaration {
    public Var(CCLParser.VarDeclarationContext ctx) {
        super(ctx);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", Var.class.getSimpleName(), variable);
    }
}
