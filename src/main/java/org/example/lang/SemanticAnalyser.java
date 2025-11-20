package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.atom.literal.Boolean;
import org.example.ast.node.atom.literal.Integer;
import org.example.ast.node.condition.Compare;
import org.example.ast.node.condition.Logic;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Var;
import org.example.ast.node.expression.Arithmetic;
import org.example.ast.node.statement.Assign;
import org.example.helper.Option;
import org.example.helper.Pair;
import org.example.helper.Util;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SemanticAnalyser extends AstVisitor<Type> {
    private final SymbolTable<Data> environment = new SymbolTable<>();
    public final BiConsumer<Node, String> onError;

    public SemanticAnalyser(BiConsumer<Node, String> onError) {
        this.onError = onError;
    }

    public SemanticAnalyser(BiConsumer<Node, String> onError, Map<Identifier, Data.Function> builtins) {
        this(onError);
        push();
        builtins.forEach(this::add); // add all builtins to the prelude
    }

    @Override
    protected Type defaultValue() {
        return Type.VOID; // void is our "empty" type
    }

    private void error(Node node, String message) {
        onError.accept(node, message);
    }

    private void wrapScope(Node node) {
        push();
        visitChildren(node);
        pop();
    }

    private void push() {
        environment.pushScope();
    }

    private void pop() {
        environment.popScope();
    }

    private boolean add(final Identifier id, Data data) {
        if (environment.peekScope().containsKey(id)) return false;
        environment.putSymbol(id, data);
        return true;
    }

    private Option<Data> get(final Identifier id) {
        return environment.getSymbol(id);
    }

    // helpers to add error messages
    private void alreadyDefined(Node node, Identifier id) {
        error(node, String.format("Identifier %s is already defined in this scope", id));
    }

    private void undefined(Node node, Identifier id) {
        error(node, String.format("Identifier %s is undefined", id));
    }

    private void requiredKind(Node node, Identifier id, Class<? extends Data> kind) {
        error(node, String.format("Identifier %s is not a %s", id, kind.getSimpleName()));
    }

    // create & destroy scopes
    @Override
    public Type visitProgram(Program node) {
        wrapScope(node);
        return Type.NONE;
    }

    @Override
    public Type visitMain(Main node) {
        wrapScope(node);
        return Type.NONE;
    }

    @Override
    public Type visitFunction(Function function) {
        // functions add their name to the parent scope...
        if (!add(function.name, Data.Function.of(function)))
            alreadyDefined(function, function.name);

        push();
        // ...and add their parameters to their own scope
        for (Variable parameter : function.parameters) {
            if (Type.VOID.is(parameter.type()))
                error(function, String.format("Function parameter %s cannot be declared with type VOID", parameter.name()));
            if (!add(parameter.name(), new Data.Variable.Mutable(parameter.type()).assign()))
                alreadyDefined(function, parameter.name());
        }

        function.declarations.forEach(this::visit);
        function.statements.forEach(this::visit);

        Type receivedType = function.output.mapOr(Type.VOID, this::visit);
        if (!receivedType.is(function.type)) {
            error(function.output.mapOr(function, Node.class::cast),
                    String.format("Function %s must return %s, received %s", function.name, function.type, receivedType));
        }

        pop();
        return Type.NONE;
    }

    // declarations are the only other source of symbols in the table
    @Override
    public Type visitConst(Const node) {
        final Variable var = node.variable;
        Type expressionType = visit(node.value); // evaluate value first just in case

        boolean declaredVoid = var.type().is(Type.VOID);
        if (declaredVoid)
            error(node, String.format("Constant %s cannot be declared with type VOID", var.name()));

        if (!add(var.name(), new Data.Variable.Constant(var.type())))
            alreadyDefined(node, var.name());

        // don't type check the value if already void
        if (!(declaredVoid || expressionType.is(var.type())))
            error(node, String.format("Constant %s requires a value of type %s, received %s",
                    node.variable.name(), var.type(), expressionType));

        return Type.NONE;
    }

    @Override
    public Type visitVar(Var node) {
        final Variable var = node.variable;
        if (Type.VOID.is(var.type()))
            error(node, String.format("Variable %s cannot be declared with type VOID", var.name()));
        if (!add(var.name(), new Data.Variable.Mutable(var.type())))
            alreadyDefined(node, var.name());
        return Type.NONE;
    }

    @Override
    public Type visitAssign(Assign node) {
        Option<Data> data = get(node.variable);
        if (!data.present()) {
            undefined(node, node.variable);
            return super.visitAssign(node);
        }

        if (!(data.get() instanceof Data.Variable variable)) {
            requiredKind(node, node.variable, Data.Variable.class);
            return super.visitAssign(node);
        }

        switch (variable) {
            case Data.Variable.Constant ignored ->
                    error(node, String.format("Constant %s cannot be reassigned", node.variable));
            case Data.Variable.Mutable mutable -> {
                Type valueType = visit(node.value);
                if (!valueType.is(mutable.type))
                    error(node, String.format("Variable %s requires a value of type %s, received %s",
                            node.variable, mutable.type, valueType));
                mutable.assign();
            }
        }
        return Type.NONE;
    }

    // typed nodes
    @Override
    public Type visitInteger(Integer node) {
        return Type.INTEGER;
    }

    @Override
    public Type visitBoolean(Boolean node) {
        return Type.BOOLEAN;
    }

    @Override
    public Type visitArithmetic(Arithmetic node) {
        if (!(visit(node.left).is(Type.INTEGER)))
            error(node, "Left-hand side of arithmetic expression must be of type INTEGER");
        if (!(visit(node.right).is(Type.INTEGER)))
            error(node, "Right-hand side of arithmetic expression must be of type INTEGER");
        return Type.INTEGER;
    }

    @Override
    public Type visitLogic(Logic node) {
        // grammar doesn't allow you to use expressions like a || b, so there's no need to check
        return Type.BOOLEAN;
    }

    @Override
    public Type visitCompare(Compare node) {
        Type left = visit(node.left), right = visit(node.right);
        // unfortunately the spec tied my hands; equals and not equals must be handled here
        switch (node.operator) {
            case EQUALS:
            case NOT_EQUALS:
                if (!left.is(right))
                    error(node, String.format("Mismatched types for %s comparison, %s != %s", node.operator, left, right));
                break;
            default:
                // other operators are only defined for numbers
                if (!(left.is(Type.INTEGER) && right.is(Type.INTEGER)))
                    error(node, String.format("Operator %s requires INTEGER, cannot be applied to %s, %s",
                            node.operator, left, right));
        }
        return Type.BOOLEAN;
    }

    // the following nodes' types are dependent on program state or AST structure
    @Override
    public Type visitReference(Reference node) {
        Option<Data> data = get(node.variable);
        if (!data.present()) {
            undefined(node, node.variable);
            return super.visitReference(node);
        }

        if (!(data.get() instanceof Data.Variable variable)) {
            requiredKind(node, node.variable, Data.Variable.class);
            return super.visitReference(node);
        }

        if (variable instanceof Data.Variable.Mutable mutable && !mutable.assigned())
            error(node, String.format("Variable %s has not been assigned a value", node.variable));

        if (node.negate && !variable.type().is(Type.INTEGER))
            error(node, String.format("Variable %s is not an INTEGER and can't be negated", node.variable));

        return variable.type();
    }

    @Override
    public Type visitCall(Call node) {
        final Option<Data> functionSignature = get(node.function);
        if (!functionSignature.present()) {
            undefined(node, node.function);
            return super.visitCall(node);
        }

        if (!(functionSignature.get() instanceof Data.Function(Type returnType, List<Type> parameterTypes))) {
            requiredKind(node, node.function, Data.Function.class);
            return super.visitCall(node);
        }

        if (parameterTypes.size() != node.arguments.size())
            error(node, String.format("Function %s called with %d argument(s), requires %d",
                    node.function, node.arguments.size(), parameterTypes.size()));

        for (Pair<Type, Identifier> pair : Util.zip(parameterTypes, node.arguments)) {
            final Type requiredType = pair.left();
            final Identifier argument = pair.right();

            final Option<Data> data = get(argument);
            if (!data.present()) {
                error(node, String.format("Argument %s is undefined", argument));
                continue;
            }

            if (!(data.get() instanceof Data.Variable variable)) {
                error(node, String.format("Argument %s is not a Variable", argument));
                continue;
            }

            if (!variable.type().is(requiredType))
                error(node, String.format("Argument %s is of type %s, required type is %s",
                        argument, variable.type(), requiredType));

            if (variable instanceof Data.Variable.Mutable mutable && !mutable.assigned())
                error(node, String.format("Argument %s has not been assigned a value", argument));
        }

        return returnType;
    }

    public sealed interface Data {
        Type type();

        sealed interface Variable extends Data {
            record Constant(Type type) implements Variable {}

            final class Mutable implements Variable {
                private final Type type;
                private boolean assigned = false;

                public Mutable(Type type) {
                    this.type = type;
                }

                public boolean assigned() {
                    return assigned;
                }

                public Type type() {
                    return type;
                }

                // mark this mutable variable as assigned (return self for chaining)
                public Mutable assign() {
                    assigned = true;
                    return this;
                }
            }
        }

        record Function(Type returnType, List<Type> parameterTypes) implements Data {
            public static Function of(org.example.ast.node.Function function) {
                return new Function(function.type, function.parameters.stream()
                        .map(org.example.ast.data.Variable::type).toList());
            }

            public Type type() {
                return returnType;
            }
        }
    }
}
