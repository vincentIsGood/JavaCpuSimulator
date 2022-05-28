package com.vincentcodes.simulator.impl;

import java.util.Arrays;

import com.vincentcodes.simulator.RegisterFile;

/**
 * aka registers.
 */
public class RegisterFileImpl extends RegisterFile{
    private int[] generalRegisters; // stores word as an element

    // Bus Endpoints
    public int rfin;
    public int rfout1;
    public int rfout2;

    public RegisterFileImpl(int noOfRegisters){
        generalRegisters = new int[noOfRegisters];
    }

    /**
     * @param pos index of general register (eg. 0 for r0)
     */
    @Override
    public int read(int pos){
        return generalRegisters[pos];
    }

    @Override
    public void write(int pos, int data){
        generalRegisters[pos] = data;
    }

    /**
     * Do not recommend directly accessing registers
     */
    public int[] get(){
        return generalRegisters;
    }

    @Override
    public int size(){
        return generalRegisters.length;
    }

    @Override
    public void reset() {
        generalRegisters = new int[generalRegisters.length];
        rfin = 0;
        rfout1 = 0;
        rfout2 = 0;
    }

    public String toString(){
        return Arrays.toString(generalRegisters);
    }
}
