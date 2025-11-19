package org.example.tac;

import org.example.ast.AstVisitor;
import org.example.ast.node.atom.literal.Literal;
import org.example.ast.node.declaration.Const;
import org.example.ast.node.declaration.Declaration;
import org.example.ast.node.expression.Arithmetic;
import org.example.helper.Option;

public class TacTranslator extends AstVisitor<Option<Address>> {
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
    public Option<Address> visitDeclaration(Declaration node) {
//        System.out.
        return super.visitDeclaration(node);
    }

    @Override
    public Option<Address> visitLiteral(Literal node) {
        Address out = new Address.Constant(node.value());
        return Option.some(out);
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
        return Option.some(result);
    }
}
