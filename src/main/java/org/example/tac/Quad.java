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

    public static Quad label(Address.Name name) {
        return single(Op.LABEL, name);
    }

    // goto is a reserved Java keyword. who knew?
    public static Quad jump(Address.Name name) {
        return single(Op.GOTO, name);
    }

    public static Quad single(Op op, Address address) { // unary-er
        return new Quad(op, address, Option.none(), Option.none());
    }

    public String instruction() {
        return switch (op) {
            case COPY -> String.format("%s = %s", result.get().value(), arg1.value());

            // unary keywords
            case RETURN -> String.format("return %s", result.mapOr("", Address::value)); // this one can have no value
            case GOTO, PARAM, GETPARAM -> String.format("%s %s", op.symbol(), arg1.value());

            case LABEL -> String.format("%s:", arg1.value());

            case EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL ->
                    String.format("if %s %s %s goto %s", arg1.value(), op.symbol(), arg2.get().value(), result.get().value());

            // anything else is either a binary or unary operation
            default -> {
                if (arg2.present()) yield String.format("%s = %s %s %s", result.get().value(),
                        arg1.value(), op.symbol(), arg2.get().value());
                else yield String.format("%s = %s %s", result.get().value(), op.symbol(), arg1.value());
            }
        };
    }
}
