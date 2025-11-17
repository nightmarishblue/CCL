package org.example;


import org.antlr.v4.runtime.ParserRuleContext;
import org.example.ast.*;
import org.example.ast.Main;
import org.example.ast.atom.Atom;
import org.example.ast.atom.Reference;
import org.example.ast.atom.literal.Boolean;
import org.example.ast.atom.literal.Integer;
import org.example.ast.condition.Compare;
import org.example.ast.condition.Condition;
import org.example.ast.condition.Logic;
import org.example.ast.condition.Not;
import org.example.ast.data.Identifier;
import org.example.ast.data.Type;
import org.example.ast.data.Variable;
import org.example.ast.declaration.Const;
import org.example.ast.declaration.Declaration;
import org.example.ast.declaration.Var;
import org.example.ast.expression.Arithmetic;
import org.example.ast.expression.Expression;
import org.example.ast.expression.Result;
import org.example.ast.expression.Value;
import org.example.ast.statement.*;
import org.example.grammar.CCLBaseVisitor;
import org.example.grammar.CCLParser;
import org.example.helper.Option;

import java.util.List;

public class AstBuilder extends CCLBaseVisitor<Node> {
    // blocks & functions
    @Override
    public Program visitProgram(CCLParser.ProgramContext ctx) {
        final List<Declaration> declarations = list(Declaration.class, ctx.declarationList().declaration());
        final List<Function> functions = list(Function.class, ctx.functionList().function());
        final Main main = visitMain(ctx.main());
        return new Program(declarations, functions, main);
    }

    @Override
    public Main visitMain(CCLParser.MainContext ctx) {
        final List<Declaration> declarations = list(Declaration.class, ctx.declarationList().declaration());
        final List<Statement> statements = list(Statement.class, ctx.statementList().statement());
        return new Main(declarations, statements);
    }

    @Override
    public Function visitFunction(CCLParser.FunctionContext ctx) {
        final Type type = Type.fromContext(ctx.type());
        final Identifier name = new Identifier(ctx.name);

        final List<Variable>  variables = ctx.parameterList().variable().stream().map(Variable::new).toList();

        final List<Declaration> declarations = list(Declaration.class, ctx.declarationList().declaration());
        final List<Statement> statements = list(Statement.class, ctx.statementList().statement());

        final Option<Expression> output = ctx.output != null
            ? Option.some(visitExpression(ctx.output))
            : Option.none();

        return new Function(type, name, variables, declarations, statements, output);
    }

    @Override
    public Call visitFunctionCall(CCLParser.FunctionCallContext ctx) {
        final Identifier function = new Identifier(ctx.name);
        final List<Identifier> arguments = ctx.argumentList().names.stream().map(Identifier::new).toList();
        return new Call(function, arguments);
    }

    // declarations
    public Declaration visitDeclaration(CCLParser.DeclarationContext ctx) {
        return (Declaration) visit(ctx);
    }

    @Override
    public Var visitVarDeclaration(CCLParser.VarDeclarationContext ctx) {
        final Variable variable = new Variable(ctx.variable());
        return new Var(variable);
    }

    @Override
    public Const visitConstDeclaration(CCLParser.ConstDeclarationContext ctx) {
        final Variable variable = new Variable(ctx.variable());
        final Expression value = visitExpression(ctx.expression());
        return new Const(variable, value);
    }

    // expressions
    public Expression visitExpression(CCLParser.ExpressionContext ctx) {
        return (Expression) visit(ctx);
    }

    @Override
    public Expression visitSubExpression(CCLParser.SubExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Arithmetic visitArithmeticExpression(CCLParser.ArithmeticExpressionContext ctx) {
        final Expression left = visitExpression(ctx.left);
        final Arithmetic.Operator operator = Arithmetic.Operator.fromToken(ctx.binaryArithmeticOperator().value);
        final Expression right = visitExpression(ctx.right);
        return new Arithmetic(left, operator, right);
    }

    @Override
    public Result visitFunctionCallExpression(CCLParser.FunctionCallExpressionContext ctx) {
        final Call call = visitFunctionCall(ctx.functionCall());
        return new Result(call);
    }

    @Override
    public Value visitAtomExpression(CCLParser.AtomExpressionContext ctx) {
        final Atom atom = visitAtom(ctx.atom());
        return new Value(atom);
    }

    // atoms
    public Atom visitAtom(CCLParser.AtomContext ctx) {
        return (Atom) visit(ctx);
    }

    @Override
    public Reference visitReferenceAtom(CCLParser.ReferenceAtomContext ctx) {
        final Identifier variable = new Identifier(ctx.name);
        final boolean negate = ctx.unaryOperator() != null; // currently the only unary operator allowed is minus
        return new Reference(variable, negate);
    }

    @Override
    public Node visitLiteralAtom(CCLParser.LiteralAtomContext ctx) {
        return super.visitLiteralAtom(ctx);
    }

    @Override
    public Boolean visitBooleanLiteral(CCLParser.BooleanLiteralContext ctx) {
        final int type = ctx.value.getType();
        final boolean value = switch (type) {
            case (CCLParser.KW_TRUE) -> true;
            case (CCLParser.KW_FALSE) -> false;
            default -> throw new IllegalArgumentException("Invalid token type " + type);
        };
        return new Boolean(value);
    }

    @Override
    public Integer visitIntegerLiteral(CCLParser.IntegerLiteralContext ctx) {
        final int value = java.lang.Integer.parseInt(ctx.value.getText());
        return new Integer(value);
    }

    // statements
    public Statement visitStatement(CCLParser.StatementContext ctx) {
        return (Statement) visit(ctx);
    }

    @Override
    public Assign visitAssignmentStatement(CCLParser.AssignmentStatementContext ctx) {
        final Identifier variable = new Identifier(ctx.var);
        final Expression value = visitExpression(ctx.expression());
        return new Assign(variable, value);
    }

    @Override
    public Discard visitFunctionCallStatement(CCLParser.FunctionCallStatementContext ctx) {
        final Call call = visitFunctionCall(ctx.functionCall());
        return new Discard(call);
    }

    @Override
    public Block visitNestedBlockStatement(CCLParser.NestedBlockStatementContext ctx) {
        final List<Statement> statements = list(Statement.class, ctx.statementBlock().statementList().statement());
        return new Block(statements);
    }

    @Override
    public IfElse visitIfStatement(CCLParser.IfStatementContext ctx) {
        final Condition condition = visitCondition(ctx.condition());
        final List<Statement> then = list(Statement.class, ctx.then.statementList().statement()),
                else_ = list(Statement.class, ctx.else_.statementList().statement());
        return new IfElse(condition, then, else_);
    }

    @Override
    public While visitWhileStatement(CCLParser.WhileStatementContext ctx) {
        final Condition condition = visitCondition(ctx.condition());
        final List<Statement> body = list(Statement.class, ctx.statementBlock().statementList().statement());
        return new While(condition, body);
    }

    @Override
    public Skip visitSkipStatement(CCLParser.SkipStatementContext ctx) {
        return new Skip();
    }

    // conditions
    public Condition visitCondition(CCLParser.ConditionContext ctx) {
        return (Condition) visit(ctx);
    }

    @Override
    public Condition visitSubCondition(CCLParser.SubConditionContext ctx) {
        return visitCondition(ctx.condition());
    }

    @Override
    public Logic visitCompoundCondition(CCLParser.CompoundConditionContext ctx) {
        final Condition left = visitCondition(ctx.left);
        final Logic.Operator operator = Logic.Operator.fromToken(ctx.logicalOperator().value);
        final Condition right = visitCondition(ctx.right);
        return new Logic(left, operator, right);
    }

    @Override
    public Not visitNegatedCondition(CCLParser.NegatedConditionContext ctx) {
        final Condition inner = visitCondition(ctx.condition());
        return new Not(inner);
    }

    @Override
    public Compare visitComparisonCondition(CCLParser.ComparisonConditionContext ctx) {
        final Expression left = visitExpression(ctx.left);
        final Compare.Operator operator = Compare.Operator.fromToken(ctx.comparisonOperator().value);
        final Expression right = visitExpression(ctx.right);
        return new Compare(left, operator, right);
    }

    // helper to convert a list of contexts to a list of nodes
    private <C extends ParserRuleContext, N extends Node> List<N>
    list(Class<N> type, List<C> contexts) {
        return contexts.stream().map(this::visit).map(type::cast).toList();
    }
}
