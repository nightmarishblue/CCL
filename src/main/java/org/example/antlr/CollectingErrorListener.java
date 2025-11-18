package org.example.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectingErrorListener extends BaseErrorListener {
    private final ArrayList<SourceError> errors = new ArrayList<>();

    public List<SourceError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        final SourceError error = new SourceError(line - 1, charPositionInLine, msg);
        errors.add(error);
    }
}
