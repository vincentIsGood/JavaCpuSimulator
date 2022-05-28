package com.vincentcodes.simulator.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vincentcodes.simulator.compiler.parser.Instruction;
import com.vincentcodes.simulator.compiler.parser.Label;
import com.vincentcodes.simulator.compiler.parser.Parser;

public class Compiler {
    public Compiler(){}

    public void compile(File file) throws IOException{
        try(FileInputStream fis = new FileInputStream(file)){
            compile(new String(fis.readAllBytes()));
        }
    }

    public byte[] compile(String assembly){
        List<Instruction> program = parse(assembly);

        // Build label addresses
        int bytesPassed = 0;
        Map<String, Integer> labelAddress = new HashMap<>();
        for(Instruction instruction : program){
            if(instruction instanceof Label)
                labelAddress.put(((Label)instruction).label, bytesPassed);
            else bytesPassed += instruction.bytesUsed();
        }

        // Generate machine code
        ByteArrayOutputStream machineCode = new ByteArrayOutputStream();
        for(Instruction instruction : program){
            if(!(instruction instanceof Label))
                machineCode.writeBytes(Instruction.toByteCode(instruction, labelAddress));
        }
        return machineCode.toByteArray();
    }

    public String compileToStringMachineCode(String assembly){
        byte[] machineCode = compile(assembly);
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < machineCode.length; i++){
            result.append(String.format("%02x" + ((i+1)%4==0? "\n" : ""), Byte.toUnsignedInt(machineCode[i])));
        }
        return result.toString();
    }

    /* Sample output
        [
            {Instruction opCode: 6, operands: [{LabelOperand label: p0}, {RegisterOperand registerNum: 4}, null]},
            {Instruction opCode: 6, operands: [{LabelOperand label: p1}, {RegisterOperand registerNum: 1}, null]},
            {Instruction opCode: 5, operands: [{RegisterOperand registerNum: 1}, {RegisterOperand registerNum: 2}, null]},
            {Instruction opCode: 6, operands: [{LabelOperand label: p2}, {RegisterOperand registerNum: 3}, null]},
            {Label label: L},
            {Instruction opCode: 0, operands: [{RegisterOperand registerNum: 4}, {RegisterOperand registerNum: 1}, {RegisterOperand registerNum: 4}]},
            {Instruction opCode: 0, operands: [{RegisterOperand registerNum: 1}, {RegisterOperand registerNum: 2}, {RegisterOperand registerNum: 1}]},
            {Instruction opCode: 1, operands: [{RegisterOperand registerNum: 3}, {RegisterOperand registerNum: 1}, {RegisterOperand registerNum: 5}]},
            {JumpInstruction opCode: 8, variant: 2, operands: [{LabelOperand label: L}, null, null]},
            {Instruction opCode: 7, operands: [{RegisterOperand registerNum: 4}, {LabelOperand label: p}, null]},
            {Instruction opCode: 9, operands: [null, null, null]},
            {Label label: p0}, {Data data: 0},
            {Label label: p1}, {Data data: 1},
            {Label label: p2}, {Data data: 10},
            {Label label: p}, {Data data: 0}
        ]
    */
    private List<Instruction> parse(String assembly){
        return new Parser(assembly).parse();
    }

}
