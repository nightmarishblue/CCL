package org.example.ast.condition;

import org.antlr.v4.runtime.Token;
import org.example.ast.expression.Expression;
import org.example.grammar.CCLParser;

public class Compare extends Condition {
    public final Expression left;
    public final Operator operator;
    public final Expression right;

    public Compare(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s %s)", getClass().getSimpleName(), left, operator, right);
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        LESS_THAN,
        LESS_EQUAL,
        GREATER_THAN,
        GREATER_EQUAL;

        public static Operator fromToken(Token token) {
            final int type = token.getType();
            return switch (type) {
                case CCLParser.EQUALS -> EQUALS;
                case CCLParser.NOT_EQUALS -> NOT_EQUALS;
                case CCLParser.LESS_THAN -> LESS_THAN;
                case CCLParser.LESS_EQUAL -> LESS_EQUAL;
                case CCLParser.GREATER_THAN -> GREATER_THAN;
                case CCLParser.GREATER_EQUAL -> GREATER_EQUAL;
                default -> throw new IllegalArgumentException("Can't construct comparison operator from " + token);
            };
        }
    }
}
