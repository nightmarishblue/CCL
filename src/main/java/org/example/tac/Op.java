package org.example.tac;

// represents all possible operators in our 3AC dialect
public enum Op {
    PLUS, MINUS, TIMES, DIVIDE, // binary arithmetic
    AND, OR, // binary logical
    NOT; // unary logical

    public String symbol() {
        return switch (this) {
            case PLUS -> "+";
            case MINUS -> "-";
            case TIMES -> "*";
            case DIVIDE -> "/";

            case AND -> "&&";
            case OR -> "||";

            case NOT -> "~";
        };
    }
}
