package org.example.ast.expression;

import org.antlr.v4.runtime.Token;
import org.example.grammar.CCLParser;

public class Arithmetic extends Expression {
    public final Expression left;
    public final Operator operator;
    public final Expression right;

    public Arithmetic(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s %s)", this.getClass().getSimpleName(), left, operator, right);
    }

    public enum Operator {
        PLUS, MINUS;

        public static Operator fromToken(final Token token) {
            final int type = token.getType();
            return switch (type) {
                case CCLParser.PLUS -> PLUS;
                case CCLParser.MINUS -> MINUS;
                default -> throw new IllegalArgumentException("Can't construct arithmetic operator from " + token);
            };
        }
    }
}
