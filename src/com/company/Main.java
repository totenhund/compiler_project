package com.company;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	    LexicalAnalyzer lex = new LexicalAnalyzer("src/input/case1.txt");
        try {
            lex.scanCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
