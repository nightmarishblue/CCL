package org.example.ast;

import org.example.grammar.CCLParser;

public abstract class Declaration extends Node {
    final Identifier name;
    final Type type;

    public Declaration(CCLParser.DeclarationContext ctx) {
        super(ctx);
        final CCLParser.VariableContext variable = ctx.getRuleContext(CCLParser.VariableContext.class, 0);

        name = new Identifier(variable.name.getText());
        type = Type.fromContext(variable.type());
    }
    // TODO add subclasses for const and var declarations?
}
