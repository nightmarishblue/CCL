package org.example.tac;

import org.example.ast.node.condition.Logic;
import org.example.ast.node.expression.Arithmetic;

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

    public static Op arithmetic(Arithmetic.Operator operator) {
        return switch (operator) {
            case PLUS -> PLUS;
            case MINUS -> MINUS;
        };
    }

    public static Op logical(Logic.Operator operator) {
        return switch (operator) {
            case AND -> AND;
            case OR -> OR;
        };
    }
}
