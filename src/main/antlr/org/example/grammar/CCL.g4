grammar CCL;

options { caseInsensitive = true; }

prog: declarationList functionList main; // (1)

main: MAIN LEFT_BRACE declarationList statementList RIGHT_BRACE; // (10)

declarationList: (declaration SEMICOLON)*; // (2)
declaration: VAR variable # varDeclaration // (4)
    | CONST variable ASSIGN expression # constDeclaration // (5)
    ; // (3)

functionList: function*; // (6)
function: type name=IDENTIFIER LEFT_BRACKET parameterList RIGHT_BRACKET LEFT_BRACE declarationList
    statementList RETURN LEFT_BRACKET expression? RIGHT_BRACKET SEMICOLON RIGHT_BRACE; // (7)

statementList: statement*; // (11)
statementBlock: LEFT_BRACE statementList RIGHT_BRACE;
statement: statementBlock
    | (assignment | functionCall | skip_) SEMICOLON
    | ifElse
    | whileLoop
    ; // (12)

assignment: var=IDENTIFIER ASSIGN expression;

ifElse: IF condition then=statementBlock ELSE else=statementBlock;
whileLoop: WHILE condition statementBlock;
skip_: SKIP_;

expression: left=fragment_ binary_operator right=fragment_
    | LEFT_BRACKET expression RIGHT_BRACKET
    | functionCall
    | fragment_
    ;

binary_operator: (PLUS | MINUS); // (14)

parameterList: (variable (COMMA variable)*)?; // (9)
argumentList: (names+=IDENTIFIER (COMMA names+=IDENTIFIER)*)?; // (18) (19)
functionCall: name=IDENTIFIER LEFT_BRACKET argumentList RIGHT_BRACKET;

type: value=(INTEGER | BOOLEAN | VOID); // (8)
variable: name=IDENTIFIER COLON type;

// TODO
condition:;
fragment_:;

// reserved keywords
MAIN: 'MAIN';
RETURN: 'RETURN';

INTEGER: 'INTEGER';
BOOLEAN: 'BOOLEAN';
VOID: 'VOID';

VAR: 'VAR';
CONST: 'CONST';

IF: 'IF';
ELSE: 'ELSE';

TRUE: 'TRUE';
FALSE: 'FALSE';

WHILE: 'WHILE';
SKIP_: 'SKIP';

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
INTEGER_LITERAL: '-'? [1-9] Digit*;
IDENTIFIER: (Letter | '_') (Letter | Digit | '_')*;

// ignored characters
WHITESPACE: [ \t\n\r]+ -> skip;

BLOCK_COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' .*? '\n' -> skip;

ILLEGAL: .; // grammar is not sound if we match this

fragment Digit: [0-9];
fragment Letter: [A-Z];