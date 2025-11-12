package org.example.ast.expression;

import org.example.ast.Node;
import org.example.grammar.CCLParser;

public abstract class Expression extends Node {
    public Expression(CCLParser.ExpressionContext ctx) {
        super(ctx);
    }

    // nested expressions are only syntactically useful, not semantic - so they don't get their own class
    public Expression(CCLParser.SubExpressionContext ctx) {
        this(ctx.expression());
    }
}
