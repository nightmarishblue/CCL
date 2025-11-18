package org.example.lang;

import org.example.ast.data.Identifier;
import org.example.helper.Iterate;
import org.example.helper.Option;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable<T> {
    // each scope is a map of name-value pairs
    // we use an array deque so we can efficiently iterate it in reverse
    public final ArrayDeque<HashMap<Identifier, T>> stack = new ArrayDeque<>();

    public void pushScope() {
        stack.push(new HashMap<>());
    }

    public void popScope() {
        stack.pop();
    }

    public Map<Identifier, T> peekScope() {
        return stack.peek();
    }

    public void putSymbol(final Identifier symbol, final T value) {
        peekScope().put(symbol, value);
    }

    public Option<Map<Identifier, T>> getSymbolScope(final Identifier symbol) {
        for (Map<Identifier, T> scope : Iterate.on(stack.descendingIterator())) {
            if (scope.containsKey(symbol)) return Option.some(scope);
        }
        return Option.none();
    }

    public Option<T> getSymbol(final Identifier symbol) {
        return getSymbolScope(symbol).map(map -> map.get(symbol));
    }
}
