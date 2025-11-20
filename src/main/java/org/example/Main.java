package org.example;


import org.antlr.v4.runtime.*;
import org.example.antlr.AstBuilder;
import org.example.antlr.CollectingErrorListener;
import org.example.antlr.SourceError;
import org.example.ast.node.Node;
import org.example.ast.node.Program;
import org.example.grammar.CCLLexer;
import org.example.grammar.CCLParser;
import org.example.antlr.CompilationFailed;
import org.example.lang.SemanticAnalyser;
import org.example.tac.Quad;
import org.example.tac.Tac;
import org.example.tac.TacTranslator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: insufficient arguments");
            System.err.printf("Usage: %s <input> [output]\n", Main.class.getName());
            System.exit(2);
        }


        OutputStream output;
        String contents;

        try {
            contents = Files.readString(Path.of(args[0]));
            output = (args.length >= 2) ? new FileOutputStream(args[1]) : System.out;
        } catch (FileNotFoundException e) {
            System.err.printf("Error: file %s not found\n", e.getMessage());
            System.exit(1);
            return;
        } catch (IOException ioError) {
            System.err.printf("Error: I/O exception reading file '%s'\n", ioError.getLocalizedMessage());
            System.exit(1);
            return;
        }

        PrintWriter writer = new PrintWriter(output, true);
        List<Quad> tac;

        try {
            tac = compile(CharStreams.fromString(contents));
        } catch (CompilationFailed failure) {
            // print the reason(s) for compilation failure
            final String[] lines = contents.split("\n");
            failure.errors.forEach(error -> {
                System.err.println(lines[error.line()]);
                final String space = " ".repeat(error.position());
                System.err.printf("%s^ %d:%d %s\n", space, error.line() + 1, error.position(), error.message());
            });
            return;
        }

        tac.stream().map(Quad::instruction).forEach(writer::println);
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

    // build the AST from a parsed program context
    // throw if the tree is incorrect
    public static Program construct(CCLParser.ProgramContext programContext) {
        final IdentityHashMap<Node, ParserRuleContext> sourceMap = new IdentityHashMap<>();
        final Program program = new AstBuilder(sourceMap::put).visitProgram(programContext);

        // semantically analyse (stop if errors)
        ArrayList<SourceError> errors = new ArrayList<>();
        SemanticAnalyser checker = new SemanticAnalyser((node, msg) -> {
            ParserRuleContext context = sourceMap.get(node);
            Token token = context.getStart();
            errors.add(new SourceError(token, msg));
        }, Tac.BUILTINS);
        checker.visit(program);

        if (!errors.isEmpty()) throw new CompilationFailed(errors);
        return program;
    }


    public static List<Quad> compile(CharStream chars) {
        // compiling a file consists of
        // 1. parse the file (stop if errors)
        final CCLParser.ProgramContext programContext = parse(chars);

        // 2. construct the ast (and 3., semantically verify it)
        final Program program = construct(programContext);

        // 3.5 apply optimisations if desired

        // 4. translate
        List<Quad> output = new ArrayList<>();
        new TacTranslator(output::add).visit(program);
        return output;
    }
}
