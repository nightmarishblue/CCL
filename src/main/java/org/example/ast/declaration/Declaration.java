package org.example.ast.declaration;

import org.example.ast.Identifier;
import org.example.ast.Node;
import org.example.ast.Type;
import org.example.grammar.CCLParser;

public abstract class Declaration extends Node {
    final Identifier name;
    final Type type;

    public Declaration(final CCLParser.DeclarationContext ctx) {
        super(ctx);
        final CCLParser.VariableContext variable = ctx.getRuleContext(CCLParser.VariableContext.class, 0);

        name = new Identifier(variable.name.getText());
        type = Type.fromContext(variable.type());
    }

    public static Declaration fromContext(final CCLParser.DeclarationContext ctx) {
        return switch (ctx) {
            case CCLParser.VarDeclarationContext varCtx -> new Var(varCtx);
            case CCLParser.ConstDeclarationContext constCtx -> new Const(constCtx);
            case CCLParser.DeclarationContext ignored ->
                    throw new IllegalArgumentException("DeclarationContext can't be passed to fromContext");
        };
    }
}
