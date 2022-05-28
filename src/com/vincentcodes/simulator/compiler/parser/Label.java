package com.vincentcodes.simulator.compiler.parser;

public class Label extends Instruction {
    public String label;

    public Label(String label){
        super(-1);
        this.label = label;
    }

    public String toString(){
        return String.format("{Label label: %s}", label);
    }
}
