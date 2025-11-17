package org.example.ast.statement;

import org.example.ast.Call;
import org.example.grammar.CCLParser;

// represents a function call whose result is discarded
public class Discard extends Statement {
    public final Call functionCall;

    public Discard(CCLParser.FunctionCallStatementContext ctx) {
        super();
        functionCall = new Call(ctx.functionCall());
    }
}
