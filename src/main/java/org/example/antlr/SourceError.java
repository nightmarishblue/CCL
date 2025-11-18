package org.example.antlr;

public record SourceError(int line, int position, String message) {
}
