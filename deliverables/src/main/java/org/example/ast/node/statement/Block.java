package org.example.ast.node.statement;

import java.util.List;

// a compound statement block that adds no new scope, yet the spec mandates it anyway
public class Block extends Statement {
    public final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }
}
