package org.example.ast.statement;

import org.example.grammar.CCLParser;

import java.util.List;

public class While extends Statement {
    // public final Condition condition; // TODO
    public final List<Statement> body;

    public While(CCLParser.WhileStatementContext ctx) {
        super(ctx);
        body = ctx.statementBlock().statementList().statement()
                .stream().map(Statement::fromContext).toList();
    }
}
