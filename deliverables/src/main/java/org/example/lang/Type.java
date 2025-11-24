package org.example.lang;

import org.antlr.v4.runtime.Token;
import org.example.grammar.CCLParser;

public enum Type {
    VOID, INTEGER, BOOLEAN, ANY, NONE;

    public static boolean same(Type left, Type right) {
        if (left == ANY || right == ANY) return true;
        if (left == NONE || right == NONE) return false; // had to try out a bottom type
        return left == right;
    }

    public boolean is(Type other) {
        return Type.same(this, other);
    }

    public static Type fromToken(final Token token) {
        int index = token.getType();
        return switch (index) {
            case CCLParser.KW_VOID -> VOID;
            case CCLParser.KW_INTEGER -> INTEGER;
            case CCLParser.KW_BOOLEAN -> BOOLEAN;
            default -> throw new IllegalArgumentException(String.format("Token %s does not represent a CCL type", token));
        };
    }

    public static Type fromContext(final CCLParser.TypeContext context) {
        return fromToken(context.value);
    }
}
