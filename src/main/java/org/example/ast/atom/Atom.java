package org.example.ast.atom;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.ast.Node;
import org.example.ast.atom.literal.Literal;
import org.example.grammar.CCLParser;

public abstract class Atom extends Node {
    // can't be AtomContext because that would break the Literal's constructor
    // luckily, we don't use AtomContext for anything, but it's a good example of how OOP doesn't really work
    public Atom(ParserRuleContext ctx) {
        super(ctx);
    }

    public static <T extends CCLParser.AtomContext> Atom fromContext(T ctx) {
        return switch (ctx) {
            case CCLParser.ReferenceAtomContext ctx_ -> new Reference(ctx_);
            case CCLParser.LiteralAtomContext ctx_ -> Literal.fromContext(ctx_.literal());
            default -> throw new IllegalArgumentException("Can't construct Atom from " + ctx);
        };
    }
}
