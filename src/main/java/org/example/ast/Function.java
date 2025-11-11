package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class Function extends Node {
    // TODO add type (probably enum)?
    Identifier name;
    List<Identifier> parameters;
    // TODO add the
    List<Declaration> declarations;

    public Function(ParserRuleContext ctx) {
        super(ctx);
    }
    // TODO add statements
    // TODO add output (expression)
}
