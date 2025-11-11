package org.example.ast.declaration;

import org.example.grammar.CCLParser;

public class Const extends Declaration {
    // TODO add the expression on the other side of the =
    public Const(CCLParser.ConstDeclarationContext ctx) {
        super(ctx);
    }

    @Override
    public String toString() {
        return String.format("%s(%s:%s=)", Const.class.getSimpleName(), this.name, this.type);
    }
}
