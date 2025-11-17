package org.example.ast.atom;

import org.example.ast.data.Identifier;

public class Reference extends Atom {
    public final Identifier variable;
    // this is a field rather than separate Node, because recursive negation is disallowed by the grammar
    public final boolean negate;

    public Reference(Identifier variable, boolean negate) {
        this.variable = variable;
        this.negate = negate;
    }

    @Override
    public String toString() {
        return String.format("%s(%s%s)", getClass().getSimpleName(), negate ? " " : "", variable);
    }
}
