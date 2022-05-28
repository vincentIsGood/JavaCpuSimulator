package com.vincentcodes.simulator.compiler.lexer;

import java.util.ArrayDeque;
import java.util.Deque;

public class Tokenizer {
    private String content;
    private int currentIndex;
    private Token currentToken;
    private Deque<Token> peekBuffer;

    public Tokenizer(String content){
        currentIndex = 0;
        this.content = content;
        peekBuffer = new ArrayDeque<>();
    }

    public Token nextToken(){
        if(peekBuffer.size() > 0)
            return currentToken = peekBuffer.pop();
        char c = getCurrentChar();
        if(c == '\0') {
            return currentToken = new Token(TokenTypes.EOF, null);
        }
        StringBuilder value = new StringBuilder();
        if(isAlphaNumeric(c)){
            while(isAlphaNumeric(c = getCurrentChar())){
                value.append(c);
                nextIndex();
            }
            return currentToken = new Token(TokenTypes.ALPHA_NUMERIC, value.toString());
        }else if(c == ':'){
            nextIndex();
            return currentToken = new Token(TokenTypes.COLON, null);
        }else if(c == ','){
            nextIndex();
            return currentToken = new Token(TokenTypes.COMMA, null);
        }else if(c == '.'){
            nextIndex();
            return currentToken = new Token(TokenTypes.DOT, null);
        }else if(c == ';'){
            while(getCurrentChar() != '\n')
                nextIndex(); // skip all comments until the end of line
            return nextToken(); // get next token which is not ";"
        }else if(c == '#'){
            while(getCurrentChar() != '\n')
                nextIndex();
            return nextToken();
        }else if(c == '\n'){
            nextIndex();
            return nextToken();
        }else if(c == ' ' || c == '\t'){
            while(getCurrentChar() == ' ' || getCurrentChar() == '\t')
                nextIndex();
            return nextToken(); // get next token which is not space
        }
        throw new RuntimeException("Invalid character: '"+ c +"' at position " + currentIndex);
    }

    public Token peekNextToken(){
        Token currentTokenTmp = currentToken;
        if(peekBuffer.size() > 0)
            return peekBuffer.peekFirst();
        Token nextToken = nextToken();
        peekBuffer.push(nextToken);
        currentToken = currentTokenTmp; // restore it
        return nextToken;
    }

    public Token getCurrentToken(){
        return currentToken;
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    private void nextIndex(){
        currentIndex++;
    }

    private char getCurrentChar(){
        if(currentIndex >= content.length())
            return '\0';
        return content.charAt(currentIndex);
    }

    private boolean isAlphaNumeric(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    public void reset(){
        currentIndex = 0;
        currentToken = null;
    }

}
