package org.example.tac;

public sealed interface Address {
    Object value();

    record Name(String value) implements Address {}
    record Constant(Object value) implements Address {}
}
