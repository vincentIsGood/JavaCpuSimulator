package com.vincentcodes.tests.simulator;

import com.vincentcodes.simulator.compiler.lexer.Token;
import com.vincentcodes.simulator.compiler.lexer.TokenTypes;
import com.vincentcodes.simulator.compiler.lexer.Tokenizer;

import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class Tokenizer_Test {

    @Test
    public void test_tokenizer(){
        var tokenizer = new Tokenizer("""
                send   r1,r2,r3 # asd
                me   r2,r5 ; ghelp
                help r5
                ok   r5, r3    ,r6     , r2  ; nice!
                """);
        Token token;
        while((token = tokenizer.nextToken()).type != TokenTypes.EOF){
            System.out.println(token);
        }
    }
}
