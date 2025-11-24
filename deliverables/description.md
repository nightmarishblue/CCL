Contents taken from my [repository](https://gitlab.computing.dcu.ie/bastibn2/CCL). Unfortunately, this was too complex to compile by hand; instructions given below.
# How to use
```sh
./gradlew installDist
./build/install/CCL/bin/CCL <source> [destination] [-p]
```
or alternatively
```sh
./gradlew run --args="<source> [destination] [-p]"
```
# Abstract Syntax Tree
The first step once I had a concrete parse tree was to convert it into an abstract syntax tree. Unfortunately ANTLR removed support for this in v4, so I had to design my own.

I created a hierarchy of `Node` classes and the `AstBuilder` visitor for the parse tree to construct a program's tree. In order to print useful information upon encountering errors, I associate every `Node` to its original source position in an `IdentityHashMap` using a consumer exposed by `AstBuilder`. This allowed me to keep the AST nodes pure of metadata.
I also created some supporting data types, such as the enum `Type` to model the language's type system and an `Identifier` to represent the language's case-insensitive names.
Finally, I built `AstVisitor<T>`, an abstract visitor class for the AST, allowing consumers to work with the `Node`s.

This was my first time designing an AST, so there were obviously mistakes I couldn't identify while writing. Some of these I was able to easily refactor in the later stages when I became a consumer of this API, and some would have necessitated a rewrite.
Were I to do so again, I would stick to the rule of each node having at most one element of data, with anything else inside its children. This would have made for DRY-er code later, as I found myself needing to duplicate some logic when visiting the AST.
I also made the mistake of sticking very rigidly to the grammar. I followed its structure as a model for the node hierarchy, and included only the types addressable by the grammar. This constrained me later, especially when I had to support some form of polymorphism.

Using an AST had some benefits asides from the obvious. I found that it made adding syntactic shorthand or "sugar" to the language's grammar very easily, implementing operators `+=` and `-=` in a few lines of code.
# Symbol Table
The symbol table was comparatively far quicker. Each scope is represented by a hash map, which reside inside a stack.
- When adding another scope, an empty map is added to the top of the stack. When removing the current scope, it is popped.
- When querying a symbol, the stack is searched top to bottom until the first scope containing it is found.
  There are other approaches, such as copying the previous map when adding scopes, which avoids linear searching but does not allow for an inner scope to modify outer ones. I can't claim my approach is any better as it is likely dependent on use-case.
# Semantic Analysis
Performed by building an implementation for `AstVisitor`, which internally makes use of `SymbolTable` to verify scope. When a mistake is found, the offending `Node` is passed to a consumer along with a message; this allows me to find the source position in the external map and print to stderr.
A non-exhaustive list of syntactic constructs that will fail semantic verification and stop compilation:
- Variables and function parameters declared with `void` type.
- Invoking or referencing an undeclared identifier.
- Declaring two or more variables/parameters with the same identifier.
    - Shadowing identifiers from outer scope is allowed.
- Invoking a function with incorrect arguments.
- Invoking a variable rather than function.
    - Also, referencing a function rather than a variable.
- Supplying non-integer arguments to an arithmetic or arithmetic comparison operator.
- Supplying arguments of mismatched type to an equality operator.
- Returning an expression of incorrect type in a function.
- Assigning an expression of incorrect type to a variable.
- Reassigning a constant.
- Reading from a variable that has yet to be assigned.
    - There is no control-flow analysis. As long as one code path or function assigns a variable, we can only trust the programmer.

Whether or not function parameters can be mutated was left to the implementation. I decided to allow it for space optimisation purposes.
I also added an experimental analyser to prune unused variables and functions from the tree. My approach is naïve however, so it is only enabled if the user passes `-p`. I think these sorts of optimisations may be better suited for applying to three-address code.
# 3-Address Code
The `TacTranslator` is another implementation of my visitor. It targets the 3AC interpreter given in the assignment description. We define two basic types, `Quad` and `Address`. `Quad` is a basic 4-tuple of one operation and three `Address`es, with the latter two being optional.
This approach is taken from the notes, though I think a sum type approach would have worked better. I did not use triples, as the interpreter does not support them.

According to the provided manual for `TACi`, addresses can be either a constant (literal) value, or an alphanumeric name – therefore `Address` has two variants. I did not distinguish temporary variables in the type system, but it could be extended to support them for optimisation purposes.

Visited nodes emit `Quad`s via consumer. Visited `Expression`s may also return an `Address` to the parent `Node` containing the value of their result. I encountered an unfortunate quirk of the 3AC dialect when implementing conditions:
- Relational operators are allowed only in conditional jumps
- Logical operators are allowed only in assignments.

This made it impossible to dispatch to the `visitCondition` method and maintain separation of concerns. Were relational operators defined in assignments, like `a = b < c`, `Condition`s could return the `Address` of the variable holding their true/false state, and the enclosing `IfElse` or `While` nodes could emit a conditional jump referencing it.
Instead, the parent node must emit the instructions for the condition. My chosen solution was to omit logical operators entirely, and emit multiple conditional jumps. This is how compound conditions work in assembly.
