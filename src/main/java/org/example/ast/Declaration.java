package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Declaration extends Node {
    public Declaration(ParserRuleContext ctx) {
        super(ctx);
    }
    // TODO add subclasses for const and var declarations?
}
