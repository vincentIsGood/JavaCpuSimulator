package com.vincentcodes.simulator;

/**
 * contains registers for pc, ir, mar, mbr
 * 
 * ir holds the whole integer value of a word instruction.
 * 
 * For this cpu, mar and mbr holds a word value only. So I
 * used Register to hold their values.
 */
public class Register implements CpuComponent{
    public int pc; // instruction pointer (points to a byte, not index)
    public int ir;
    public int mar; // Memory Address Register, 4 bytes
    public int mbr; // Memory Buffer Register, 4 bytes
    public int zeroFlag; // 1 or 0. Zero flag only, for now
    public int tmp; // temp for storing sub-routine's address (intermediate var)
    public int sp; // stack pointer (4 bytes)

    private final Memory mem;

    public Register(Memory mem){
        this.mem = mem;
        sp = mem.size() * 4;
    }

    @Override
    public void reset(){
        pc = 0;
        ir = 0;
        mar = 0;
        mbr = 0;
        zeroFlag = 0;
        tmp = 0;
        sp = mem.size() * 4;
    }
}
