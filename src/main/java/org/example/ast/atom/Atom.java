package org.example.ast.atom;

import org.example.ast.Node;
import org.example.ast.atom.literal.Literal;
import org.example.grammar.CCLParser;

public abstract class Atom extends Node {
    public Atom() {
    }

    public static <T extends CCLParser.AtomContext> Atom fromContext(T ctx) {
        return switch (ctx) {
            case CCLParser.ReferenceAtomContext ctx_ -> new Reference(ctx_);
            case CCLParser.LiteralAtomContext ctx_ -> Literal.fromContext(ctx_.literal());
            case CCLParser.AtomContext ctx_ ->
                 throw new IllegalArgumentException(String.format("Subclass of %s is required", ctx_.getClass().getSimpleName()));
        };
    }
}
