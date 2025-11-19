package org.example.ast;

import org.example.ast.node.*;
import org.example.ast.node.atom.Atom;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.atom.literal.Boolean;
import org.example.ast.node.atom.literal.Integer;
import org.example.ast.node.atom.literal.Literal;
import org.example.ast.node.condition.Compare;
import org.example.ast.node.condition.Condition;
import org.example.ast.node.condition.Logic;
import org.example.ast.node.condition.Not;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.declaration.Var;
import org.example.ast.node.expression.Arithmetic;
import org.example.ast.node.expression.Expression;
import org.example.ast.node.expression.Result;
import org.example.ast.node.expression.Value;
import org.example.ast.node.statement.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// a (much simpler) visitor inspired by ANTLR's parse tree visitor API
public abstract class AstVisitor<T> {
    // what to return for a Node with no visit method
    protected T defaultValue() {
        return null;
    }

    // what to do with the next output - default, replace the old
    protected T aggregate(T current, T next) {
        return next;
    }

    // dispatcher for the visitor - ANTLR uses double dispatch, I use reflection
    public T visit(Node node) {
        try {
            Method method = this.getClass().getMethod("visit" + node.getClass().getSimpleName(), node.getClass());
            @SuppressWarnings("unchecked")
            final T output = (T) method.invoke(this, node); // visit*() methods all return T
            return output;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    // for nodes with no defined method, walk through their children
    public T visitChildren(Node node) {
        T result = defaultValue();
        for (Node child : node.children()) {
            result = aggregate(result, visit(child));
        }
        return result;
    }


    public T visitNode(Node node) {
        return visitChildren(node);
    }

    // blocks
    public T visitProgram(Program node) {
        return visitNode(node);
    }

    public T visitMain(Main node) {
        return visitNode(node);
    }

    public T visitFunction(Function node) {
        return visitNode(node);
    }

    public T visitCall(Call node) {
        return visitNode(node);
    }


    // atoms & literals
    public T visitAtom(Atom node) {
        return visitNode(node);
    }

    public T visitReference(Reference node) {
        return visitAtom(node);
    }

    public T visitLiteral(Literal node) {
        return visitAtom(node);
    }

    public T visitInteger(Integer node) {
        return visitLiteral(node);
    }

    public T visitBoolean(Boolean node) {
        return visitLiteral(node);
    }


    // conditions
    public T visitCondition(Condition node) {
        return visitNode(node);
    }

    public T visitCompare(Compare node) {
        return visitCondition(node);
    }

    public T visitLogic(Logic node) {
        return visitCondition(node);
    }

    public T visitNot(Not node) {
        return visitCondition(node);
    }


    // declarations
    public T visitDeclaration(Declaration node) {
        return visitNode(node);
    }

    public T visitConst(Const node) {
        return visitDeclaration(node);
    }

    public T visitVar(Var node) {
        return visitDeclaration(node);
    }


    // expressions
    public T visitExpression(Expression node) {
        return visitNode(node);
    }

    public T visitArithmetic(Arithmetic node) {
        return visitExpression(node);
    }

    public T visitResult(Result node) {
        return visitExpression(node);
    }

    public T visitValue(Value node) {
        return visitExpression(node);
    }


    // statements
    public T visitStatement(Statement node) {
        return visitNode(node);
    }

    public T visitAssign(Assign node) {
        return visitStatement(node);
    }

    public T visitBlock(Block node) {
        return visitStatement(node);
    }

    public T visitDiscard(Discard node) {
        return visitStatement(node);
    }

    public T visitIfElse(IfElse node) {
        return visitStatement(node);
    }

    public T visitSkip(Skip node) {
        return visitStatement(node);
    }

    public T visitWhile(While node) {
        return visitStatement(node);
    }
}
