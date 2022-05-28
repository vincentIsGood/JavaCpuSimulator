package com.vincentcodes.simulator.compiler.parser;

public class RegisterOperand implements Operand {
    public int registerNum;
    
    public RegisterOperand(int registerNum) {
        this.registerNum = registerNum;
    }

    public String toString(){
        return String.format("{RegisterOperand registerNum: %d}", registerNum);
    }
}
