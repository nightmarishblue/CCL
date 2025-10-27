lexer grammar CCLLexer;

options { caseInsensitive = true; } // spec says the language is not case sensitive

fragment Digit: [0-9];
fragment Letter: [A-Z];

// reserved keywords
KW_MAIN: 'MAIN';
KW_RETURN: 'RETURN';

KW_INTEGER: 'INTEGER';
KW_BOOLEAN: 'BOOLEAN';
KW_VOID: 'VOID';

KW_VAR: 'VAR';
KW_CONST: 'CONST';

KW_IF: 'IF';
KW_ELSE: 'ELSE';

KW_TRUE: 'TRUE';
KW_FALSE: 'FALSE';

KW_WHILE: 'WHILE';
KW_SKIP: 'SKIP';

// symbols & operators
COMMA: ',';
SEMICOLON: ';';
COLON: ':';

ASSIGN: '=';

LEFT_BRACE: '{';
RIGHT_BRACE: '}';
LEFT_BRACKET: '(';
RIGHT_BRACKET: ')';
// arithmetic
PLUS: '+';
MINUS: '-';
TILDE: '~';
// logical
OR: '||';
AND: '&&';
// comparison
EQUALS: '==';
NOT_EQUALS: '!=';
LESS_THAN: '<';
GREATER_THAN: '>';
LESS_EQUAL: '<=';
GREATER_EQUAL: '>=';

// identifiers & literals
NUMBER: '0' | '-'? [1-9] Digit*; // rules-as-written the spec would disallow literal 0, which I have opted not to do
IDENTIFIER: (Letter | '_') (Letter | Digit | '_')*;

// ignored characters
WHITESPACE: [ \t\n\r]+ -> skip;
LINE_COMMENT: '//' .*? '\n' -> skip;

ILLEGAL: .; // grammar is not sound if we match this

BLOCK_COMMENT_START: '/*' -> pushMode(BLOCK_COMMENT), skip;

mode BLOCK_COMMENT;

NESTED_BLOCK_COMMENT_START: '/*' -> pushMode(BLOCK_COMMENT), skip;

BLOCK_COMMENT_END: '*/' -> popMode, skip;

BLOCK_COMMENT_CONTENT: . -> skip;
