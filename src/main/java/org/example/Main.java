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

    public static CCLParser fileParser(final String filepath) throws IOException {
        CharStream chars = CharStreams.fromFileName(filepath);
        TokenStream tokens = new CommonTokenStream(lexer(chars));
        return parser(tokens);
    }

    public static boolean parseFile(final String filepath) throws IOException {
        CCLParser parser = fileParser(filepath);
        parser.setErrorHandler(new BailErrorStrategy());
        try {
            parser.program();
        } catch (ParseCancellationException e) {
            return false;
        }
        return true;
    }
}
