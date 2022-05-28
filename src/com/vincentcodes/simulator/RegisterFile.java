package com.vincentcodes.simulator;

public abstract class RegisterFile implements CpuComponent {
    
    public int rfin;
    public int rfout1;
    public int rfout2;

    public abstract int read(int pos);
    public abstract void write(int pos, int data);
    public abstract int size();
    public abstract void reset();

}
