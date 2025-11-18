package org.example.antlr;


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
import java.util.function.BiConsumer;

public class AstBuilder extends CCLBaseVisitor<Node> {
    private final Option<BiConsumer<Node, ParserRuleContext>> onNodeCreated;

    public AstBuilder(BiConsumer<Node, ParserRuleContext> onNodeCreated) {
        this.onNodeCreated = Option.some(onNodeCreated);
    }

    public AstBuilder() {
        this.onNodeCreated = Option.none();
    }


    // blocks & functions
    @Override
    public Program visitProgram(CCLParser.ProgramContext ctx) {
        final List<Declaration> declarations = list(Declaration.class, ctx.declarationList().declaration());
        final List<Function> functions = list(Function.class, ctx.functionList().function());
        final Main main = visitMain(ctx.main());
        return callback(new Program(declarations, functions, main), ctx);
    }

    @Override
    public Main visitMain(CCLParser.MainContext ctx) {
        final List<Declaration> declarations = list(Declaration.class, ctx.declarationList().declaration());
        final List<Statement> statements = list(Statement.class, ctx.statementList().statement());
        return callback(new Main(declarations, statements), ctx);
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

        return callback(new Function(type, name, variables, declarations, statements, output), ctx);
    }

    @Override
    public Call visitFunctionCall(CCLParser.FunctionCallContext ctx) {
        final Identifier function = new Identifier(ctx.name);
        final List<Identifier> arguments = ctx.argumentList().names.stream().map(Identifier::new).toList();
        return callback(new Call(function, arguments), ctx);
    }

    // declarations
    public Declaration visitDeclaration(CCLParser.DeclarationContext ctx) {
        return callback((Declaration) visit(ctx), ctx);
    }

    @Override
    public Var visitVarDeclaration(CCLParser.VarDeclarationContext ctx) {
        final Variable variable = new Variable(ctx.variable());
        return callback(new Var(variable), ctx);
    }

    @Override
    public Const visitConstDeclaration(CCLParser.ConstDeclarationContext ctx) {
        final Variable variable = new Variable(ctx.variable());
        final Expression value = visitExpression(ctx.expression());
        return callback(new Const(variable, value), ctx);
    }

    // expressions
    public Expression visitExpression(CCLParser.ExpressionContext ctx) {
        return callback((Expression) visit(ctx), ctx);
    }

    @Override
    public Expression visitSubExpression(CCLParser.SubExpressionContext ctx) {
        return callback(visitExpression(ctx.expression()), ctx);
    }

    @Override
    public Arithmetic visitArithmeticExpression(CCLParser.ArithmeticExpressionContext ctx) {
        final Expression left = visitExpression(ctx.left);
        final Arithmetic.Operator operator = Arithmetic.Operator.fromToken(ctx.binaryArithmeticOperator().value);
        final Expression right = visitExpression(ctx.right);
        return callback(new Arithmetic(left, operator, right), ctx);
    }

    @Override
    public Result visitFunctionCallExpression(CCLParser.FunctionCallExpressionContext ctx) {
        final Call call = visitFunctionCall(ctx.functionCall());
        return callback(new Result(call), ctx);
    }

    @Override
    public Value visitAtomExpression(CCLParser.AtomExpressionContext ctx) {
        final Atom atom = visitAtom(ctx.atom());
        return callback(new Value(atom), ctx);
    }

    // atoms
    public Atom visitAtom(CCLParser.AtomContext ctx) {
        return callback((Atom) visit(ctx), ctx);
    }

    @Override
    public Reference visitReferenceAtom(CCLParser.ReferenceAtomContext ctx) {
        final Identifier variable = new Identifier(ctx.name);
        final boolean negate = ctx.unaryOperator() != null; // currently the only unary operator allowed is minus
        return callback(new Reference(variable, negate), ctx);
    }

    @Override
    public Node visitLiteralAtom(CCLParser.LiteralAtomContext ctx) {
        return callback(super.visitLiteralAtom(ctx), ctx);
    }

    @Override
    public Boolean visitBooleanLiteral(CCLParser.BooleanLiteralContext ctx) {
        final int type = ctx.value.getType();
        final boolean value = switch (type) {
            case (CCLParser.KW_TRUE) -> true;
            case (CCLParser.KW_FALSE) -> false;
            default -> throw new IllegalArgumentException("Invalid token type " + type);
        };
        return callback(new Boolean(value), ctx);
    }

    @Override
    public Integer visitIntegerLiteral(CCLParser.IntegerLiteralContext ctx) {
        final int value = java.lang.Integer.parseInt(ctx.value.getText());
        return callback(new Integer(value), ctx);
    }

    // statements
    public Statement visitStatement(CCLParser.StatementContext ctx) {
        return callback((Statement) visit(ctx), ctx);
    }

    @Override
    public Assign visitAssignmentStatement(CCLParser.AssignmentStatementContext ctx) {
        final Identifier variable = new Identifier(ctx.var);
        final Expression value = visitExpression(ctx.expression());
        return callback(new Assign(variable, value), ctx);
    }

    @Override
    public Discard visitFunctionCallStatement(CCLParser.FunctionCallStatementContext ctx) {
        final Call call = visitFunctionCall(ctx.functionCall());
        return callback(new Discard(call), ctx);
    }

    @Override
    public Block visitNestedBlockStatement(CCLParser.NestedBlockStatementContext ctx) {
        final List<Statement> statements = list(Statement.class, ctx.statementBlock().statementList().statement());
        return callback(new Block(statements), ctx);
    }

    @Override
    public IfElse visitIfStatement(CCLParser.IfStatementContext ctx) {
        final Condition condition = visitCondition(ctx.condition());
        final List<Statement> then = list(Statement.class, ctx.then.statementList().statement()),
                else_ = list(Statement.class, ctx.else_.statementList().statement());
        return callback(new IfElse(condition, then, else_), ctx);
    }

    @Override
    public While visitWhileStatement(CCLParser.WhileStatementContext ctx) {
        final Condition condition = visitCondition(ctx.condition());
        final List<Statement> body = list(Statement.class, ctx.statementBlock().statementList().statement());
        return callback(new While(condition, body), ctx);
    }

    @Override
    public Skip visitSkipStatement(CCLParser.SkipStatementContext ctx) {
        return callback(new Skip(), ctx);
    }

    // conditions
    public Condition visitCondition(CCLParser.ConditionContext ctx) {
        return callback((Condition) visit(ctx), ctx);
    }

    @Override
    public Condition visitSubCondition(CCLParser.SubConditionContext ctx) {
        return callback(visitCondition(ctx.condition()), ctx);
    }

    @Override
    public Logic visitCompoundCondition(CCLParser.CompoundConditionContext ctx) {
        final Condition left = visitCondition(ctx.left);
        final Logic.Operator operator = Logic.Operator.fromToken(ctx.logicalOperator().value);
        final Condition right = visitCondition(ctx.right);
        return callback(new Logic(left, operator, right), ctx);
    }

    @Override
    public Not visitNegatedCondition(CCLParser.NegatedConditionContext ctx) {
        final Condition inner = visitCondition(ctx.condition());
        return callback(new Not(inner), ctx);
    }

    @Override
    public Compare visitComparisonCondition(CCLParser.ComparisonConditionContext ctx) {
        final Expression left = visitExpression(ctx.left);
        final Compare.Operator operator = Compare.Operator.fromToken(ctx.comparisonOperator().value);
        final Expression right = visitExpression(ctx.right);
        return callback(new Compare(left, operator, right), ctx);
    }

    // helper to convert a list of contexts to a list of nodes
    private <C extends ParserRuleContext, N extends Node> List<N>
    list(Class<N> type, List<C> contexts) {
        return contexts.stream().map(this::visit).map(type::cast).toList();
    }

    // helper to execute the callback on every created node
    <T extends Node> T callback(T newNode, ParserRuleContext ctx) {
        if (onNodeCreated.present()) onNodeCreated.get().accept(newNode, ctx);
        return newNode;
    }
}
