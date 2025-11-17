package org.example.ast;

import org.example.ast.data.Identifier;
import org.example.ast.data.Type;
import org.example.ast.data.Variable;
import org.example.ast.declaration.Declaration;
import org.example.ast.expression.Expression;
import org.example.ast.statement.Statement;
import org.example.grammar.CCLParser;
import org.example.helper.Option;

import java.util.List;

public class Function extends Node {
    public final Type type;
    public final Identifier name;

    public final List<Variable> parameters;

    public final List<Declaration> declarations;
    public final List<Statement> statements;

    public final Option<Expression> output;

    public Function(CCLParser.FunctionContext ctx) {
        type = Type.fromContext(ctx.type());
        name = new Identifier(ctx.name.getText());

        parameters = ctx.parameterList().variable().stream().map(Variable::new).toList();

        declarations = ctx.declarationList().declaration().stream().map(Declaration::fromContext).toList();
        statements = ctx.statementList().statement().stream().map(Statement::fromContext).toList();

        output = ctx.output != null
                ? Option.some(Expression.fromContext(ctx.output))
                : Option.none();
    }
}
