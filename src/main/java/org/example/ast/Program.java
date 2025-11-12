package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.grammar.CCLParser;

import java.util.List;

public class Program extends Node {
    final List<Declaration> declarations;
    public final List<Function> functions;

    public final Main main;

    public Program(CCLParser.ProgramContext ctx) {
        super(ctx);
        declarations = ctx.declarationList().declaration().stream().map(Declaration::fromContext).toList();
        functions = ctx.functionList().function().stream().map(Function::new).toList();
        main = new Main(ctx.main());
    }
}
