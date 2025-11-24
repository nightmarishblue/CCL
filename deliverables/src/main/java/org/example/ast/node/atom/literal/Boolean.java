package org.example.ast.node.atom.literal;

public class Boolean extends Literal {
    public final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }
}
