package org.example.ast.literal;

import org.example.ast.Node;
import org.example.grammar.CCLParser;

public abstract class Literal extends Node {
    public Literal(CCLParser.LiteralContext ctx) {
        super(ctx);
    }

    public static <T extends CCLParser.LiteralContext> Literal fromContext(T ctx) {
        return switch (ctx) {
            case CCLParser.BooleanLiteralContext ctx_ -> new Boolean(ctx_);
            case CCLParser.IntegerLiteralContext ctx_ -> new Integer(ctx_);
            default -> throw new IllegalArgumentException("Invalid context " + ctx);
        };
    }
}
