package com.vincentcodes.simulator.impl;

import com.vincentcodes.simulator.ArithmeticLogicUnit;
import com.vincentcodes.simulator.ArithmeticLogicUnit.Operation;
import com.vincentcodes.simulator.Bus;
import com.vincentcodes.simulator.BusEndPoint;
import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.CentralProcessor.ComponentName;
import com.vincentcodes.simulator.CpuOperations;
import com.vincentcodes.simulator.Register;

public class CpuOperationsImpl implements CpuOperations{
    private final CentralProcessor cu;

    private Bus s1bus;
    private Bus s2bus;
    private Bus dbus;

    private ArithmeticLogicUnit alu;
    private Register register;

    public CpuOperationsImpl(CentralProcessor controlUnit){
        this.cu = controlUnit;
        this.s1bus = this.cu.getComponent(ComponentName.S1BUS);
        this.s2bus = this.cu.getComponent(ComponentName.S2BUS);
        this.dbus = this.cu.getComponent(ComponentName.DBUS);
        this.alu = this.cu.getComponent(ComponentName.ALU);
        this.register = this.cu.getComponent(ComponentName.REGISTER);
    }

    // Implementations for the supported operations
    @Override
    public void add(){
        // ADD r1,r2,r3 ; r1 + r2 -> r3
        executeAluInstruction(Operation.ADD);
    }

    @Override
    public void sub(){
        // ADD r1,r2,r3 ; r1 - r2 -> r3
        executeAluInstruction(Operation.SUB);
    }

    @Override
    public void not(){
        // NOT r1  ,r3 ; ~r1 -> r3
        executeAluInstruction(Operation.NOT);
    }

    @Override
    public void and(){
        // AND r1,r2,r3 ; r1 & r2 -> r3
        executeAluInstruction(Operation.AND);
    }

    @Override
    public void or(){
        // OR r1,r2,r3 ; r1 | r2 -> r3
        executeAluInstruction(Operation.OR);
    }

    @Override
    public void mov(){
        // MOV r1  ,r3 ; r1 -> r3
        executeAluInstruction(Operation.COPY);
    }

    /**
     * 2-word load instruction
     * <pre>
     * Word 1: 06,00,ff,xx
     * Word 2:  <address>
     * ff - addressing mode (absolute)
     * xx - load to xx-th register.
     * </pre>
     */
    @Override
    public void ld(){
        // Fetch next word as Address
        fetchNextWordByPC();
        // Send MBR to MAR (because PC points to an address, now we do [PC])
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        // Dereference PC and read it should give us the real value (eg. constant 1)
        cu.readMemoryUsingMar();
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.RFIN);
        // Write it to register
        cu.writeToDstOperand();
    }

    /**
     * 2-word store instruction
     * <pre>
     * Word 1: 07,xx,ff,00
     * Word 2:  <address>
     * xx - read from xx-th register.
     * ff - addressing mode (absolute)
     * </pre>
     */
    @Override
    public void st(){
        // Fetch next word as Address
        fetchNextWordByPC();
        // Put next address to MAR, waiting for dereference
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        // Read value from register
        cu.decodeSrc1AndDeref();
        s1bus.transfer(BusEndPoint.RFOUT1, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MBR);
        // Dereference and write value to memory
        cu.writeToMemoryUsingMar();
    }

    /**
     * 2-word jump instruction (Jcc); cc = condition code
     * <pre>
     * Word 1: 08,xx,ff,00
     * Word 2:  <address>
     * xx - condition code (0 - JMP, 1 - JZ, 2 - JNZ)
     * ff - addressing mode (absolute)
     * </pre>
     */
    @Override
    public void jmp(){
        fetchNextWordByPC();
        // Decode the condition
        boolean doJump = false;
        switch(cu.decodeOperandRaw(1)){
            case 0: doJump = true; break;
            case 1: doJump = register.zeroFlag == 1; break;
            case 2: doJump = register.zeroFlag == 0; break;
        }
        if(doJump){
            s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
            alu.execute(Operation.COPY, false);
            dbus.transfer(BusEndPoint.C, BusEndPoint.PC);
        }
    }

    /**
     * Stops program
     */
    @Override
    public void hlt(){
        cu.stop();
    }

    /**
     * push value from register to stack
     * <pre>
     * Word 1: 0a,xx,00,00
     * xx - register number (eg. 0 for r0)
     * </pre>
     */
    @Override
    public void push() {
        // retrieve register value
        cu.decodeSrc1AndDeref();
        s1bus.transfer(BusEndPoint.RFOUT1, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MBR);
        // send SP to MAR
        cu.decSP();
        s1bus.transfer(BusEndPoint.SP, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        // Done
        cu.writeToMemoryUsingMar();
    }

    /**
     * pop value from stack and store it to register
     * <pre>
     * Word 1: 0b,00,00,xx
     * xx - register number (eg. 0 for r0)
     * </pre>
     */
    @Override
    public void pop() {
        // retrieve value from mem
        s1bus.transfer(BusEndPoint.SP, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        cu.readMemoryUsingMar();
        // put value to specified register
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.RFIN);
        // Done
        cu.writeToDstOperand();
    }

    /**
     * 2-word call instruction
     * <p>
     * This CPU will not save register file automatically,
     * it is the compiler's job to save and restore main
     * routine's registers.
     * <p>
     * Example:
     * <pre>
     * // sub-routine:
     * push r1
     * push r2
     * // code for sub-routine
     * // use r1, r2
     * // pop (restore) values back to register r2, r1
     * pop r2
     * pop r1
     * </pre>
     * 
     * Instruction info:
     * <pre>
     * Word 1: 0c,00,ff,00
     * Word 2:  <address>
     * ff - absolute addressing mode
     * address - subroutine's absolute address
     * </pre>
     */
    @Override
    public void call() {
        fetchNextWordByPC();
        // retrieve sub-routine's address and save it
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        cu.saveMarToTemp();
        // push pc to stack (return address)
        cu.decSP();
        s1bus.transfer(BusEndPoint.SP, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        s1bus.transfer(BusEndPoint.PC, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MBR);
        cu.writeToMemoryUsingMar();
        // next addr = sub-routine's addr
        cu.moveTempToMar();
        s1bus.transfer(BusEndPoint.MAR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.PC);
    }

    /**
     * return instruction
     * <p>
     * This CPU have no base pointer, it is crucial for compilers to do 
     * proper callstack cleanup
     * <pre>
     * Word 1: 0d,00,00,00
     * </pre>
     */
    @Override
    public void ret() {
        s1bus.transfer(BusEndPoint.SP, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        cu.readMemoryUsingMar();
        cu.incSP(); // incSP ~= pop
        s1bus.transfer(BusEndPoint.MBR, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.PC);
    }

    /**
     * Performs data transferring from RF to ALU before 
     * operation execution.
     */
    public void executeAluInstruction(ArithmeticLogicUnit.Operation op){
        // transfer data from RegisterFile to ALU first
        boolean useOneOperand = op == Operation.NOT || op == Operation.COPY;
        cu.decodeSrc1AndDeref();
        s1bus.transfer(BusEndPoint.RFOUT1, BusEndPoint.A);
        if(!useOneOperand){
            cu.decodeSrc2AndDeref();
            s2bus.transfer(BusEndPoint.RFOUT2, BusEndPoint.B);
        }
        // execute here
        alu.execute(op, true);
        dbus.transfer(BusEndPoint.C, BusEndPoint.RFIN);
        cu.writeToDstOperand();
    }

    /**
     * After operation, MBR should now contain the next 
     * word which PC points to in the .text (code) part 
     * of memory. 
     */
    public void fetchNextWordByPC(){
        // Send PC to MAR and fetch another word.
        s1bus.transfer(BusEndPoint.PC, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        cu.fetchNextWordUsingMar();
    }
}
