package com.company.tokens;

public class IdentifierToken extends Token {
    private String value; //Value === name

    public IdentifierToken(String name) {
        super("identifier");
        this.value = name;
    }

    public String getName() {
        return value;
    }

    @Override
    public String toString() {
        return "IdentifierToken{" +
                "name='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
