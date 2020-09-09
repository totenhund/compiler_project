package com.company;

import com.company.tokens.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LexicalAnalyzer {

    private final String[] keywords = {"class", "method", "extends", "is", "this",
            "var", "end", "return", "if", "else", "then", "while",
            "loop", "true", "false", "extends"};

    private final String[] separators = {"[", "]", "(", ")", ".", ","};

    private HashMap<String, Boolean> keyHashMap = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> sepHashMap = new HashMap<String, Boolean>();
    private HashMap<String, String> separatorTokenMap = new HashMap<String, String>();

    private final String operators = "[:=]+";
    private final String letters = "[a-zA-Z]";
    private final String digits = "[0-9]";

    private String pathToFile = "";

    public LexicalAnalyzer(String path) {
        initKeywords();
        initSeparators();
        initSeparatorTokenMap();
        pathToFile = path;
    }

    private void initKeywords() {
        for (String keyword : keywords) {
            keyHashMap.put(keyword, true);
        }
    }

    private void initSeparators() {
        for (String separator : separators) {
            sepHashMap.put(separator, true);
        }
    }

    private void initSeparatorTokenMap() {
        separatorTokenMap.put("[", "openSquareBracketSeparator");
        separatorTokenMap.put("]", "closeSquareBracketSeparator");
        separatorTokenMap.put("(", "openBracketSeparator");
        separatorTokenMap.put(")", "closeBracketSeparator");
        separatorTokenMap.put(".", "dotSeparator");
        separatorTokenMap.put(",", "commaSeparator");
    }

    // check if string is keyword
    private Boolean isKeyword(String token) {
        if (keyHashMap.get(token) == null) return false;
        return keyHashMap.get(token);
    }

    // check if char is separator
    private Boolean isSeparator(String s) {
        if (sepHashMap.get(s) == null) return false;
        return sepHashMap.get(s);
    }

    // check if char is letter or not
    public boolean isLetter(String s) {
        return s.matches(letters.concat("+"));
    }

    // check if char is digit or not
    public boolean isDigit(String s) {
        return String.valueOf(s).matches(digits);
    }

    // check if char is operator
    public boolean isOperator(String s) {
        return s.matches(operators);
    }

    // convert file to one big String obj
    public String convertFileToString() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();

        stringBuilder.append(" "); // uberКостыль
        return stringBuilder.toString();
    }

    // Scanner
    public ArrayList<Token> scanCode() throws IOException {
        ArrayList<Token> tokenList = new ArrayList<>();
        String prg = convertFileToString();
        int i = 0;
        while (i < prg.length()) {
            String currChar = String.valueOf(prg.charAt(i));
            switch (currChar) {
                case ":":
                    // Operators
                    if (prg.charAt(i + 1) == '=') {
                        tokenList.add(new Token("varAssignOperator"));
                        System.out.println("varAssignOperator");
                        i++;
                    } else {
                        tokenList.add(new Token("typeAssignOperator"));
                        System.out.println("typeAssignOperator");
                    }
                    i++;
                    break;
                case "/":
                    // Comments

                    if (prg.charAt(i + 1) == '/') {
                        while (prg.charAt(i + 1) != '\n') {
                            i++;
                        }
                    } else if (prg.charAt(i + 1) == '*') {
                        while (true) {
                            if (prg.charAt(i) == '*') {
                                if (prg.charAt(i + 1) == '/') {
                                    i += 2;
                                    break;
                                }
                            }

                            if (i == prg.length() - 1) {
                                System.out.println("UNCLOSED_COMMENTS");
                                break;
                            }

                            i++;
                        }
                    } else {
                        System.out.println("Lexical error (undefined token): " + currChar.charAt(0));
                        i++;
                    }
                    break;
                case " ":
                case "\n":
                case "\t":
                case "\r":
                    // White space
                    i++;
                    break;
                default:
                    if (currChar.matches(letters)) {
                        // Keyword or identifier
                        String token = currChar;
                        while (isLetter(String.valueOf(prg.charAt(i + 1))) && i < prg.length() - 1) {
                            token += String.valueOf(prg.charAt(i + 1));
                            i++;
                        }

                        if (isKeyword(token)) {
                            tokenList.add(new Token(token));
                            System.out.println(token);
                        } else {
                            tokenList.add(new IdentifierToken(token));
                            System.out.println("identifier");
                        }

                    } else if (isSeparator(currChar)) {
                        // Separators
                        tokenList.add(new Token(separatorTokenMap.get(currChar)));
                        System.out.println(separatorTokenMap.get(currChar));
                    } else if (isDigit(currChar)) {
                        // Numbers
                        String token = currChar;
                        while (i < prg.length() - 1 && (isDigit(String.valueOf(prg.charAt(i + 1)))
                                || (prg.charAt(i + 1) == '.' && !token.contains(".")))) {
                            token += String.valueOf(prg.charAt(i + 1));
                            i++;
                        }
                        boolean tokenIsFloat = token.matches("[+]?[0-9]*\\.?[0-9]+");
                        if (tokenIsFloat) {
                            tokenList.add(new RealToken(Double.parseDouble(token)));
                        } else {
                            tokenList.add(new IntToken(Integer.parseInt(token)));
                        }
                        System.out.println("number" + token);
                    } else {
                        // Mistakes
                        if (currChar.charAt(0) != Character.MIN_VALUE) {
                            System.out.println("Lexical error (undefined token): " + currChar.charAt(0));
                        }
                    }
                    i++;
            }
        }
        /* Print as tokens
        for (Token t:tokenList) {
            System.out.println(t);
        }
        */
        return tokenList;
    }

}
