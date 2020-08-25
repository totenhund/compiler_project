package com.company;


import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
	    LexicalAnalyzer lex = new LexicalAnalyzer("src/input/case3.txt");

        try {
            lex.scanCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
