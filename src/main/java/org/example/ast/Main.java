package org.example.ast;

import org.example.ast.declaration.Declaration;
import org.example.ast.statement.Statement;
import org.example.grammar.CCLParser;

import java.util.List;

public class Main extends Node {
    public final List<Declaration> declarations;
    public final List<Statement> statements;

    public Main(CCLParser.MainContext ctx) {
        super(ctx);
        declarations = ctx.declarationList().declaration().stream().map(Declaration::fromContext).toList();
        statements = ctx.statementList().statement().stream().map(Statement::fromContext).toList();
    }
}
