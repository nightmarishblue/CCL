package org.example.ast.statement;

import org.example.ast.condition.Condition;
import org.example.grammar.CCLParser;

import java.util.List;

public class IfElse extends Statement {
    public final Condition condition;
    public final List<Statement> then, else_;

    public IfElse(CCLParser.IfStatementContext ctx) {
        super(ctx);
        condition = Condition.fromContext(ctx.condition());
        then = ctx.then.statementList().statement()
                .stream().map(Statement::fromContext).toList();
        else_ = ctx.else_.statementList().statement()
                .stream().map(Statement::fromContext).toList();
    }
}
