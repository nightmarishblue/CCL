package org.example;


import org.antlr.v4.runtime.*;
import org.example.antlr.AstBuilder;
import org.example.antlr.CollectingErrorListener;
import org.example.antlr.SourceError;
import org.example.ast.Node;
import org.example.ast.Program;
import org.example.grammar.CCLLexer;
import org.example.grammar.CCLParser;
import org.example.antlr.CompilationFailed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Error: insufficient arguments");
            System.err.printf("Usage: %s <filename>\n", Main.class.getName());
            System.exit(2);
        }

        final Path filepath = Path.of(args[0]);
        final String contents = Files.readString(filepath);
        try {
            compile(CharStreams.fromString(contents));
        } catch (CompilationFailed failure) {
            // print the reason(s) for compilation failure
            final String[] lines = contents.split("\n");
            failure.errors.forEach(error -> {
                System.err.println(lines[error.line()]);
                final String space = " ".repeat(error.position());
                System.err.printf("%s^ %d:%d %s\n", space, error.line(), error.position(), error.message());
            });
        }
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
        final CCLParser.ProgramContext programContext = parse(chars);
        // 2. construct the ast
        final IdentityHashMap<Node, ParserRuleContext> sourceMap = new IdentityHashMap<>();
        final Program program = new AstBuilder(sourceMap::put).visitProgram(programContext);
        System.out.println(program);
        System.out.println(sourceMap);
        // 3. semantically analyse
    }
}
