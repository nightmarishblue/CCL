package org.example.helper;

import java.util.NoSuchElementException;
import java.util.function.Function;

// Java's Optional is subpar, so I have shamelessly copied Rust's
public sealed interface Option<T> permits Option.Some, Option.None {
    boolean present();

    // return the contained value if Some, throw if None
    T get();
    // return the contained value if Some, or the provided default if None
    T getOr(T fallback);

    // maps Some<T> to Some<N> with the provided function, returning None if empty
    <N> Option<N> map(Function<T, N> f);
    // maps Some<T> to N with the provided function, returning the fallback if empty
    <N> N mapOr(N fallback, Function<T, N> f);

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
        public T getOr(T fallback) {
            return value;
        }

        @Override
        public <N> Option<N> map(Function<T, N> f) {
            return Option.some(f.apply(this.value));
        }

        @Override
        public <N> N mapOr(N fallback, Function<T, N> f) {
            return f.apply(value);
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
        public T getOr(T fallback) {
            return fallback;
        }

        @Override
        public <N> Option<N> map(Function<T, N> f) {
            return Option.none();
        }

        @Override
        public <N> N mapOr(N fallback, Function<T, N> f) {
            return fallback;
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}
