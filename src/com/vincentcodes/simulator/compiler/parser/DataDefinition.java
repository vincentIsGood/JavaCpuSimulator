package com.vincentcodes.simulator.compiler.parser;

public class DataDefinition extends Instruction {
    public int data; // word

    public DataDefinition(int data){
        super(-1);
        this.data = data;
    }
    
    public String toString(){
        return String.format("{Data data: %d}", data);
    }
}
