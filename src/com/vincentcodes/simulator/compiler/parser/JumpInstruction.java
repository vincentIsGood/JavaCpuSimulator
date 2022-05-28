package com.vincentcodes.simulator.compiler.parser;

import java.util.Arrays;

public class JumpInstruction extends Instruction{
    public int opVariant; // used for "jcc" condition codes

    public JumpInstruction(int opCode) {
        super(opCode);
    }

    public String toString(){
        return String.format("{JumpInstruction opCode: %d, variant: %d, operands: %s}", opCode, opVariant, Arrays.toString(operands));
    }
}
