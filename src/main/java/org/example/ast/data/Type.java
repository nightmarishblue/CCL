package org.example.ast.data;

import org.antlr.v4.runtime.Token;
import org.example.grammar.CCLParser;

public enum Type {
    VOID,
    INTEGER,
    BOOLEAN;

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
