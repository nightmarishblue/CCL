package org.example.ast.statement;

import org.example.ast.condition.Condition;

import java.util.List;

public class While extends Statement {
    public final Condition condition;
    public final List<Statement> body;

    public While(Condition condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }
}
