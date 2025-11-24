package org.example.ast.node.expression;

import org.example.ast.node.atom.Atom;

public class Value extends Expression {
    public final Atom atom;

    public Value(Atom atom) {
        this.atom = atom;
    }
}
