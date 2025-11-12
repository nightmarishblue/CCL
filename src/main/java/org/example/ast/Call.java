package org.example.ast;

import org.example.ast.data.Identifier;
import org.example.grammar.CCLParser;

import java.util.List;

public class Call extends Node {
    final Identifier function;
    final List<Identifier> arguments;

    public Call(CCLParser.FunctionCallContext ctx) {
        super(ctx);
        function = new Identifier(ctx.name);
        arguments = ctx.argumentList().names.stream().map(Identifier::new).toList();
    }
}
