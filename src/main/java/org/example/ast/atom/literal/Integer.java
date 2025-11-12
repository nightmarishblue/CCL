package org.example.ast.atom.literal;

import org.example.grammar.CCLParser;

public class Integer extends Literal {
    public final int value;

    public Integer(CCLParser.IntegerLiteralContext ctx) {
        super(ctx);
        value = java.lang.Integer.parseInt(ctx.value.getText());
    }
}
