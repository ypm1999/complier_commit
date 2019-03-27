
grammar MxStar;

program
    :   sections*  EOF
    ;

sections
    :   functionDefinition
    |   classDefinition
    |   declarationStatement
    ;

functionDefinition
    :   (typeOrVoid)? identifier '(' declarationList? ')' compoundStatement
    ;

classDefinition
    :   Class identifier '{' classStatement '}'
    ;

classStatement
    :   (functionDefinition | declarationStatement)*
    ;

declarationList
    :   declaration (',' declaration)*
    ;

declaration
    :   type identifier ('=' expression)?
    ;


// Expression build
primaryExpression
    :   primaryExpression '[' expression ']'                            # arrayCallExpr
    |   primaryExpression '(' argumentExpressionList? ')'               # functionCallExpr
    |   primaryExpression '.' bracketIdentifier                         # memberCallExpr
    |   This                                                            # thisExpr
    |   identifier                                                      # identifierExpr
    |   constant                                                        # constantExpr
    |   '(' expression ')'                                              # subExpr
    ;

argumentExpressionList
    :   expression (',' expression)*
    ;

expression
    :   primaryExpression                                               # primaryExpr
    |   expression op = ('++' | '--')                                   # suffixIncDec
    |   <assoc = right> op = ('++' | '--') expression                   # prefixExpr
    |   <assoc = right> op = ('+' | '-' | '!' | '~') expression         # prefixExpr
    |   newExpression                                                   # newExpr
    |   exp1=expression op = ('*' | '/' | '%') exp2=expression          # binaryExpr
    |   exp1=expression op = ('+' | '-') exp2=expression                # binaryExpr
    |   exp1=expression op = ('<<' | '>>') exp2=expression              # binaryExpr
    |   exp1=expression op = ('<' | '>' | '<=' | '>=') exp2=expression  # binaryExpr
    |   exp1=expression op = ('==' | '!=') exp2=expression              # binaryExpr
    |   exp1=expression op = '&' exp2=expression                        # binaryExpr
    |   exp1=expression op = '^' exp2=expression                        # binaryExpr
    |   exp1=expression op = '|' exp2=expression                        # binaryExpr
    |   exp1=expression op = '&&' exp2=expression                       # binaryExpr
    |   exp1=expression op = '||' exp2=expression                       # binaryExpr
    |   <assoc=right> primaryExpression op = '=' expression             # assignExpr
    ;

newExpression
    :   New baseType ('[' expression ']')+ ('[' ']')*
    |   New baseType ( '('  ')' )?
    ;

statement
    :   compoundStatement
    |   expressionStatement
    |   selectionStatement
    |   iterationStatement
    |   jumpStatement
    ;

compoundStatement
    :   '{' compoundStatementItem* '}'
    ;

compoundStatementItem
    :   statement
    |   declarationStatement
    ;


declarationStatement
    :   declaration ';'
    ;

expressionStatement
    :   expression? ';'
    ;

selectionStatement
    :   If '(' expression ')' thenStmt = statement (Else elseStmt = statement)?
    ;

iterationStatement
    :   While '(' expression ')' statement                  #whileStatement
    |   For '(' forCondition ')' statement                  #forStatement
    ;

forCondition
	:   exp1 = expression? ';' exp2 = expression? ';' exp3 = expression?
	;

jumpStatement
    :   Return expression? ';'              #reutrnStmt
    |   Continue ';'                        #continueStmt
    |   Break ';'                           #breakStmt
    ;

identifier : Identifier;

baseType
    :   identifier
    |   Int
    |   Bool
    |   String
    ;

type
    :   baseType
    |   type '[' ']'
    ;

typeOrVoid
    :   Void
    |   type
    ;


bracketIdentifier
    :   identifier
    |   '(' bracketIdentifier ')'
    ;



constant
    :   True                        #boolConst
    |   False                       #boolConst
    |   Null                        #nullConst
    |   IntegerConstant             #intConst
    |   CharacterConstant           #stringConst
    ;

Null                : 'null';
True                : 'true';
False               : 'false';
This                : 'this';
Void                : 'void';
Int                 : 'int';
String              : 'string';
Bool                : 'bool';
New                 : 'new';
Class               : 'class';
Return              : 'return';
Continue            : 'continue';
Break               : 'break';
Else                : 'else';
For                 : 'for';
While               : 'while';
If                  : 'if';


LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
PlusPlus : '++';
Minus : '-';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';

Equal : '==';
NotEqual : '!=';

Dot : '.';



Identifier
    :   Nondigit        (   Nondigit
        |   Digit
        |   '_'
        )*
    ;


IntegerConstant
    :   '0'
    |   NonzeroDigit Digit*
    ;

Nondigit
    :   [a-zA-Z]
    ;

Digit
    :   [0-9]
    ;

NonzeroDigit
    :   [1-9]
    ;

CharacterConstant
    :   '"' CCharSequence? '"'
    ;

fragment CCharSequence
    :   CChar+
    ;

fragment CChar
    :   EscapeSequence
    |   ~["\\\r\n]
    ;

fragment EscapeSequence
    :   '\\' ["nrt\\]
    ;	                     

Whitespace
    :   [ \t]+ -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        ) -> skip
    ;

BlockComment
    :   '/*' .*? '*/' -> skip
    ;

LineComment
    :   '//' ~[\r\n]* -> skip
    ;