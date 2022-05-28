package com.vincentcodes.simulator.compiler.parser;

public class LabelOperand implements Operand{
    public String label;
    
    public LabelOperand(String label) {
        this.label = label;
    }

    public String toString(){
        return String.format("{LabelOperand label: %s}", label);
    }
}
