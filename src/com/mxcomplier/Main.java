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
        if (args.length > 0)
            if (args[0].equals("-DEBUG"))
                Config.DEBUG = true;
        try {
            InputStream codeInput;
            if (Config.DEBUG)
                codeInput = new FileInputStream("testcases/testcase.mx");
            else
                codeInput = System.in;

            CharStream charInput = CharStreams.fromStream(codeInput);
            MxStarLexer lexer = new MxStarLexer(charInput);
            CommonTokenStream token = new CommonTokenStream(lexer);
            MxStarParser parser = new MxStarParser(token);
            parser.setErrorHandler(new BailErrorStrategy());
            ParseTree tree = parser.program();

            ProgramNode ast = (ProgramNode) new ASTBuilder().visit(tree);
            new ScopePrepareASTScanner().visit(ast);
            new ScopeClassMemberASTScanner().visit(ast);
            new ScopeBuilderASTScanner().visit(ast);

            IRBuilder irBuilder = new IRBuilder();
            irBuilder.visit(ast);

            new FuncInliner().run(irBuilder);
            new IRfixer().visit((irBuilder.root));
            if (Config.DEBUG) {
                new IRPrinter(irBuilder).visit(irBuilder.root);
//            IRInterpreter interpreter = new IRInterpreter(irBuilder);
//            interpreter.run();
            }
            new BlockMerger(true).visit(irBuilder.root);

            new GraphAllocator().run(irBuilder);
            new StackFrameAllocater().visit(irBuilder.root);
            new BlockMerger(false).visit(irBuilder.root);
            new NasmPrinter(irBuilder).visit(irBuilder.root);

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
