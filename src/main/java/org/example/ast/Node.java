package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Node {
    public final int position; // or something?
    public Node(final ParserRuleContext ctx) {
        position = ctx.getStart().getStartIndex();
    }

    @Override
    public String toString() {
        final String type = getClass().getSimpleName();
        Field[] fields = getClass().getDeclaredFields();
        Stream<Object> values = Arrays.stream(fields).map(field -> {
            try {
                return field.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        String list = values.map(String::valueOf).collect(Collectors.joining(", "));
        return String.format("%s(%s)", type, list);
    }
}
