package org.example.ast.statement;

import org.example.ast.data.Identifier;
import org.example.ast.expression.Expression;
import org.example.grammar.CCLParser;

public class Assign extends Statement {
    public final Identifier variable;
    public final Expression value;

    public Assign(CCLParser.AssignmentStatementContext ctx) {
        super(ctx);
        variable = new Identifier(ctx.var);
        value = Expression.fromContext(ctx.expression());
    }
}
