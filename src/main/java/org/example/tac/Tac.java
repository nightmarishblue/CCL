package org.example.tac;

import org.example.ast.data.Identifier;

import java.util.Set;
import java.util.stream.Collectors;

public class Tac {
    public static final Set<Identifier> BUILTINS = Set.of("_exit", "_read", "_print", "_println")
            .stream().map(Identifier::new).collect(Collectors.toSet());
}
