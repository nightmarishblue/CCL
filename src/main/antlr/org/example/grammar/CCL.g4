grammar CCL;

options { caseInsensitive = true; }

prog: EXAMPLE;

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