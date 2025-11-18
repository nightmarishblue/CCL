package org.example.helper;

// they say Java is the manliest programming language, because every dev has to grow their own Pair<L, R>
public record Pair<L, R>(L left, R right) {
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
