# 构建AST

> 要用Visitor建立AST，就要自己写一个类BuildAstVisitor继承原有的类\<GRAMMAR_NAME>BaseVisitor \<Node>。

简单说，来一个class BuildAstVisitor extends BaseVisitor \<Node>

``` java
public class BuildASTVistor extends MxStarBaseVisitor<Node> {
  
}
```

> Node是AST的结点类，为了表示不同的结点，它有很多很多子类。


* [x] Node
  * [x] ProgramNode
  * [x] DefNode
    * [x] VarDefNode
    * [x] VarListNode
    * [x] VarNode
    * [x] FuncDefNode
    * [x] ClassDefNode
  * [x] StmtNode
    * [x] VarDefStmtNode
    * [x] ExprStmtNode
    * [x] IfStmtNode
    * [x] WhileStmtNode
    * [x] ForStmtNode
    * [x] ReturnStmtNode
    * [x] BreakStmtNode
    * [x] ContinueStmtNode
    * [x] BlockStmtNode
  * [x] ExprNode
    * [x] IdentifierExprNode
    * [x] ThisExprNode
    * [x] ConstExprNode
      * [x] BoolNode
      * [x] IntNode
      * [x] StringNode
      * [x] NullNode
    * [x] MemberExprNode
    * [x] ArrayExprNode
    * [x] FuncExprNode
    * [x] NewExprNode
    * [x] PostfixExprNode
    * [x] PrefixExprNode
    * [x] BinaryExprNode
  * [x] TypeNode
    * [x] PrimitiveTypeNode
    * [x] ClassTypeNode
    * [x] ArrayTypeNode