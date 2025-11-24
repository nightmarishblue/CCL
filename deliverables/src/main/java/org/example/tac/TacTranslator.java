package org.example.tac;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.atom.literal.Literal;
import org.example.ast.node.condition.Compare;
import org.example.ast.node.condition.Condition;
import org.example.ast.node.condition.Logic;
import org.example.ast.node.condition.Not;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Var;
import org.example.ast.node.expression.Arithmetic;
import org.example.ast.node.expression.Result;
import org.example.ast.node.statement.Assign;
import org.example.ast.node.statement.Discard;
import org.example.ast.node.statement.IfElse;
import org.example.ast.node.statement.While;
import org.example.helper.Option;
import org.example.lang.SymbolTable;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TacTranslator extends AstVisitor<Option<Address>> {
    final private Consumer<Quad> onEmit;
    final private SymbolTable<Address.Name> symbols = new SymbolTable<>();
    private int temps = 0;
    private int labels = 0;
    private int variables = 0;

    public TacTranslator(Consumer<Quad> onEmit) {
        this.onEmit = onEmit;
    }

    private void emit(Quad quad) {
        onEmit.accept(quad);
    }

    private Address.Name temp() {
        return new Address.Name("t" + ++temps);
    }

    private Address.Name label() {
        return new Address.Name("l" + ++labels);
    }

    // map variable names to addresses
    private Address.Name variable(Identifier name) {
        // every variable gets a unique address
        // if a variable is found in scope, use that
        Option<Address.Name> symbol = symbols.getSymbol(name);
        if (symbol.present()) return symbol.get();
        Address.Name address = new Address.Name("v" + ++variables);
        symbols.putSymbol(name, address);
        return address;
    }

    @Override
    protected Option<Address> defaultValue(Node ignored) {
        return Option.none();
    }


    @Override
    public Option<Address> visitProgram(Program node) {
        symbols.pushScope();
        // add all global variables to the scope
        node.declarations.forEach(d -> variable(d.variable.name()));
        node.functions.forEach(this::visit);

        emit(Quad.label(new Address.Name("main")));
        // in TACi, main contains the global scope, hence why the declarations have to go here
        node.declarations.forEach(d -> {
            if (d instanceof Const c) visitConst(c); // constants are always defined
            else {
                // but variables are normally left blank; we initialise them with a default so they stay in main's scope
                Address.Name name = variable(d.variable.name());
                Object default_ = switch (d.variable.type()) {
                    case INTEGER -> 0;
                    case BOOLEAN -> false;
                    case VOID, ANY, NONE -> throw new RuntimeException(d.variable.type() + " variable declared");
                };
                emit(Quad.unary(Op.COPY, new Address.Constant(default_), name));
            }
        });
        visit(node.main);
        symbols.popScope();
        return Option.none();
    }

    @Override
    public Option<Address> visitMain(Main node) {
        return visitChildren(node); // nothing to do from here, just traverse
    }

    @Override
    public Option<Address> visitFunction(Function node) {
        symbols.pushScope();
        emit(Quad.label(new Address.Name(node.name.value().toLowerCase(Locale.ROOT))));

        // retrieve arguments
        AtomicInteger i = new AtomicInteger();
        node.parameters.stream()
                .map(Variable::name)
                .map(this::variable)
                .forEach(parameter ->
                    emit(Quad.unary(Op.GETPARAM, new Address.Constant(i.getAndIncrement()), parameter))
                );

        node.declarations.forEach(this::visit);
        node.statements.forEach(this::visit);

        Option<Address> result = node.output.map(this::visit).map(Option::get);
        // using null is a code smell - it appears quads don't map as cleanly to the problem as the notes said
        emit(new Quad(Op.RETURN, null, Option.none(), result));
        symbols.popScope();
        return Option.none();
    }

    // expressions
    @Override
    public Option<Address> visitLiteral(Literal node) {
        Address out = new Address.Constant(node.value());
        return Option.some(out);
    }

    @Override
    public Option<Address> visitReference(Reference node) {
        Address.Name name = variable(node.variable);
        if (node.negate) {
            Address temp = temp();
            // our 3AC as no unary negation support, so we create a temporary
            Quad negation = Quad.binary(Op.MINUS, new Address.Constant(0), name, temp);
            emit(negation);
            return Option.some(temp);
        } else {
            return Option.some(name);
        }
    }

    private void invoke(Call functionCall, Option<Address> result) {
        // prepare arguments
        functionCall.arguments
                .reversed()
                .stream()
                .map(this::variable)
                .forEach(arg -> emit(Quad.single(Op.PARAM, arg)));

        Address.Name function = new Address.Name(functionCall.function.value().toLowerCase(Locale.ROOT));
        Address.Constant arguments = new Address.Constant(functionCall.arguments.size());
        emit(new Quad(Op.CALL, function, Option.some(arguments), result));
    }

    @Override
    public Option<Address> visitResult(Result node) {
        Address.Name result = temp();
        invoke(node.functionCall, Option.some(result));
        return Option.some(result);
    }

    @Override
    public Option<Address> visitDiscard(Discard node) {
        invoke(node.functionCall, Option.none());
        return Option.none();
    }

    @Override
    public Option<Address> visitArithmetic(Arithmetic node) {
        Address left = visit(node.left).get();
        Address right = visit(node.right).get();
        Address out = temp();

        Quad result = Quad.binary(Op.arithmetic(node.operator), left, right, out);

        emit(result);

        return Option.some(out);
    }

    // declarations/assignments
    @Override
    public Option<Address> visitConst(Const node) {
        Address value = visit(node.value).get();
        Address result = variable(node.variable.name());
        emit(Quad.unary(Op.COPY, value, result));
        return Option.none();
    }

    @Override
    public Option<Address> visitVar(Var node) {
        // this is a no-op since we mandate assignment before reading
        return Option.none();
    }

    @Override
    public Option<Address> visitAssign(Assign node) {
        Address value = visit(node.value).get();
        Address result = variable(node.variable);
        emit(Quad.unary(Op.COPY, value, result));
        return Option.none();
    }

    // logical operators are allowed in assignments but not conditional jumps
    // comparison operators are mandated in conditional jumps but disallowed in assignments
    // terrible 3ac design and terrible grammar design have both come together magically to force this on me
    void emitCondition(Condition condition, Address.Name trueLabel, Address.Name falseLabel) {
        switch (condition) {
            // comparison operators work as-is
            case Compare cmp -> {
                Address left = visit(cmp.left).get(), right = visit(cmp.right).get();
                emit(Quad.binary(Op.comparison(cmp.operator), left, right, trueLabel));
                emit(Quad.jump(falseLabel));
            }
            // logical operators can be recreated with multiple ifs
            case Logic logic -> {
                Address.Name middle = label();
                if (logic.operator == Logic.Operator.AND) {
                    /*
                        A && B is equivalent to:
                        if a goto middle
                        goto false
                        middle:
                        if b goto true
                        goto false
                     */
                    emitCondition(logic.left, middle, falseLabel);
                } else {
                    /*
                        A || B is equivalent to:
                        if a goto true
                        if b goto true
                        goto false
                     */
                    emitCondition(logic.left, trueLabel, middle);
                }
                emit(Quad.label(middle));
                emitCondition(logic.right, trueLabel, falseLabel);
            }
            // a not is easy - just swap the labels
            case Not not -> emitCondition(not.inner, falseLabel, trueLabel);
            default -> throw new IllegalStateException("Unexpected value: " + condition);
        }
    }

    // control flow
    @Override
    public Option<Address> visitIfElse(IfElse node) {
        Address.Name then = label(), else_ = label(), endif = label();
        emitCondition(node.condition, then, else_); // this needs both labels, so we can't get away with using 2 total

        emit(Quad.label(then));
        node.then.forEach(this::visit);
        emit(Quad.jump(endif));

        emit(Quad.label(else_));
        node.else_.forEach(this::visit);
        emit(Quad.label(endif));

        return Option.none();
    }

    @Override
    public Option<Address> visitWhile(While node) {
        Address.Name check = label(), body = label(), end = label();

        emit(Quad.label(check));
        emitCondition(node.condition, body, end); // if true go to body, else go to end

        emit(Quad.label(body));
        node.body.forEach(this::visit);

        emit(Quad.jump(check));
        emit(Quad.label(end));

        return Option.none();
    }
}
