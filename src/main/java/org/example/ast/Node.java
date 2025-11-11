package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Node {
    public final int position; // or something?
    public Node(final ParserRuleContext ctx) {
        position = ctx.getStart().getStartIndex();
    }
}
