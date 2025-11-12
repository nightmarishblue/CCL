package org.example.ast.condition;

import org.example.ast.Node;
import org.example.grammar.CCLParser;

public abstract class Condition extends Node {
    public Condition(CCLParser.ConditionContext ctx) {
        super(ctx);
    }

    public static <T extends CCLParser.ConditionContext> Condition fromContext(T ctx) {
        return switch (ctx) {
            // much like subexpressions, subconditions are syntax we can strip away
            case CCLParser.SubConditionContext ctx_ -> Condition.fromContext(ctx_.condition());
            case CCLParser.NegatedConditionContext ctx_ -> new Not(ctx_);
            case CCLParser.ConditionContext ctx_ -> null;
//                throw new IllegalArgumentException(String.format("Subclass of %s is required", ctx_.getClass().getSimpleName()));
        };
    }
}
