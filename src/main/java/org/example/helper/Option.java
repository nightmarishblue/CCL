package org.example.helper;

import java.util.NoSuchElementException;
import java.util.function.Function;

// Java's Optional is subpar, so I have shamelessly copied Rust's
public sealed interface Option<T> permits Option.Some, Option.None {
    boolean present();
    T get();
    <N> Option<N> map(Function<T, N> f); // maps T in Some<T> to type N, returning None if empty

    static <T> Some<T> some(T value) {
        return new Some<>(value);
    }

    static <T> None<T> none() {
        return new None<>();
    }

    record Some<T>(T value) implements Option<T> {
        public boolean present() {
            return true;
        }

        public T get() {
            return value;
        }

        @Override
        public <N> Option<N> map(Function<T, N> f) {
            return Option.some(f.apply(this.value));
        }

        @Override
        public String toString() {
            return String.format("Some(%s)", value);
        }
    }

    record None<T>() implements Option<T> {
        @Override
        public boolean present() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException("get() called on None");
        }

        @Override
        public <N> Option<N> map(Function<T, N> f) {
            return Option.none();
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}
