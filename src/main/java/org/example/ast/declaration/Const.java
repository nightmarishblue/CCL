package org.example.ast.declaration;

import org.example.ast.expression.Expression;
import org.example.grammar.CCLParser;

public class Const extends Declaration {
    final Expression value;

    public Const(CCLParser.ConstDeclarationContext ctx) {
        super(ctx);
        value = Expression.fromContext(ctx.expression());
    }

    @Override
    public String toString() {
        return String.format("%s(%s=%s)", Const.class.getSimpleName(), variable, value);
    }
}
