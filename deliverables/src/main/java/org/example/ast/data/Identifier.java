package org.example.ast.data;

import org.antlr.v4.runtime.Token;

import java.util.Locale;

public record Identifier(String value) {
    public Identifier(String value) {
        this.value = value.toUpperCase(Locale.ROOT);
    }

    public Identifier(Token token) {
        this(token.getText());
    }

    @Override
    public String toString() {
        return value;
    }
}
