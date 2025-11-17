package org.example.ast.declaration;

import org.example.ast.Node;
import org.example.ast.data.Variable;
import org.example.grammar.CCLParser;

public abstract class Declaration extends Node {
    public final Variable variable;

    public Declaration(final CCLParser.DeclarationContext ctx) {
        final CCLParser.VariableContext variableCtx = ctx.getRuleContext(CCLParser.VariableContext.class, 0);
        variable = new Variable(variableCtx);
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
