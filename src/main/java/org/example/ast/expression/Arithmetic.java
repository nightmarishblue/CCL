package org.example.ast.expression;

import org.example.grammar.CCLParser;

public abstract class Arithmetic extends Expression {
    public final Expression left, right;

    public Arithmetic(CCLParser.ArithmeticExpressionContext ctx) {
        super(ctx);
        left = Expression.fromContext(ctx.left);
        right = Expression.fromContext(ctx.right);
    }

    public static Arithmetic fromContext(CCLParser.ArithmeticExpressionContext ctx) {
        // it may have been a mistake to merge both arithmetic operations into the same parser rule
        final int type = ctx.binaryArithmeticOperator().value.getType();
        return switch (type) {
            case (CCLParser.PLUS) -> new Add(ctx);
            case (CCLParser.MINUS) -> new Subtract(ctx);
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", this.getClass().getSimpleName(), left, right);
    }
}
