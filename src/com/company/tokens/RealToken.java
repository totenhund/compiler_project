package com.company.tokens;

public class RealToken extends Token {
    private double value;

    public RealToken(double value) {
        super("real");
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "realToken{" +
                "value=" + value +
                ", type='" + type + '\'' +
                '}';
    }
}
