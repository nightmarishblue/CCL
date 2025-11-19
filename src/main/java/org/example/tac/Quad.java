package org.example.tac;

import org.example.helper.Option;

// represents a single instruction of 3AC
public record Quad(Op op, Address arg1, Option<Address> arg2, Option<Address> result) {
    public static Quad unary(Op op, Address arg, Address result) {
        return new Quad(op, arg, Option.none(), Option.some(result));
    }

    public static Quad binary(Op op, Address arg1, Address arg2, Address result) {
        return new Quad(op, arg1, Option.some(arg2), Option.some(result));
    }

    public String instruction() {
        return switch (op) {
            // special cases for jump, etc. can go here
            case COPY -> String.format("%s = %s", result.get().value(), arg1.value());
            default -> {
                if (arg2.present()) yield String.format("%s = %s %s %s", result.get().value(),
                        arg1.value(), op.symbol(), arg2.get().value());
                else yield String.format("%s = %s %s", result.get().value(), op.symbol(), arg1.value());
            }
        };
    }
}
