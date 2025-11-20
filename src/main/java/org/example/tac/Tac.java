package org.example.tac;

import org.example.ast.data.Identifier;
import org.example.lang.SemanticAnalyser;

import java.util.List;
import java.util.Map;

public class Tac {
    public static final Map<Identifier, SemanticAnalyser.Data.Function> BUILTINS = Map.of(
            new Identifier("_exit"), new SemanticAnalyser.Data.Function(SemanticAnalyser.Type.VOID, List.of()),
            new Identifier("_read"), new SemanticAnalyser.Data.Function(SemanticAnalyser.Type.ANY, List.of()),
            new Identifier("_print"), new SemanticAnalyser.Data.Function(SemanticAnalyser.Type.VOID, List.of(SemanticAnalyser.Type.ANY)),
            new Identifier("_println"), new SemanticAnalyser.Data.Function(SemanticAnalyser.Type.VOID, List.of(SemanticAnalyser.Type.ANY))
    );
}
