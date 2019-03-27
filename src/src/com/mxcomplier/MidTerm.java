package com.mxcomplier;

import com.mxcomplier.AST.ProgramNode;
import com.mxcomplier.FrontEnd.ASTBuilder;
import com.mxcomplier.FrontEnd.ScopeBuilderASTScanner;
import com.mxcomplier.FrontEnd.ScopeClassMemberASTScanner;
import com.mxcomplier.FrontEnd.ScopePrepareASTScanner;
import com.mxcomplier.LaxerParser.MxStarLexer;
import com.mxcomplier.LaxerParser.MxStarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;

public class MidTerm {
//    private ProgramNode ast;
    public static void main(String[] args) {
        try{
            InputStream codeInput= System.in;
            CharStream charInput = CharStreams.fromStream(codeInput);
            MxStarLexer lexer = new MxStarLexer(charInput);
            CommonTokenStream token= new CommonTokenStream(lexer);
            MxStarParser parser = new MxStarParser(token);
            ParseTree tree = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ProgramNode ast = (ProgramNode) astBuilder.visit(tree);
            ScopePrepareASTScanner scanner1 = new ScopePrepareASTScanner();
            ScopeClassMemberASTScanner scanner2 = new ScopeClassMemberASTScanner();
            ScopeBuilderASTScanner scanner3 = new ScopeBuilderASTScanner();
            System.out.println("Scanner1");
            scanner1.visit(ast);
            System.out.println("Scanner2");
            scanner2.visit(ast);
            System.out.println("Scanner3");
            scanner3.visit(ast);
        }
        catch (Error e) {
            System.out.println("Complier Failed!");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("input file not exist！");
        }
    }

}
