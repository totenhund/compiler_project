package com.company.tokens;

public class IntToken extends Token {
    private int value;

    public IntToken(int value) {
        super("integer");
        this.value = value;
    }

    @Override
    public Object getValue() {return value;}

    @Override
    public String toString() {
        return "IntToken{" +
                "value=" + value +
                ", type='" + type + '\'' +
                '}';
    }
}
