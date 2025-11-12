package org.example.ast.condition;

import org.example.grammar.CCLParser;

public class Not extends Condition {
    public final Condition inner;

    public Not(CCLParser.NegatedConditionContext ctx) {
        super(ctx);
        inner = Condition.fromContext(ctx.condition());
    }
}
