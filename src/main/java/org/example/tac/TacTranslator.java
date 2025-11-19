package org.example.tac;

import org.example.ast.AstVisitor;
import org.example.ast.node.atom.Reference;
import org.example.ast.node.atom.literal.Literal;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.declaration.Var;
import org.example.ast.node.expression.Arithmetic;
import org.example.ast.node.statement.Assign;
import org.example.helper.Option;

public class TacTranslator extends AstVisitor<Option<Address>> {
    // visit(Expression) should return an address, anything else should return none
    private int temps = 0;

    private Address temp() {
        return new Address.Name("t" + ++temps);
    }

    private void emit(Quad quad) {
        System.out.println(quad.instruction()); // TODO expose API
    }

    @Override
    protected Option<Address> defaultValue() {
        return Option.none();
    }

    @Override
    public Option<Address> visitLiteral(Literal node) {
        Address out = new Address.Constant(node.value());
        return Option.some(out);
    }

    @Override
    public Option<Address> visitReference(Reference node) {
        Address.Name name = new Address.Name(node.variable.value());
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

    @Override
    public Option<Address> visitArithmetic(Arithmetic node) {
        Address left = visit(node.left).get();
        Address right = visit(node.right).get();
        Address out = temp();

        Quad result = Quad.binary(Op.arithmetic(node.operator), left, right, out);

        emit(result);

        return Option.some(out);
    }

    @Override
    public Option<Address> visitConst(Const node) {
        Address value = visit(node.value).get();
        Address result = new Address.Name(node.variable.name().value());
        emit(Quad.unary(Op.COPY, value, result));
        return Option.none();
    }

    @Override
    public Option<Address> visitVar(Var node) {
        // we're leaving this as a no-op since we mandate assignment before reading
        return super.visitVar(node);
    }

    @Override
    public Option<Address> visitAssign(Assign node) {
        Address value = visit(node.value).get();
        Address result = new Address.Name(node.variable.value());
        emit(Quad.unary(Op.COPY, value, result));
        return Option.none();
    }
}
