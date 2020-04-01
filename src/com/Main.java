package com;

import com.ast.ASTVisitor;
import com.ast.ProgramNode;
import com.frontend.ASTBuilder;
import com.frontend.ASTPrinter;
import com.parser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;


public class Main {
    public static void main(String[] args) throws Exception {
        String fileName;
        fileName = "test.mx";

        InputStream inputStream;
        CharStream input;
        try {
            inputStream = new FileInputStream(fileName);
            input = CharStreams.fromStream(inputStream);
        } catch (Exception exception) {
            System.err.println("Fail to open file!");
            return;
        }

        try {
            MxStarLexer lexer = new MxStarLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MxStarParser parser = new MxStarParser(tokens);
            ParseTree parseTree = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ProgramNode astRoot = (ProgramNode) astBuilder.visit(parseTree);
            //
        } catch (Exception exception) {
            System.out.println("Compile Error!");
        }

        System.out.println("AST finished!");
    }

}
