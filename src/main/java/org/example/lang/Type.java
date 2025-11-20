package org.example.lang;

public enum Type {
    VOID, INTEGER, BOOLEAN, ANY, NONE;

    public static Type of(org.example.ast.data.Type nativeType) {
        return switch (nativeType) {
            case VOID -> VOID;
            case INTEGER -> INTEGER;
            case BOOLEAN -> BOOLEAN;
        };
    }

    public static boolean same(Type left, Type right) {
        if (left == ANY || right == ANY) return true;
        if (left == NONE || right == NONE) return false; // had to try out a bottom type
        return left == right;
    }

    public boolean is(Type other) {
        return Type.same(this, other);
    }

    public boolean is(org.example.ast.data.Type other) {
        return Type.same(this, Type.of(other));
    }
}
