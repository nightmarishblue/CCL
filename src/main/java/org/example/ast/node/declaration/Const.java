package org.example.ast.node.declaration;

import org.example.ast.data.Variable;
import org.example.ast.node.expression.Expression;

public class Const extends Declaration {
    public final Expression value;

    public Const(Variable variable, Expression value) {
        super(variable);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s(%s=%s)", Const.class.getSimpleName(), variable, value);
    }
}
