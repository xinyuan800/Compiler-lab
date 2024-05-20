parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;//注意使用该语句指定词法分析器；请不要修改词法分析器或语法分析器的文件名，否则Makefile可能无法正常工作，影响评测结果
}

program
   : compUnit
   ;
compUnit
   : (funcDef | decl)+ EOF
   ;
decl
    :constDecl | varDecl
    ;
constDecl
    : CONST bType constDef (COMMA constDef)* SEMICOLON
    ;
bType
    : INT
    ;
constDef
    : IDENT (L_BRACKT constExp R_BRACKT)*ASSIGN constInitVal
    ;
constInitVal
    : constExp
    | L_BRACE(constInitVal(COMMA constInitVal)*)?R_BRACE
    ;
varDecl
    : bType varDef(COMMA varDef)*SEMICOLON
    ;
varDef
    : IDENT (L_BRACKT constExp R_BRACKT)*
    | IDENT (L_BRACKT constExp R_BRACKT)* ASSIGN initVal
    ;
initVal
    : exp
    | L_BRACE (initVal(COMMA initVal)*)? R_BRACE
    ;
funcDef
    : funcType IDENT L_PAREN(funcFParams)?R_PAREN block
    ;
funcType
    : VOID
    | INT
    ;
funcFParams
    : funcFParam(COMMA funcFParam)*
    ;
funcFParam
    : bType IDENT (L_BRACKT R_BRACKT(L_BRACKT exp R_BRACKT)*)?
    ;
block
    : L_BRACE(blockItem)* R_BRACE
    ;
blockItem
    : decl
    | stmt
    ;
stmt
    :lVal ASSIGN exp SEMICOLON #stmt1
    | (exp)? SEMICOLON #stmt2
    | block #block1
     | IF L_PAREN cond R_PAREN stmt ( ELSE stmt )? #ifStmt
     | WHILE L_PAREN cond R_PAREN stmt #whileStmt
     |  BREAK SEMICOLON #stmt3
     | CONTINUE SEMICOLON #stmt4
     | RETURN (exp)? SEMICOLON #stmt5
     ;
exp
   : L_PAREN exp R_PAREN #exp1
   | lVal#exp2
   | number#num
   | IDENT L_PAREN funcRParams? R_PAREN#funCall
   | unaryOp exp#exp3
   | exp (MUL | DIV | MOD) exp#exp4
   | exp (PLUS | MINUS) exp#exp5
   ;

cond
   : exp
   | cond (LT | GT | LE | GE) cond
   | cond (EQ | NEQ) cond
   | cond AND cond
   | cond OR cond
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)*
   ;

number
   : INTEGER_CONST
   ;

unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   ;

constExp
   : exp
   ;