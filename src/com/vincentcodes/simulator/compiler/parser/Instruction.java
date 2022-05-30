package com.vincentcodes.simulator.compiler.parser;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;

import com.vincentcodes.simulator.impl.CpuOperationsImpl;

/**
 * Convention:
 * 3 ops: add r1,r2,r3 ; r1 + r2 -> r3
 * 2 ops: add r1,r3    ; r1 + r3 -> r3
 * 
 * label is allowed but {@code TwoWordInstruction} is used
 */
public class Instruction {
    public int opCode;
    public Operand[] operands = new Operand[3];

    public Instruction(int opCode){
        this.opCode = opCode;
    }

    /**
     * In our CPU,
     * Label operands uses absolute address, hence an maximum 4-word 
     * instruction can be created (1 word for instruction, 3 words 
     * for the 3 labels)
     */
    public int bytesUsed(){
        int numOfLabelOperands = 0;
        for(Operand operand : operands)
            if(operand instanceof LabelOperand) numOfLabelOperands++;
        return (1+numOfLabelOperands)*4;
    }

    public String toString(){
        return String.format("{Instruction opCode: %d, operands: %s}", opCode, Arrays.toString(operands));
    }

    /**
     * @see CpuOperationsImpl
     */
    public static byte[] toByteCode(Instruction instruction, Map<String, Integer> labelAddress){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(instruction instanceof DataDefinition){
            baos.writeBytes(intToBytes(((DataDefinition)instruction).data));
            return baos.toByteArray();
        }
        try{
            byte[] instructionBytes = new byte[4];
            instructionBytes[0] = (byte)instruction.opCode;
            switch(instruction.opCode){
                // add, sub, and, or
                case 0: case 1: case 3: case 4: {
                    instructionBytes[1] = (byte)((RegisterOperand)instruction.operands[0]).registerNum;
                    instructionBytes[2] = (byte)((RegisterOperand)instruction.operands[1]).registerNum;
                    instructionBytes[3] = (byte)((RegisterOperand)instruction.operands[2]).registerNum;
                    baos.writeBytes(instructionBytes);
                    return baos.toByteArray();
                }
                // not, mov
                case 2: case 5: {
                    instructionBytes[1] = (byte)((RegisterOperand)instruction.operands[0]).registerNum;
                    instructionBytes[3] = (byte)((RegisterOperand)instruction.operands[1]).registerNum;
                    baos.writeBytes(instructionBytes);
                    return baos.toByteArray();
                }
                // ld
                case 6: {
                    instructionBytes[2] = (byte)0xff; // addressing mode
                    instructionBytes[3] = (byte)((RegisterOperand)instruction.operands[1]).registerNum;
                    baos.writeBytes(instructionBytes);
                    baos.writeBytes(intToBytes(labelAddress.get(((LabelOperand)instruction.operands[0]).label)));
                    return baos.toByteArray();
                }
                // st
                case 7: {
                    instructionBytes[1] = (byte)((RegisterOperand)instruction.operands[0]).registerNum;
                    instructionBytes[2] = (byte)0xff; // addressing mode
                    baos.writeBytes(instructionBytes);
                    baos.writeBytes(intToBytes(labelAddress.get(((LabelOperand)instruction.operands[1]).label)));
                    return baos.toByteArray();
                }
                // jmp
                case 8: {
                    JumpInstruction jmpInst = (JumpInstruction)instruction;
                    instructionBytes[1] = (byte)jmpInst.opVariant;
                    instructionBytes[2] = (byte)0xff; // addressing mode
                    baos.writeBytes(instructionBytes);
                    baos.writeBytes(intToBytes(labelAddress.get(((LabelOperand)instruction.operands[0]).label)));
                    return baos.toByteArray();
                }
                // push
                case 10: {
                    instructionBytes[1] = (byte)((RegisterOperand)instruction.operands[0]).registerNum;
                    baos.writeBytes(instructionBytes);
                    return baos.toByteArray();
                }
                // pop
                case 11: {
                    instructionBytes[3] = (byte)((RegisterOperand)instruction.operands[0]).registerNum;
                    baos.writeBytes(instructionBytes);
                    return baos.toByteArray();
                }
                // call
                case 12: {
                    instructionBytes[2] = (byte)0xff; // addressing mode
                    baos.writeBytes(instructionBytes);
                    baos.writeBytes(intToBytes(labelAddress.get(((LabelOperand)instruction.operands[0]).label)));
                    return baos.toByteArray();
                }
                // hlt, ret
                case 9: case 13: {
                    baos.writeBytes(instructionBytes);
                    return baos.toByteArray();
                }
                default: throw new RuntimeException("Invalid op code value: '" + instruction.opCode + "' from instruction: " + instruction);
            }
        }catch(ClassCastException e){
            throw new RuntimeException("Invalid format found when converting " + instruction + " to machine code", e);
        }
    }

    private static byte[] intToBytes(int num){
        return new byte[]{(byte)(num >> 24), (byte)(num >> 16), (byte)(num >> 8), (byte)(num)};
    }
}
