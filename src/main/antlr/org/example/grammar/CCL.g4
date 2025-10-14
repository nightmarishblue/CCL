grammar CCL;

options { caseInsensitive = true; }

prog: declarationList functionList main; // (1)

main: MAIN LEFT_BRACKET declarationList statementBlock RIGHT_BRACKET; // (10)

declarationList: (declaration SEMICOLON)*; // (2)

declaration: varDeclaration | constDeclaration; // (3)
varDeclaration: VAR name=IDENTIFIER COLON type; // (4)
constDeclaration: CONST name=IDENTIFIER COLON type ASSIGN expression; // (5)

functionList: function*; // (6)

function: type name=IDENTIFIER LEFT_PAREN parameterList RIGHT_PAREN LEFT_BRACKET declarationList
    statementBlock RETURN LEFT_PAREN expression? RIGHT_PAREN SEMICOLON RIGHT_BRACKET; // (7)

type: value=(INTEGER | BOOLEAN | VOID); // (8)

// TODO
expression:;
parameterList:;
statementBlock:;

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

LEFT_BRACKET: '{';
RIGHT_BRACKET: '}';
LEFT_PAREN: '(';
RIGHT_PAREN: ')';
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