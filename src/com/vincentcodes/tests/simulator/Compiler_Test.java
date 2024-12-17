package com.vincentcodes.tests.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void compile_main_routine(){
        assertEquals("""
0600ff04
0000003c
0600ff01
00000040
05010002
0600ff03
00000044
00040104
00010201
01030105
0802ff00
0000001c
0704ff00
00000048
09000000
00000000
00000001
0000000a
00000000
        """, compiler.compileToStringMachineCode("""
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

    @Test
    public void compile_main_and_subroutine(){
        // technique: Use push and pop together to restore old values
        assertEquals("""
01040404
0600ff01
00000078
05010002
0600ff03
0000007c
0501000a
0c00ff00
00000044
00040b04
00010201
01030105
0802ff00
00000018
0704ff00
00000080
09000000
0a0c0000
0a0d0000
0600ff0d
00000078
010b0b0b
050a000c
000b0a0b
010c0d0c
0802ff00
0000005c
0b00000d
0b00000c
0d000000
00000001
0000000a
00000000
        """, compiler.compileToStringMachineCode("""
                SUB r4, r4, r4
                LD p1, r1
                MOV r1, r2
                LD p2, r3
            L:  MOV r1, r10
                CALL SQ
                ADD r4, r11, r4
                ADD r1, r2, r1
                SUB r3, r1, r5
                JNZ L
                ST r4, p
                HLT

                ; SQ(x) = x^2 = x*x = x+...+x (x plus x times)
            SQ: PUSH r12
                PUSH r13
                LD p1, r13
                SUB r11, r11, r11
                MOV r10, r12
            L2: ADD r11, r10, r11
                SUB r12, r13, r12
                JNZ L2
                POP r13
                POP r12
                RET
            p1: .dw 1
            p2: .dw a
            p:  .dw 0
        """));
    }
}
