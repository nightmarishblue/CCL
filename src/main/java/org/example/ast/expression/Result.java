package org.example.ast.expression;

import org.example.ast.Call;
import org.example.grammar.CCLParser;

// this class represents invoking a function and using its result
public class Result extends Expression {
    public final Call functionCall;

    public Result(CCLParser.FunctionCallExpressionContext ctx) {
        super();
        functionCall = new Call(ctx.functionCall());
    }
}
