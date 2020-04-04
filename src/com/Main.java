package com;

import com.ast.ProgramNode;
import com.frontend.ASTBuilder;
import com.frontend.ASTPrinter;
import com.frontend.Scope;
import com.frontend.SemanticChecker;
import com.parser.*;
import com.utility.ErrorHandler;
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

        ErrorHandler errorHandler = new ErrorHandler();
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
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxStarErrorListener(errorHandler));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MxStarParser parser = new MxStarParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MxStarErrorListener(errorHandler));
            ParseTree parseTree = parser.program();
            if (errorHandler.getErrorCnt() > 0) {
                throw new RuntimeException();
            }
            out.println("ParseTree finished!");
            //
            ASTBuilder astBuilder = new ASTBuilder(null);
            ProgramNode astRoot = (ProgramNode) astBuilder.visit(parseTree);
            out.println("AST finished!");
            //
            boolean printAST = false;
            if (printAST) {
                ASTPrinter astPrinter = new ASTPrinter();
                astPrinter.visit(astRoot);
                return;
            }
            //
            Scope globalScope = new Scope(null);
            SemanticChecker semanticChecker = new SemanticChecker(globalScope);
            semanticChecker.visit(astRoot);
            out.println("Semantic finished!");
        } catch (Exception exception) {
            err.println(exception);
            System.exit(1);
        }
    }

}
