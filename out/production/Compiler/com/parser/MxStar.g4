grammar MxStar;

@header {
package com.parser;
}

program: def*;

def: varDef | classDef | funcDef;

//---------------------- definition -------------------------------------------
varDef: type varList ';';
//
classDef:
	Class Identifier '{' (varDef | funcDef | constructorDef)* '}' ';';
//
funcDef: (type | Void) Identifier '(' ( paramList)? ')' block;
//
constructorDef: Identifier '(' paramList? ')' block;

//
varList: var (',' var)*;
var: Identifier ('=' expr)?;
//
paramList: param (',' param)*;
param: type Identifier;
//

//------------------------ type -----------------------------------------------
type: type '[' ']'
    | nonArrayType
    ;


nonArrayType: Bool | Int | String | Identifier;

//---------------------------- statement ------------------------------------------

block: '{' stmt* '}';

stmt:
	varDef																# varDefStmt
	| expr ';'															# exprStmt
	| If '(' expr ')' thenStmt = stmt (Else elseStmt = stmt)?			# ifStmt
	| While '(' expr ')' stmt											# whileStmt
	| For '(' init = expr? ';' cond = expr? ';' step = expr? ')' stmt	# forStmt
	| Return expr? ';'													# returnStmt
	| Break ';'															# breakStmt
	| Continue ';'														# continueStmt
	| block																# blockStmt
	| ';'																# emptyStmt;

//--------------------------- expression ------------------------------------------

exprList: expr (',' expr)*;

expr:
	Identifier												# identifierExpr
	| This													# thisExpr
	| constant												# constExpr
	| '(' expr ')'											# parenExpr
	| expr '.' Identifier									# memberExpr
	| array = expr '[' idx = expr ']'						# arrayExpr
	| expr '(' exprList? ')'								# funcExpr
	| <assoc = right> New creator							# newExpr
	| expr op = ('++' | '--')								# postfixExpr
	| <assoc = right> op = ('++' | '--') expr				# prefixExpr
	| <assoc = right> op = ('+' | '-') expr					# prefixExpr
	| <assoc = right> op = ('~' | '!') expr					# prefixExpr
	| lhs = expr bop = ('*' | '/' | '%') rhs = expr			# binaryExpr
	| lhs = expr bop = ('+' | '-') rhs = expr				# binaryExpr
	| lhs = expr bop = ('>>' | '<<') rhs = expr				# binaryExpr
	| lhs = expr bop = ('<' | '>' | '<=' | '>=') rhs = expr	# binaryExpr
	| lhs = expr bop = ('!=' | '==') rhs = expr				# binaryExpr
	| lhs = expr bop = '&' rhs = expr						# binaryExpr
	| lhs = expr bop = '^' rhs = expr						# binaryExpr
	| lhs = expr bop = '|' rhs = expr						# binaryExpr
	| lhs = expr bop = '&&' rhs = expr						# binaryExpr
	| lhs = expr bop = '||' rhs = expr						# binaryExpr
	| <assoc = right> lhs = expr bop = '=' rhs = expr		# binaryExpr;

constant: BoolConst | IntConst | StringConst | NullConst;

creator:
	nonArrayType ('[' expr ']')* ('[' ']')+ ('[' expr ']')+	    # errorCreator
	| nonArrayType ('[' expr ']')+ ('[' ']')*					# arrayCreator
	| nonArrayType ('(' ')')?								    # nonArrayCreator;

//---------------------- reserved keyword --------------------------------------------
Bool: 'bool';
Int: 'int';
String: 'string';
Void: 'void';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';
New: 'new';
Class: 'class';
This: 'this';
//-------------------------------------------------------------------------------------

// white space
WhiteSpace: [ \t]+ -> skip;
// new line
NewLine: ('\r' '\n'? | '\n') -> skip;
// comment
LineComment: '//' ~ [\r\n]* -> skip;
BlockComment: '/*' .*? '*/' -> skip;

// constant
BoolConst: 'true' | 'false';
IntConst: [1-9] [0-9]* | '0';
StringConst: '"' (~["\\\r\n] | '\\' ["n\\])* '"';
NullConst: 'null';

// identifier
Identifier: [a-zA-Z] [a-zA-Z0-9_]*;


