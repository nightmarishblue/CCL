package org.example.ast.atom.literal;

import org.example.grammar.CCLParser;

public class Boolean extends Literal {
    public final boolean value;

    public Boolean(CCLParser.BooleanLiteralContext ctx) {
        super(ctx);
        final int type = ctx.value.getType();
        value = switch (type) {
            case (CCLParser.KW_TRUE) -> true;
            case (CCLParser.KW_FALSE) -> false;
            default -> throw new IllegalArgumentException("Invalid token type " + type);
        };
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
