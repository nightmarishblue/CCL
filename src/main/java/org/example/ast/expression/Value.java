package org.example.ast.expression;

import org.example.ast.atom.Atom;

public class Value extends Expression {
    public final Atom atom;

    public Value(Atom atom) {
        this.atom = atom;
    }
}
