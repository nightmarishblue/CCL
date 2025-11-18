package org.example;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.example.grammar.CCLLexer;
import org.example.grammar.CCLParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Error: insufficient arguments");
            System.err.printf("Usage: %s <filename>\n", Main.class.getName());
            System.exit(2);
        }
        final String file = args[0];
        final boolean succeeded = parseFile(file);
        if (succeeded)
            System.out.printf("%s parsed successfully\n", file);
        else
            System.out.printf("%s has not parsed\n", file);
    }

    public static CCLLexer lexer(CharStream chars) {
        CCLLexer lexer = new CCLLexer(chars);
        lexer.removeErrorListeners();
        return lexer;
    }

    public static CCLParser parser(TokenStream tokens) {
        CCLParser parser = new CCLParser(tokens);
        parser.removeErrorListeners();
        return parser;
    }

    public static CCLParser parser(CharStream chars, ANTLRErrorListener listener) {
        final CCLLexer lexer = lexer(chars);
        final CCLParser parser = parser(new CommonTokenStream(lexer));
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);
        return parser;
    }

    // parse a file, outputting either the concrete syntax tree or a list of errors
    public static CCLParser.ProgramContext parse(CharStream chars) {
        CollectingErrorListener errorListener = new CollectingErrorListener();
        final CCLParser parser = parser(chars, errorListener);
        final CCLParser.ProgramContext program = parser.program();

        List<SourceError> errors = errorListener.getErrors();
        if (!errors.isEmpty()) throw new CompilationFailed(errors);
        return program;
    }


    public static void compile(CharStream chars) {
        // compiling a file consists of
        // 1. parse the file (stop if errors)
        CCLParser.ProgramContext p = parse(chars);
        // 2. construct the ast
        // 3. semantically analyse
    }
}
