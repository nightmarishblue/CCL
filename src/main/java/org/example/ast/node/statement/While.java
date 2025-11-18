package org.example.ast.node.statement;

import org.example.ast.node.condition.Condition;

import java.util.List;

public class While extends Statement {
    public final Condition condition;
    public final List<Statement> body;

    public While(Condition condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }
}
