package com.vincentcodes.simulator.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.vincentcodes.simulator.compiler.lexer.Token;
import com.vincentcodes.simulator.compiler.lexer.TokenTypes;
import com.vincentcodes.simulator.compiler.lexer.Tokenizer;

public class Parser {
    private static String[] SUPPORTED_OPS = new String[]{
        "add", "sub", "not", "and", "or", 
        "mov", "ld", "st", "jmp", "hlt",
        "push", "pop", "call", "ret"
    };
    private static String[] JCC = new String[]{
        "jmp", "jz", "jnz"
    };

    private Tokenizer tokenizer;

    public Parser(String assembly){
        tokenizer = new Tokenizer(assembly);
    }

    public void reset(){
        tokenizer.reset();
    }
    
    /**
     * Dereferencing is not supported yet.
     * [label:]op src1, src2, dst
     * [label:]op src1, dst
     * 
     * label: .dw number
     */
    public List<Instruction> parse(){
        List<Instruction> program = new ArrayList<>();
        Token nextToken = tokenizer.nextToken();
        while(nextToken.type != TokenTypes.EOF){
            if(nextToken.type == TokenTypes.ALPHA_NUMERIC){
                if(tokenizer.peekNextToken().type == TokenTypes.COLON){
                    program.add(new Label(nextToken.value));
                    nextToken = tokenizer.nextToken(); // skip colon
                    nextToken = tokenizer.nextToken();
                    if(nextToken.type == TokenTypes.DOT){
                        nextToken = tokenizer.nextToken();
                        if(nextToken.type != TokenTypes.ALPHA_NUMERIC)
                            throw new RuntimeException("Expecting an identifier at pos: " + tokenizer.getCurrentToken());
                        program.add(new DataDefinition(Integer.parseInt(tokenizer.nextToken().value, 16)));
                    }else program.add(parseInstruction()); // we're at "op"
                }else if(nextToken.type == TokenTypes.ALPHA_NUMERIC){
                    program.add(parseInstruction()); // we're at "op"
                }else
                    throw new RuntimeException("Invalid token: '" + nextToken.value + "' at pos: " + tokenizer.getCurrentToken());
            }else
                throw new RuntimeException("Expecting a label or instruction at pos: " + tokenizer.getCurrentIndex());
            nextToken = tokenizer.nextToken();
        }
        return program;
    }

    private Instruction parseInstruction(){
        Token op = tokenizer.getCurrentToken();
        int opCode = resolveOpCode(op.value);
        switch(opCode){
            // add, sub, and, or
            case 0: case 1: case 3: case 4: return fillInstruction(new Instruction(opCode), 3);
            // not, mov, ld, st
            case 2: case 5: case 6: case 7: return fillInstruction(new Instruction(opCode), 2);
            // jmp
            case 8: {
                JumpInstruction instruction = new JumpInstruction(opCode);
                fillInstruction(instruction, 1);
                instruction.opVariant = resolveJcc(op.value);
                return instruction;
            }
            // push, pop, call
            case 10: case 11: case 12: return fillInstruction(new Instruction(opCode), 1);
            // hlt, ret
            case 9: case 13: return new Instruction(opCode);
            default: throw new RuntimeException("Invalid op code value: '" + op.value + "'");
        }
    }

    /**
     * @param instruction mutates the instruction
     * @return a filled instruction
     */
    private Instruction fillInstruction(Instruction instruction, int operandsRequired){
        Token token = tokenizer.nextToken();
        instruction.operands[0] = resolveOperand(token.value);
        if(operandsRequired <= 1) return instruction;

        token = tokenizer.nextToken();
        if(token.type != TokenTypes.COMMA)
            throw new RuntimeException("Expected ',' at pos: " + tokenizer.getCurrentIndex());
        instruction.operands[1] = resolveOperand(tokenizer.nextToken().value);
        if(operandsRequired <= 2) return instruction;

        token = tokenizer.nextToken();
        if(token.type == TokenTypes.COMMA)
            instruction.operands[2] = resolveOperand(tokenizer.nextToken().value);
        return instruction;
    }

    private static int resolveOpCode(String opStr){
        opStr = opStr.toLowerCase();
        for(int i = 0; i < SUPPORTED_OPS.length; i++){
            if(opStr.equals(SUPPORTED_OPS[i])) 
                return i;
        }
        // Jcc
        for(int i = 0; i < JCC.length; i++){
            if(opStr.equals(JCC[i])) 
                return 8;
        }
        return -1;
    }
    
    private static Operand resolveOperand(String value){
        main:
        if(value.charAt(0) == 'r' || value.charAt(0) == 'R'){
            for(int i = 1; i < value.length(); i++){
                char c = value.charAt(i);
                if(!(c >= '0' && c <= '9')) break main;
            }
            return new RegisterOperand(Integer.parseInt(value.substring(1)));
        }
        return new LabelOperand(value);
    }

    private static int resolveJcc(String cc){
        cc = cc.toLowerCase();
        for(int i = 0; i < JCC.length; i++){
            if(cc.equals(JCC[i])) 
                return i;
        }
        return -1;
    }
}
