package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.ast.statement.Statement;

import java.util.List;

public class Main extends Node {
    public final List<Declaration> declarations;
    public final List<Statement> statements;

    public Main(List<Declaration> declarations, List<Statement> statements) {
        this.declarations = declarations;
        this.statements = statements;
    }
}
