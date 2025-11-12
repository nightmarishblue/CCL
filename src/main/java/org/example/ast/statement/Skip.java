package org.example.ast.statement;

import org.example.grammar.CCLParser;

// why does a language that allows empty blocks need a nop? I wish I knew
public class Skip extends Statement {
    public Skip(CCLParser.SkipStatementContext ctx) {
        super(ctx);
    }
}
