package com.vincentcodes.simulator;

import com.vincentcodes.simulator.util.VoidFunction;

/**
 * IR decoder for Control Unit.
 * 
 * Instruction format looks like this:
 * <pre>
 * A word-sized (4 bytes) instruction.
 * Each component here is 1 byte.
 * +--------+-------+-------+-------+
 * | opcode | src 1 | src 2 | dst 1 |
 * +--------+-------+-------+-------+
 * 
 * Each operand / param is a register 
 * file index (eg. when src1 takes r1, 
 * then src1 is 0x01).
 * </pre>
 */
public class Decoder {
    private final VoidFunction[] OPS;

    public Decoder(CpuOperations ops){
        OPS = ops.getAllOperations();
    }

    /**
     * @param ir raw instruction value (not instruction pointer, or pc)
     * @throws ArrayIndexOutOfBoundsException when opcode is not supported
     */
    public VoidFunction decode(int ir){
        // Takes 0x..000000
        int opcode = (ir >> 24) & 0xff;
        return OPS[opcode];
    }
}