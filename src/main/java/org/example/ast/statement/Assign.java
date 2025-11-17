package org.example.ast.statement;

import org.example.ast.data.Identifier;
import org.example.ast.expression.Expression;

public class Assign extends Statement {
    public final Identifier variable;
    public final Expression value;

    public Assign(Identifier variable, Expression value) {
        this.variable = variable;
        this.value = value;
    }
}
