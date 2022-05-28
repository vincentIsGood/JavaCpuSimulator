package com.vincentcodes.simulator;

public interface Memory extends CpuComponent {
    int read(int pos);
    void write(int pos, int data);
    int size();
}
