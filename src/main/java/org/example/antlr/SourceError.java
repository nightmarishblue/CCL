package org.example.antlr;

import org.antlr.v4.runtime.Token;

// note: line is expected to be 0-based when stored
public record SourceError(int line, int position, String message) {
    public SourceError(Token token, String message) {
        this(token.getLine() - 1, token.getCharPositionInLine(), message);
    }
}
