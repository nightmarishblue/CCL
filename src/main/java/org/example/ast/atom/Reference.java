package org.example.ast.atom;

import org.example.ast.data.Identifier;
import org.example.grammar.CCLParser;

public class Reference extends Atom {
    public final Identifier variable;
    // this is a field rather than separate Node, because recursive negation is disallowed by the grammar
    public final boolean negate;

    public Reference(CCLParser.ReferenceAtomContext ctx) {
        super();
        variable = new Identifier(ctx.name);
        negate = ctx.unaryOperator() != null; // currently the only unary operator allowed is minus
    }

    @Override
    public String toString() {
        return String.format("%s(%s%s)", getClass().getSimpleName(), negate ? " " : "", variable);
    }
}
