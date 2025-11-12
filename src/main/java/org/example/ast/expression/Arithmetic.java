package org.example.ast.expression;

import org.example.grammar.CCLParser;

public abstract class Arithmetic extends Expression {
    final Expression left, right;

    public Arithmetic(CCLParser.ArithmeticExpressionContext ctx) {
        super(ctx);
        left = Expression.fromContext(ctx);
        right = Expression.fromContext(ctx);
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", this.getClass().getSimpleName(), left, right);
    }
}
