package org.example.ast.condition;

import org.antlr.v4.runtime.Token;
import org.example.grammar.CCLParser;

public class Logic extends Condition {
    public final Condition left, right;
    public final Operator operator;

    public Logic(CCLParser.CompoundConditionContext ctx) {
        super(ctx);
        left = Condition.fromContext(ctx.left);
        operator = Operator.fromToken(ctx.logicalOperator().value);
        right = Condition.fromContext(ctx.right);
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s %s)", getClass().getSimpleName(), left, operator, right);
    }

    public enum Operator {
        AND, OR;

        public static Operator fromToken(final Token token) {
            final int type = token.getType();
            return switch (type) {
                case CCLParser.AND -> AND;
                case CCLParser.OR -> OR;
                default -> throw new IllegalArgumentException("Can't construct logical operator from " + token);
            };
        }
    }
}
