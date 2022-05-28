package com.vincentcodes.simulator.impl;

import java.util.Arrays;

import com.vincentcodes.simulator.Memory;

public class MemoryImpl implements Memory{
    private int[] memory; // stores word as an element

    public MemoryImpl(int memSize){
        memory = new int[memSize];
    }

    /**
     * Read value from pos directly.
     * @param pos index of memory
     */
    @Override
    public int read(int pos){
        return memory[pos];
    }

    /**
     * Write value to pos directly.
     */
    @Override
    public void write(int pos, int data){
        memory[pos] = data;
    }

    /**
     * Do not recommend directly accessing memory
     */
    public int[] get(){
        return memory;
    }

    @Override
    public int size(){
        return memory.length;
    }

    @Override
    public void reset(){
        memory = new int[memory.length];
    }

    public String toString(){
        return Arrays.toString(memory);
    }
}
