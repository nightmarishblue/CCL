package org.example.ast.declaration;

import org.example.ast.data.Variable;

public class Var extends Declaration {
    public Var(Variable variable) {
        super(variable);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", Var.class.getSimpleName(), variable);
    }
}
