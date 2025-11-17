package org.example.ast.condition;

public class Not extends Condition {
    public final Condition inner;

    public Not(Condition inner) {
        this.inner = inner;
    }
}
