package org.example.ast.atom.literal;

import org.example.ast.atom.Atom;
import org.example.grammar.CCLParser;

public abstract class Literal extends Atom {
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
