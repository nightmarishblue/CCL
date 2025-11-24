package org.example.ast.node;

import org.example.ast.data.Identifier;
import org.example.lang.Type;
import org.example.ast.data.Variable;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.expression.Expression;
import org.example.ast.node.statement.Statement;
import org.example.helper.Option;

import java.util.List;

public class Function extends Node {
    public final Type type;
    public final Identifier name;

    public final List<Variable> parameters;

    public final List<Declaration> declarations;
    public final List<Statement> statements;

    public final Option<Expression> output;

    public Function(Type type, Identifier name, List<Variable> parameters, List<Declaration> declarations, List<Statement> statements, Option<Expression> output) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.declarations = declarations;
        this.statements = statements;
        this.output = output;
    }
}
