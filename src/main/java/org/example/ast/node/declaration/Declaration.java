package org.example.ast.node.declaration;

import org.example.ast.node.Node;
import org.example.ast.data.Variable;

public abstract class Declaration extends Node {
    public final Variable variable;

    public Declaration(Variable variable) {
        this.variable = variable;
    }
}
