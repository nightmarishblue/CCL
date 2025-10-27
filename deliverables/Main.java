//package org.example; // package removed for assignment purposes


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
// these imports are unneccessary if -package isn't passed to ANTLR
//import org.example.grammar.CCLLexer;
//import org.example.grammar.CCLParser;

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

    public static boolean parseFile(final String filepath) throws IOException {
        CharStream stream = CharStreams.fromFileName(filepath);
        CCLLexer lexer = new CCLLexer(stream);
        TokenStream tokens = new CommonTokenStream(lexer);
        CCLParser parser = new CCLParser(tokens);
        // disable the error reporting to console
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        try {
            parser.program();
        } catch (ParseCancellationException e) {
            return false;
        }
        return true;
    }
}
