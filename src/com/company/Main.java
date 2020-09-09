package com.company;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	    LexicalAnalyzer lex = new LexicalAnalyzer("src/cases/case11.txt");
        try {
            lex.scanCode();
            lex.Print_Tokens();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
