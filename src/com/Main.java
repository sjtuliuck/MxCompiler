package com;

import com.ast.*;
import com.frontend.ASTBuilder;
import com.parser.*;
import com.utility.ErrorHandler;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.SyntaxTree;

import java.io.FileInputStream;
import java.io.InputStream;


public class Main {
    public static void main(String[] args) throws Exception {
        ErrorHandler errorHandler = new ErrorHandler();

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
            ASTBuilder astBuilder = new ASTBuilder();
        } catch (Exception exception) {
            System.out.println("Compile Error!");
        }

        if (errorHandler.getCnt() > 0) {
            System.err.println("Compiler Error!");
        } else {
            System.out.println("AST finished!");
        }
    }

}
