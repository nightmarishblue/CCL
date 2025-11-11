package org.example.ast;

import java.util.Locale;

public class Identifier {
    public final String value;

    public Identifier(String name) {
        value = name.toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Identifier identifier)) return false;
        return value.equals(identifier.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
