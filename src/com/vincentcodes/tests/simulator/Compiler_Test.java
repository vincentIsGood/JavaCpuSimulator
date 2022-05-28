package com.vincentcodes.tests.simulator;

import com.vincentcodes.simulator.compiler.Compiler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class Compiler_Test {
    private Compiler compiler;

    @BeforeAll
    public void setup(){
        this.compiler = new Compiler();
    }

    @Test
    public void parse(){
        System.out.println(compiler.compileToStringMachineCode("""
                ld p0,r4
                ld p1,r1
                mov r1,r2
                ld p2,r3
            L:  add r4,r1,r4
                add r1,r2,r1
                sub r3,r1,r5
                jnz L
                st r4,p
                hlt
            p0: .dw 0
            p1: .dw 1
            p2: .dw a
            p:  .dw 0
        """));
    }
}
