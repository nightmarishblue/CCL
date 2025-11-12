package org.example.ast.expression;

import org.example.ast.Node;
import org.example.ast.atom.Atom;
import org.example.grammar.CCLParser;

public abstract class Expression extends Node {
    public Expression(CCLParser.ExpressionContext ctx) {
        super(ctx);
    }

    public static <T extends CCLParser.ExpressionContext> Expression fromContext(T ctx) {
        return switch (ctx) {
            // subexpressions are purely syntactic constructs, so they don't get their own class
            case CCLParser.SubExpressionContext ctx_ -> Expression.fromContext(ctx_.expression());
            case CCLParser.ArithmeticExpressionContext ctx_ -> Arithmetic.fromContext(ctx_);
            case CCLParser.FunctionCallExpressionContext ctx_ -> new Result(ctx_);
            case CCLParser.AtomExpressionContext ctx_ -> new Value(ctx_);
            case CCLParser.ExpressionContext ctx_ ->
                    throw new IllegalArgumentException(String.format("Subclass of %s is required", ctx_.getClass().getSimpleName()));
        };
    }
}
