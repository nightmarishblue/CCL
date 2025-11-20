package org.example.ast.data;

import org.example.grammar.CCLParser;
import org.example.lang.Type;

public record Variable(Identifier name, Type type) {
    public Variable(CCLParser.VariableContext ctx) {
        this(new Identifier(ctx.name.getText()), Type.fromContext(ctx.type()));
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, type);
    }
}
