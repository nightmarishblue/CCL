package org.example.ast.expression;

import org.example.ast.atom.Atom;
import org.example.grammar.CCLParser;

public class Value extends Expression {
    public final Atom atom;

    public Value(CCLParser.AtomExpressionContext ctx) {
        super();
        atom = Atom.fromContext(ctx.atom());
    }
}
