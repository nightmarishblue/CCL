package org.example.ast.node.statement;

import org.example.ast.node.Call;

// represents a function call whose result is discarded
public class Discard extends Statement {
    public final Call functionCall;

    public Discard(Call functionCall) {
        this.functionCall = functionCall;
    }
}
