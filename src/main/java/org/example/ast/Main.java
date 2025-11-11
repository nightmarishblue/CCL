package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.grammar.CCLParser;

import java.util.List;

public class Main extends Node {
    List<Declaration> declarations;

    public Main(CCLParser.MainContext ctx) {
        super(ctx);
        declarations = ctx.declarationList().declaration().stream().map(Declaration::fromContext).toList();
    }
    // TODO add statements
}
