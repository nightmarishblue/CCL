package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.ast.declaration.Declaration;

import java.util.List;

public class Main extends Node {
    List<Declaration> declarations;

    public Main(ParserRuleContext ctx) {
        super(ctx);
    }
    // TODO add statements
}
