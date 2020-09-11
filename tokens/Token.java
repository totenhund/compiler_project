package com.company.tokens;

public class Token {
    protected String type;
    protected Object value;

    public Token(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {return null;}

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                '}';
    }
}
