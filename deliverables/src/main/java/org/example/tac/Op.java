package org.example.tac;

import org.example.ast.node.condition.Compare;
import org.example.ast.node.condition.Logic;
import org.example.ast.node.expression.Arithmetic;

// represents all possible operators in our 3AC dialect
public enum Op {
    COPY,

    PLUS, MINUS, TIMES, DIVIDE, // binary arithmetic
    AND, OR, // binary logical
    NOT, // unary logical

    EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL, // comparison

    // control flow
    LABEL, GOTO, // really stretching the definition of "operator" here guys

    // functions
    RETURN, // this one can have no arguments...
    PARAM, GETPARAM,
    CALL,
    ;

    public String symbol() {
        return switch (this) {
            case COPY, LABEL -> "";

            case GOTO -> "goto";
            case RETURN -> "return";
            case PARAM -> "param";
            case GETPARAM -> "getparam";
            case CALL -> "call";


            case PLUS -> "+";
            case MINUS -> "-";
            case TIMES -> "*";
            case DIVIDE -> "/";

            case AND -> "&&";
            case OR -> "||";

            case NOT -> "~"; // don't actually know if this is the symbol, the manual was unclear

            case EQUALS -> "==";
            case NOT_EQUALS -> "!=";
            case LESS_THAN -> "<";
            case LESS_EQUAL -> "<=";
            case GREATER_THAN -> ">";
            case GREATER_EQUAL -> ">=";
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

    public static Op comparison(Compare.Operator operator) {
        return switch (operator) {
            case EQUALS -> EQUALS;
            case NOT_EQUALS -> NOT_EQUALS;
            case LESS_THAN -> LESS_THAN;
            case LESS_EQUAL -> LESS_EQUAL;
            case GREATER_THAN -> GREATER_THAN;
            case GREATER_EQUAL -> GREATER_EQUAL;
        };
    }
}
