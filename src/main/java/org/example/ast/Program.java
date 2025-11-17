package org.example.ast;

import org.example.ast.declaration.Declaration;

import java.util.List;

public class Program extends Node {
    public final List<Declaration> declarations;
    public final List<Function> functions;

    public final Main main;

    public Program(List<Declaration> declarations, List<Function> functions, Main main) {
        this.declarations = declarations;
        this.functions = functions;
        this.main = main;
    }
}
