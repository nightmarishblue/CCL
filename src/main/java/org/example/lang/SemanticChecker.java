package org.example.lang;

import org.example.ast.AstVisitor;
import org.example.ast.data.Identifier;
import org.example.ast.data.Type;
import org.example.ast.data.Variable;
import org.example.ast.node.*;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.statement.Assign;
import org.example.helper.Option;
import org.example.helper.Pair;
import org.example.helper.Util;

import java.util.function.BiConsumer;

public class SemanticChecker extends AstVisitor<Void> {
    private final SymbolTable<Signature> environment = new SymbolTable<>();
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

    private boolean add(final Identifier id, Signature signature) {
        if (environment.peekScope().containsKey(id)) return false;
        environment.putSymbol(id, signature);
        return true;
    }

    private Option<Signature> get(final Identifier id) {
        return environment.getSymbol(id);
    }

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
        if (!add(function.name, Signature.Function.of(function)))
            alreadyDefined(function, function.name);
        push();
        // ...and add their parameters to their own scope
        for (Variable var : function.parameters) {
            if (var.type() == Type.VOID)
                error(function, String.format("Function parameter %s cannot be declared with type VOID", var.name()));
            else {
                if (!add(var.name(), new Signature.Variable(var.type(), false)))
                    alreadyDefined(function, var.name());
            }
        }
        visitChildren(function);
        pop();
        return null;
    }

    // declarations are the only other source of symbols in the table
    @Override
    public Void visitDeclaration(Declaration node) {
        // not adding the variable in the case of void saves us headache later
        final Variable var = node.variable;
        if (var.type() == Type.VOID)
            error(node, String.format("Variable %s cannot be declared with type void", var.name()));
        else {
            if (!add(var.name(), new Signature.Variable(var.type(), node instanceof Const)))
                alreadyDefined(node, var.name());
        }
        return null;
    }

    @Override
    public Void visitReference(Reference node) {
        if (!get(node.variable).present()) undefined(node, node.variable);
        return super.visitReference(node);
    }

    @Override
    public Void visitCall(Call node) {
        final Option<Signature> functionSignature = get(node.function);
        if (!functionSignature.present()) {
            undefined(node, node.function);
            return null;
        }

        if (functionSignature.get() instanceof Signature.Function function) {
            if (function.parameterTypes().size() != node.arguments.size())
                error(node, String.format("Function %s called with the wrong number of arguments", node.function));

            for (Pair<Type, Identifier> pair : Util.zip(function.parameterTypes(), node.arguments)) {
                final Type required = pair.left();
                final Identifier argument = pair.right();
                final Option<Signature> signature = get(argument);
                if (!signature.present()) error(node, String.format("Argument %s is undefined", argument));
                else if (signature.get() instanceof Signature.Variable variable && variable.type() != required)
                    error(node, String.format("Argument %s is of type %s, required type is %s", argument, variable.type(), required));
                else error(node, String.format("Argument %s is not a variable", argument));
            }
        } else error(node, String.format("Identifier %s is not a function", node.function));
        return null;
    }

    @Override
    public Void visitAssign(Assign node) {
        Option<Signature> signature = get(node.variable);
        if (signature.present()) {
            if (signature.get() instanceof Signature.Variable variable) {
                if (variable.constant()) error(node, String.format("%s is constant and cannot be reassigned", node.variable));
                // TODO check that the expression is of the correct type
            }
        } else undefined(node, node.variable);
        return null;
    }
}
