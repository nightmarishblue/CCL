package org.example.ast.node;

import org.example.helper.Option;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Node {
    // get all the Nodes underneath this one
    public List<Node> children() {
        // would be safer to mandate children to define this, but reflection is a decent fallback
        List<Node> result = new ArrayList<>();

        for (Field field : this.getClass().getFields()) {
            if (!field.canAccess(this)) continue;

            Object value;
            try { value = field.get(this); }
            catch (IllegalAccessException e) { throw new RuntimeException(e); }

            if (value instanceof Option.Some<?> some) value = some.get(); // unwrap any Option<Node>, etc.

            if (value instanceof Node node) result.add(node);
            else if (value instanceof List<?> list) {
                list.stream()
                        .filter(Node.class::isInstance)
                        .map(Node.class::cast)
                        .forEach(result::add);
            }
        }

        return Collections.unmodifiableList(result);
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
