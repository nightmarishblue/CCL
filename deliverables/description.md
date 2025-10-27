# Grammar
The CCL grammar is split into `CCLParser.g4` and `CCLLexer.g4`.
## Parser
For reasons explained later, I split the grammar into multiple `.g4` files. The `tokenVocab` option gives the name of the lexer grammar.

The spec's rules for `<expression>` and `<fragment>` are mutually left-recursive, which I fixed by:
- Removing `<fragment>` -> `<expression>`
- Replacing `<expression>` -> `<fragment> <binary_arith_op> <fragment>` with `<expression>` -> `<expression> <binary_arith_op> <expression>`

The resulting grammar is ANTLR-legal and should be just as powerful.

## Lexer 
CCL's spec states the language is case-insensitive, which I specify in `options`.

All reserved keywords are defined first, so as to give them precedence over identifiers.
The spec's rules for integer literals would, if followed to the letter, forbid the use of literal `0`, so I made a special exception in that lexer rule.

The spec also states that block comments may be nested. As this cannot be done without a stack, I introduce the mode `BLOCK_COMMENT`. This is [illegal in combined grammars](https://github.com/antlr/antlr4/blob/master/doc/lexer-rules.md#lexical-modes), which necessitated the split.
# Program
The assignment states that our program should read the contents of the file given by the first argument and parse it, printing a binary message indicating success or failure.

ANTLR's generated lexers and parsers by default attempt to continue past errors, so I override said behaviour by removing the default `ConsoleErrorListener`s (which print to `stderr`) and using the `BailErrorStrategy`.
Now, when the parser encounters an error, it immediately stops and throws. This isn't exactly Swiss in its precision, but it is sufficient for the assignment's requirements.