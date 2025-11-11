package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.grammar.CCLParser;

import java.util.List;

public class Program extends Node {
    final List<Declaration> declarations;
    List<Function> functions;

    Main main;

    public Program(CCLParser.ProgramContext ctx) {
        super(ctx);
        declarations = ctx.declarationList().declaration().stream().map(Declaration::new).toList();
    }
}
