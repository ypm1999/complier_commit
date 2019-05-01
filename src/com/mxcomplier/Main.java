package com.mxcomplier;

import com.mxcomplier.AST.ProgramNode;
import com.mxcomplier.Error.ComplierError;
import com.mxcomplier.FrontEnd.*;
import com.mxcomplier.LaxerParser.MxStarLexer;
import com.mxcomplier.LaxerParser.MxStarParser;
import com.mxcomplier.backEnd.*;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {
        try {
            InputStream codeInput = System.in;
//            InputStream codeInput = new FileInputStream("testcases/testcase.mx");
            CharStream charInput = CharStreams.fromStream(codeInput);
            MxStarLexer lexer = new MxStarLexer(charInput);
            CommonTokenStream token = new CommonTokenStream(lexer);
            MxStarParser parser = new MxStarParser(token);
            parser.setErrorHandler(new BailErrorStrategy());
            ParseTree tree = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ProgramNode ast = (ProgramNode) astBuilder.visit(tree);
            ScopePrepareASTScanner scanner1 = new ScopePrepareASTScanner();
            ScopeClassMemberASTScanner scanner2 = new ScopeClassMemberASTScanner();
            ScopeBuilderASTScanner scanner3 = new ScopeBuilderASTScanner();
            scanner1.visit(ast);
            scanner2.visit(ast);
            scanner3.visit(ast);

            IRBuilder irBuilder = new IRBuilder();
            IRPrinter irPrinter = new IRPrinter(irBuilder);

            IRfixer iRfixer = new IRfixer();
            RegisterAllocater allocater = new RegisterAllocater();
            StackFrameAllocater stackFrameAllocater = new StackFrameAllocater();
            irBuilder.visit(ast);
//            irPrinter.visit(irBuilder.root);
//            IRInterpreter interpreter = new IRInterpreter(irBuilder);
//            interpreter.run();
            iRfixer.visit((irBuilder.root));
            allocater.visit(irBuilder.root);

            stackFrameAllocater.visit(irBuilder.root);
            iRfixer.visit((irBuilder.root));
            NasmPrinter nasmPrinter = new NasmPrinter(irBuilder);
            nasmPrinter.visit(irBuilder.root);

        } catch (ComplierError e) {
            System.err.println("Complier Failed!");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("input file not exist!");
            System.exit(-1);
        } catch (ParseCancellationException e) {
            System.err.println("parser exception!");
            System.exit(-1);
        }
    }
}
