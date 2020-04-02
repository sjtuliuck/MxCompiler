package com;

import com.ast.ASTVisitor;
import com.ast.ProgramNode;
import com.frontend.ASTBuilder;
import com.frontend.ASTPrinter;
import com.frontend.Scope;
import com.frontend.semantic.ClassScanner;
import com.frontend.semantic.GlobalScanner;
import com.frontend.semantic.ScopeBuilder;
import com.parser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;


public class Main {
    public static void main(String[] args) throws Exception {
        PrintStream err = System.err;
        PrintStream out = System.out;
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
            parser.setErrorHandler(new BailErrorStrategy());
            ParseTree parseTree = parser.program();
            out.println("ParseTree finished!");
            //
            ASTBuilder astBuilder = new ASTBuilder(null);
            ProgramNode astRoot = (ProgramNode) astBuilder.visit(parseTree);
            out.println("AST finished!");
            //
            Scope globalScope = new Scope(null);
            GlobalScanner globalScanner = new GlobalScanner(globalScope);
            globalScanner.visit(astRoot);
            ClassScanner classScanner = new ClassScanner(globalScope);
            classScanner.visit(astRoot);
            ScopeBuilder scopeBuilder = new ScopeBuilder(globalScope);
            scopeBuilder.visit(astRoot);
            out.println("Semantic finished!");
        } catch (Exception exception) {
            err.println(exception);
            System.exit(1);
        }
    }

}
