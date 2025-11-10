package org.example.ast;

import java.util.List;

public class Program extends Node {
    List<Declaration> declarations;
    List<Function> functions;

    Main main;
}
