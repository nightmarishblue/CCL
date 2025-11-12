package org.example.ast.data;

import java.util.Locale;

public record Identifier(String value) {
    public Identifier(String value) {
        this.value = value.toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return value;
    }
}
