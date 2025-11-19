package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Type;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Var;
import org.example.ast.node.statement.Assign;
import org.example.helper.Option;
import org.example.helper.Pair;
import org.example.helper.Util;

import java.util.List;
import java.util.function.BiConsumer;

public class SemanticChecker extends AstVisitor<Void> {
    private final SymbolTable<Data> environment = new SymbolTable<>();
    public final BiConsumer<Node, String> onError;

    public SemanticChecker(BiConsumer<Node, String> onError) {
        this.onError = onError;
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

    // create & destroy scopes
    @Override
    public Void visitProgram(Program node) {
        wrapScope(node);
        return null;
    }

    @Override
    public Void visitMain(Main node) {
        wrapScope(node);
        return null;
    }

    @Override
    public Void visitFunction(Function function) {
        // functions add their name to the parent scope...
        if (!add(function.name, Data.Function.of(function)))
            alreadyDefined(function, function.name);
        push();
        // ...and add their parameters to their own scope
        for (Variable parameter : function.parameters) {
            if (parameter.type() == Type.VOID)
                error(function, String.format("Function parameter %s cannot be declared with type VOID", parameter.name()));
            if (!add(parameter.name(), new Data.Variable.Mutable(parameter.type()).assign()))
                alreadyDefined(function, parameter.name());
        }
        visitChildren(function);
        pop();
        return null;
    }

    // declarations are the only other source of symbols in the table
    @Override
    public Void visitConst(Const node) {
        final Variable var = node.variable;
        if (var.type() == Type.VOID)
            error(node, String.format("Constant %s cannot be declared with type VOID", var.name()));
        if (!add(var.name(), new Data.Variable.Constant(var.type())))
            alreadyDefined(node, var.name());
        return super.visitConst(node); // TODO evaluate expression and remove this
    }

    @Override
    public Void visitVar(Var node) {
        final Variable var = node.variable;
        if (var.type() == Type.VOID)
            error(node, String.format("Variable %s cannot be declared with type VOID", var.name()));
        if (!add(var.name(), new Data.Variable.Mutable(var.type())))
            alreadyDefined(node, var.name());
        return null;
    }

    @Override
    public Void visitReference(Reference node) {
        if (!get(node.variable).present()) undefined(node, node.variable);
        return super.visitReference(node);
    }

    @Override
    public Void visitCall(Call node) {
        final Option<Data> functionSignature = get(node.function);
        if (!functionSignature.present()) {
            undefined(node, node.function);
            return null;
        }

        if (!(functionSignature.get() instanceof Data.Function function)) {
            error(node, String.format("Identifier %s is not a function", node.function));
            return null;
        }

        if (function.parameterTypes().size() != node.arguments.size())
            error(node, String.format("Function %s called with the wrong number of arguments", node.function));

        for (Pair<Type, Identifier> pair : Util.zip(function.parameterTypes(), node.arguments)) {
            final Type requiredType = pair.left();
            final Identifier argument = pair.right();

            final Option<Data> data = get(argument);
            if (!data.present()) {
                error(node, String.format("Argument %s is undefined", argument));
                continue;
            }

            if (!(data.get() instanceof Data.Variable variable)) {
                error(node, String.format("Argument %s is not a variable", argument));
                continue;
            }

            if (variable.type() != requiredType)
                error(node, String.format("Argument %s is of type %s, required type is %s",
                        argument, variable.type(), requiredType));

            if (variable instanceof Data.Variable.Mutable mutable && !mutable.assigned())
                error(node, String.format("Argument %s has not been assigned a value", argument));
        }
        return null;
    }

    @Override
    public Void visitAssign(Assign node) {
        Option<Data> data = get(node.variable);
        if (!data.present()) {
            undefined(node, node.variable);
            return super.visitAssign(node);
        }

        switch (data.get()) {
            case Data.Function function -> {
                error(node, String.format("Cannot assign to function %s", function));
            }
            case Data.Variable variable -> {
                switch (variable) {
                    case Data.Variable.Constant ignored ->
                            error(node, String.format("Constant %s cannot be reassigned", node.variable));
                    case Data.Variable.Mutable mutable -> {
                        mutable.assign();
                        // TODO check that the expression is of the correct type for this variable
                    }
                }
            }
        }
        return super.visitAssign(node); // TODO evaluate expression and remove this
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

        record Function(Type type, List<Type> parameterTypes) implements Data {
            public static Function of(org.example.ast.node.Function function) {
                return new Function(function.type, function.parameters.stream().map(org.example.ast.data.Variable::type).toList());
            }
        }
    }
}
