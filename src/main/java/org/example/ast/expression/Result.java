package org.example.ast.expression;

import org.example.ast.Call;

// this class represents invoking a function and using its result
public class Result extends Expression {
    public final Call functionCall;

    public Result(Call functionCall) {
        this.functionCall = functionCall;
    }
}
