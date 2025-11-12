package org.example.ast.statement;

import org.example.grammar.CCLParser;

import java.util.List;

// a compound statement block that adds no new scope, yet the spec mandates it anyway
public class Block extends Statement {
    public final List<Statement> statements;

    public Block(CCLParser.NestedBlockStatementContext ctx) {
        super(ctx);
        statements = ctx.statementBlock().statementList().statement()
                .stream().map(Statement::fromContext).toList();
    }
}
