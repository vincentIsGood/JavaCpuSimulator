package com.vincentcodes.simulator.compiler.lexer;

public class Token {
    public TokenTypes type;
    public String value;

    public Token(TokenTypes type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString(){
        return String.format("{type: %s, value: '%s'}", type, value);
    }
}
