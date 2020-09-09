package com.company;

import com.company.AST.Node;
import com.company.tokens.Token;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        LexicalAnalyzer lex = new LexicalAnalyzer("src/cases/case1.txt");
        ArrayList<Token> tokens;
        try {
             tokens = lex.scanCode();
        } catch (IOException e) {
            e.printStackTrace();
            tokens = null;
        }
        SyntaxAnalyzer syn = new SyntaxAnalyzer(tokens);
        Node result = syn.makeAST();
    }
}
