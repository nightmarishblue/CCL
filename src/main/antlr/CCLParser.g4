parser grammar CCLParser;

options { tokenVocab = CCLLexer; }

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