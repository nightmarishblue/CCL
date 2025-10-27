grammar CCL;

options { caseInsensitive = true; }

prog: declarationList functionList main; // (1)

main: KW_MAIN LEFT_BRACE declarationList statementList RIGHT_BRACE; // (10)

declarationList: (declaration SEMICOLON)*; // (2)
declaration // (3)
    : KW_VAR variable # varDeclaration // (4)
    | KW_CONST variable ASSIGN expression # constDeclaration // (5)
    ;

functionList: function*; // (6)
function: type name=IDENTIFIER LEFT_BRACKET parameterList RIGHT_BRACKET LEFT_BRACE declarationList
    statementList KW_RETURN LEFT_BRACKET expression? RIGHT_BRACKET SEMICOLON RIGHT_BRACE; // (7)

statementList: statement*; // (11) (referred to as statementBlock in the spec)
statementBlock: LEFT_BRACE statementList RIGHT_BRACE;
statement // (12)
    : statementBlock # nestedBlockStatement
    | functionCall SEMICOLON # functionCallStatement
    | var=IDENTIFIER ASSIGN expression SEMICOLON # assignmentStatement
    | KW_SKIP SEMICOLON # skipStatement
    | KW_IF condition then=statementBlock KW_ELSE else=statementBlock # ifStatement
    | KW_WHILE condition statementBlock # whileStatement
    ;

condition // (16)
    : TILDE condition # negatedCondition
    | LEFT_BRACKET condition RIGHT_BRACKET # subCondition
    | left=expression comparisonOperator right=expression # comparisonCondition
    | left=condition logicalOperator right=condition # logicalCondition
    ;

expression // (13)
    : LEFT_BRACKET expression RIGHT_BRACKET # subExpression
    | left=expression binaryArithmeticOperator right=expression # arithmeticExpression
    | functionCall # functionCallExpression
    | fragment_ # primaryExpression
    ;

binaryArithmeticOperator: value=(PLUS | MINUS); // (14)
comparisonOperator: value=(EQUALS | NOT_EQUALS | LESS_THAN | LESS_EQUAL | GREATER_THAN | GREATER_EQUAL); // (17)
logicalOperator: value=(AND | OR);

fragment_: reference | boolean | number; // (15)
reference: negated=MINUS? name=IDENTIFIER;
boolean: value=(KW_TRUE | KW_FALSE);
number: value=NUMBER;

parameterList: (variable (COMMA variable)*)?; // (9)
argumentList: (names+=IDENTIFIER (COMMA names+=IDENTIFIER)*)?; // (18) (19)
functionCall: name=IDENTIFIER LEFT_BRACKET argumentList RIGHT_BRACKET;

type: value=(KW_INTEGER | KW_BOOLEAN | KW_VOID); // (8)
variable: name=IDENTIFIER COLON type;

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
NUMBER: '-'? [1-9] Digit*;
IDENTIFIER: (Letter | '_') (Letter | Digit | '_')*;

// ignored characters
WHITESPACE: [ \t\n\r]+ -> skip;

BLOCK_COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' .*? '\n' -> skip;

ILLEGAL: .; // grammar is not sound if we match this

fragment Digit: [0-9];
fragment Letter: [A-Z];