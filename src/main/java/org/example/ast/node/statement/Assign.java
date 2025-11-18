package org.example.ast.node.statement;

import org.example.ast.data.Identifier;
import org.example.ast.node.expression.Expression;

public class Assign extends Statement {
    public final Identifier variable;
    public final Expression value;

    public Assign(Identifier variable, Expression value) {
        this.variable = variable;
        this.value = value;
    }
}
