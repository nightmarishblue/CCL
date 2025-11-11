package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class Program extends Node {
    List<Declaration> declarations;
    List<Function> functions;

    Main main;

    public Program(ParserRuleContext ctx) {
        super(ctx);
    }
}
