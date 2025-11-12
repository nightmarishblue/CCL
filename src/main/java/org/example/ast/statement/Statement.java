package org.example.ast.statement;

import org.example.ast.Node;
import org.example.grammar.CCLParser;

public abstract class Statement extends Node {
    public Statement(CCLParser.StatementContext ctx) {
        super(ctx);
    }

    public static <T extends CCLParser.StatementContext> Statement fromContext(T ctx) {
        return switch (ctx) {
            case CCLParser.NestedBlockStatementContext ctx_ -> new Block(ctx_);
            case CCLParser.AssignmentStatementContext ctx_ -> new Assign(ctx_);
            case CCLParser.FunctionCallStatementContext ctx_ -> new Discard(ctx_);
            case CCLParser.StatementContext ctx_ -> null; // TODO replace with a throw
        };
    }
}
