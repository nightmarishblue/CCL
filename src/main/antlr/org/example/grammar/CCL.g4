grammar CCL;

options { caseInsensitive = true; } // spec says the language is not case sensitive

program: declarationList functionList main; // (1)

declarationList: (declaration SEMICOLON)*; // (2)
declaration // (3)
    : KW_VAR variable # varDeclaration // (4)
    | KW_CONST variable ASSIGN expression # constDeclaration // (5)
    ;

functionList: function*; // (6)
function: type name=IDENTIFIER LEFT_BRACKET parameterList RIGHT_BRACKET LEFT_BRACE declarationList
    statementList KW_RETURN LEFT_BRACKET output=expression? RIGHT_BRACKET SEMICOLON RIGHT_BRACE; // (7)
functionCall: name=IDENTIFIER LEFT_BRACKET argumentList RIGHT_BRACKET;

type: value=(KW_INTEGER | KW_BOOLEAN | KW_VOID); // (8)

parameterList: (variable (COMMA variable)*)?; // (9)
variable: name=IDENTIFIER COLON type;

main: KW_MAIN LEFT_BRACE declarationList statementList RIGHT_BRACE; // (10)

statementList: statement*; // (11) (referred to as statementBlock in the spec)
statementBlock: LEFT_BRACE statementList RIGHT_BRACE;
statement // (12)
    : var=IDENTIFIER ASSIGN expression SEMICOLON # assignmentStatement
    | functionCall SEMICOLON # functionCallStatement
    | statementBlock # nestedBlockStatement
    | KW_IF condition then=statementBlock KW_ELSE else=statementBlock # ifStatement
    | KW_WHILE condition statementBlock # whileStatement
    | KW_SKIP SEMICOLON # skipStatement
    ;

expression // (13)
    // spec had fragment here, but after removing the mutual left recursion this would make nested arithmetic impossible
    : left=expression binaryArithmeticOperator right=expression # arithmeticExpression
    | LEFT_BRACKET expression RIGHT_BRACKET # subExpression
    | functionCall # functionCallExpression
    | atom # atomExpression
    ;

binaryArithmeticOperator: value=(PLUS | MINUS); // (14)

atom // (15) (referred to as fragment in the spec, which is unfortunately an ANTLR-reserved keyword)
    : unaryOperator? name=IDENTIFIER # referenceAtom
    | literal # literalAtom
    ; // spec contained a rule recursing back into expression; removed this

unaryOperator: value=MINUS; // only one unary operator for atoms

literal
    : value=NUMBER # integerLiteral
    | value=(KW_TRUE | KW_FALSE) # booleanLiteral
    ;

condition // (16)
    : TILDE condition # negatedCondition
    | LEFT_BRACKET condition RIGHT_BRACKET # subCondition
    | left=expression comparisonOperator right=expression # comparisonCondition
    | left=condition logicalOperator right=condition # compoundCondition
    ;

comparisonOperator: value=(EQUALS | NOT_EQUALS | LESS_THAN | LESS_EQUAL | GREATER_THAN | GREATER_EQUAL); // (17)
logicalOperator: value=(AND | OR);

argumentList: (names+=IDENTIFIER (COMMA names+=IDENTIFIER)*)?; // (18) (19)

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

BLOCK_COMMENT: '/*' .*? '*/' -> skip; // unsure what it means for a comment to be "nested," as they are all skipped
LINE_COMMENT: '//' .*? '\n' -> skip;

ILLEGAL: .; // grammar is not sound if we match this

fragment Digit: [0-9];
fragment Letter: [A-Z];