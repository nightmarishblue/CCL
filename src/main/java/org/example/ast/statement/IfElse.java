package org.example.ast.statement;

import org.example.ast.condition.Condition;

import java.util.List;

public class IfElse extends Statement {
    public final Condition condition;
    public final List<Statement> then, else_;

    public IfElse(Condition condition, List<Statement> then, List<Statement> else_) {
        this.condition = condition;
        this.then = then;
        this.else_ = else_;
    }
}
