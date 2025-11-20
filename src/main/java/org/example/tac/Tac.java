package org.example.tac;

import org.example.ast.data.Identifier;
import org.example.lang.SemanticAnalyser;
import org.example.lang.Type;

import java.util.List;
import java.util.Map;

public class Tac {
    public static final Map<Identifier, SemanticAnalyser.Data.Function> BUILTINS = Map.of(
            new Identifier("_exit"), new SemanticAnalyser.Data.Function(Type.VOID, List.of()),
            new Identifier("_read"), new SemanticAnalyser.Data.Function(Type.ANY, List.of()),
            new Identifier("_print"), new SemanticAnalyser.Data.Function(Type.VOID, List.of(Type.ANY)),
            new Identifier("_println"), new SemanticAnalyser.Data.Function(Type.VOID, List.of(Type.ANY))
    );
}
