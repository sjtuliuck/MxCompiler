package com.ast;

public interface ASTVisitor {
    //
    void visit(ProgramNode node);
    //
//    void visit(DefNode node);
    void visit(VarDefNode node);
    void visit(ClassDefNode node);
    void visit(FuncDefNode node);
//    void visit(ConstructorDefNode node);
    // StmtNode
    void visit(VarDefStmtNode node);
    void visit(ExprStmtNode node);
    void visit(IfStmtNode node);
    void visit(WhileStmtNode node);
    void visit(ForStmtNode node);
    void visit(ReturnStmtNode node);
    void visit(BreakStmtNode node);
    void visit(ContinueStmtNode node);
    void visit(BlockStmtNode node);
    //
    void visit(ExprListNode node);
    // ExprNode
    void visit(IdentifierExprNode node);
    void visit(ThisExprNode node);
    void visit(MemberExprNode node);
    void visit(ArrayExprNode node);
    void visit(FuncExprNode node);
    void visit(NewExprNode node);
//    void visit(ConstructorExprNode node);
    void visit(PostfixExprNode node);
    void visit(PrefixExprNode node);
    void visit(BinaryExprNode node);
//    void visit(AssignExprNode node);
    // ConstExprNode
    void visit(BoolConstNode node);
    void visit(IntConstNode node);
    void visit(StringConstNode node);
    void visit(NullConstNode node);
    // TypeNode
    void visit(TypeNode node);
    // VarNode
    void visit(VarNode node);
    void visit(VarListNode node);
}