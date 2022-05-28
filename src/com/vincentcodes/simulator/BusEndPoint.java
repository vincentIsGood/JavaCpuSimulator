package com.vincentcodes.simulator;

import com.vincentcodes.simulator.impl.BusImpl;

/**
 * Remember to add functionality for new endpoints
 * in {@link BusImpl}
 */
public enum BusEndPoint {
    A, B, C,              // for alu
    RFIN, RFOUT1, RFOUT2, // for register file
    PC, // program counter
    SP, // stack pointer
    MAR,
    MBR
}
