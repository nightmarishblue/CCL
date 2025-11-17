package org.example.ast.statement;

import org.example.ast.condition.Condition;
import org.example.grammar.CCLParser;

import java.util.List;

public class While extends Statement {
    public final Condition condition;
    public final List<Statement> body;

    public While(CCLParser.WhileStatementContext ctx) {
        super();
        condition = Condition.fromContext(ctx.condition());
        body = ctx.statementBlock().statementList().statement()
                .stream().map(Statement::fromContext).toList();
    }
}
