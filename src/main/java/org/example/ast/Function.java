package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.grammar.CCLParser;

import java.util.List;

public class Function extends Node {
    final Type type;
    final Identifier name;
    final List<Variable> parameters;
    final List<Declaration> declarations;

    public Function(CCLParser.FunctionContext ctx) {
        super(ctx);
        type = Type.fromContext(ctx.type());
        name = new Identifier(ctx.name.getText());
        parameters = ctx.parameterList().variable().stream().map(Variable::new).toList();
        declarations = ctx.declarationList().declaration().stream().map(Declaration::fromContext).toList();
    }
    // TODO add statements
    // TODO add output (expression)
}
