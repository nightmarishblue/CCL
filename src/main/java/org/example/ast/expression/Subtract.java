package org.example.ast.expression;

import org.example.grammar.CCLParser;

public class Subtract extends Arithmetic {
    public Subtract(CCLParser.ArithmeticExpressionContext ctx) {
        super(ctx);
    }
}
