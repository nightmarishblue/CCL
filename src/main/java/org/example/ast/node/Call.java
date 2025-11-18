package org.example.ast.node;

import org.example.ast.data.Identifier;

import java.util.List;

public class Call extends Node {
    public final Identifier function;
    public final List<Identifier> arguments;

    public Call(Identifier function, List<Identifier> arguments) {
        this.function = function;
        this.arguments = arguments;
    }
}
