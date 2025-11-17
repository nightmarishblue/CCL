package org.example.ast.statement;

import org.example.ast.Call;

// represents a function call whose result is discarded
public class Discard extends Statement {
    public final Call functionCall;

    public Discard(Call functionCall) {
        this.functionCall = functionCall;
    }
}
