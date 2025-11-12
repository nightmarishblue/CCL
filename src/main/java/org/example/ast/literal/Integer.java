package org.example.ast.literal;

import org.example.grammar.CCLParser;

public class Integer extends Literal {
    public final int value;

    public Integer(CCLParser.IntegerLiteralContext ctx) {
        super(ctx);
        value = java.lang.Integer.parseInt(ctx.value.getText());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
