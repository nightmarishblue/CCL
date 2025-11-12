package org.example.ast.expression;

import org.example.grammar.CCLParser;

public class Add extends Arithmetic {
    public Add(CCLParser.ArithmeticExpressionContext ctx) {
        super(ctx);
    }
}
