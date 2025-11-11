package org.example.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Node {
    public final int position; // or something?
    public Node(final ParserRuleContext ctx) {
        position = ctx.getStart().getStartIndex();
    }

    @Override
    public String toString() {
        final String type = getClass().getSimpleName();
        Field[] fields = getClass().getDeclaredFields();
        Object[] values = Arrays.stream(fields).map(field -> {
            try {
                return field.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toArray();
        return String.format("%s(%s)", type, Arrays.toString(values));
    }
}
