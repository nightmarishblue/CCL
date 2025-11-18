package org.example.lang;

import org.example.ast.data.Type;

import java.util.List;

public sealed interface Signature {
    record Variable(Type type, boolean constant) implements Signature {
        public Variable {
            if (type == Type.VOID) throw new IllegalArgumentException("Variables may not be type void");
        }
    }

    record Function(Type type, List<Type> parameterTypes) implements Signature {
        public static Function of(org.example.ast.node.Function function) {
            return new Function(function.type, function.parameters.stream().map(org.example.ast.data.Variable::type).toList());
        }
    }
}
