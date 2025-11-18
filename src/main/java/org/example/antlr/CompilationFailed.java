package org.example.antlr;

import java.util.List;

public class CompilationFailed extends RuntimeException {
    public final List<SourceError> errors;

    public CompilationFailed(List<SourceError> errors) {
        this.errors = errors;
    }
}
