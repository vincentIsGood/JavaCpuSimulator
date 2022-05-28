package com.vincentcodes.simulator;

import java.util.HashMap;
import java.util.Map;

import com.vincentcodes.simulator.ArithmeticLogicUnit.Operation;
import com.vincentcodes.simulator.impl.BusImpl;
import com.vincentcodes.simulator.impl.CpuOperationsImpl;
import com.vincentcodes.simulator.impl.MemoryImpl;
import com.vincentcodes.simulator.impl.RegisterFileImpl;
import com.vincentcodes.simulator.util.VoidFunction;

/**
 * Controls buses and all kinds of operations.
 * This is a simulator thing. Executing object
 * methods simulate as a control signals. Hence,
 * there is no Signal class or whatever to add
 * complexity.
 * <p>
 * This CPU reads word by word. Instructions are
 * typically a one-word instruction. Some 
 * instructions can be a two-word instruction.
 */
public class CentralProcessor implements CpuComponent{
    public enum ComponentName{
        S1BUS,
        S2BUS,
        DBUS,
        ALU,
        GENERAL_REGISTERS,
        REGISTER,
        MEMORY;
    }

    public final CpuOperations OPERATIONS;

    private final Map<ComponentName, CpuComponent> COMPONENTS;

    private final Bus s1bus;
    private final Bus s2bus;
    private final Bus dbus;
    private final Register register;
    private final RegisterFile generalRegisters;
    private final Memory memory;
    private final ArithmeticLogicUnit alu;

    private final Decoder decoder;

    // modern computers won't use halt
    private boolean halt = false;

    public CentralProcessor(){
        COMPONENTS = new HashMap<>();

        COMPONENTS.put(ComponentName.GENERAL_REGISTERS, generalRegisters = new RegisterFileImpl(16));
        COMPONENTS.put(ComponentName.MEMORY, memory = new MemoryImpl(1024));
        COMPONENTS.put(ComponentName.REGISTER, register = new Register(memory));
        COMPONENTS.put(ComponentName.ALU, alu = new ArithmeticLogicUnit(register));

        // Buses need previous components
        COMPONENTS.put(ComponentName.S1BUS, s1bus = new BusImpl(this));
        COMPONENTS.put(ComponentName.S2BUS, s2bus = new BusImpl(this));
        COMPONENTS.put(ComponentName.DBUS, dbus = new BusImpl(this));
        connectBuses();

        OPERATIONS = new CpuOperationsImpl(this);
        decoder = new Decoder(OPERATIONS);
    }

    public void connectBuses(){
        s1bus.connectFrom(BusEndPoint.RFOUT1, BusEndPoint.PC, BusEndPoint.SP, BusEndPoint.MAR, BusEndPoint.MBR);
        s1bus.connectTo(BusEndPoint.A);
        s2bus.connectFrom(BusEndPoint.RFOUT2);
        s2bus.connectTo(BusEndPoint.B);
        dbus.connectFrom(BusEndPoint.C);
        dbus.connectTo(BusEndPoint.RFIN, BusEndPoint.PC, BusEndPoint.SP, BusEndPoint.MAR, BusEndPoint.MBR);
    }

    /**
     * Read instruction from memory and store it to IR
     */
    public void fetch(){
        // Bring PC to MAR
        s1bus.transfer(BusEndPoint.PC, BusEndPoint.A);
        alu.execute(Operation.COPY, false);
        dbus.transfer(BusEndPoint.C, BusEndPoint.MAR);
        // Fetch instruction from mem
        readMemoryUsingMar();
        // Put instruction to ir
        register.ir = register.mbr;
        // Done
        incrementPC();
    }

    /**
     * Reads instruction register (ie. dereference instruction pointer)
     */
    public VoidFunction decode(){
        return decoder.decode(register.ir);
    }

    public void execute(VoidFunction operation){
        operation.execute();
    }

    /**
     * Entry point
     */
    public void start(){
        while(!halt && (register.pc / 4 < memory.size())){
            fetch();
            execute(decode());
        }
    }

    public void stop(){
        halt = true;
    }

    // Low-level operation abstraction
    /**
     * Standard read from memory. 
     * MAR contains the address to read from and store
     * the result to MBR.
     */
    public void readMemoryUsingMar(){
        // a word is 4-bytes
        register.mbr = memory.read(register.mar / 4);
    }

    /**
     * Write MBR value to mem address which MAR contains.
     */
    public void writeToMemoryUsingMar(){
        memory.write(register.mar / 4, register.mbr);
    }

    /**
     * Result goes to MBR while PC is incremented
     */
    public void fetchNextWordUsingMar(){
        readMemoryUsingMar();
        incrementPC();
    }

    public void saveMarToTemp(){
        register.tmp = register.mar;
    }

    public void moveTempToMar(){
        register.mar = register.tmp;
    }

    /**
     * Decode src operand 1, get what the register contains
     * then put them into RFOUT1.
     */
    public void decodeSrc1AndDeref(){
        generalRegisters.rfout1 = generalRegisters.read(decodeOperandRaw(1));
    }
    /**
     * Decode src operand 2, get what the register contains
     * then put them into RFOUT2.
     */
    public void decodeSrc2AndDeref(){
        generalRegisters.rfout2 = generalRegisters.read(decodeOperandRaw(2));
    }

    /**
     * Take data in RFIN and write it to register specified 
     * by dst operand.
     */
    public void writeToDstOperand(){
        // 0x000000..
        generalRegisters.write(register.ir & 0xff, generalRegisters.rfin);
    }

    /**
     * Read nth byte starting from 1st operand counting from
     * the left.
     * @param nthByte cannot be 0
     */
    public int decodeOperandRaw(int nthByte){
        if(nthByte <= 0 || nthByte >= 4)
            throw new IllegalArgumentException("Cannot read "+ nthByte +"th (byte) operand from an instruction");
        // Returns 0x00..0000, 0x0000..00, 0x000000..
        return (register.ir >> 8*(3-nthByte)) & 0xff;
    }

    // Variable state change
    public void incrementPC(){
        register.pc += 4;
    }
    public void incSP(){
        register.sp += 4;
    }
    public void decSP(){
        register.sp -= 4;
    }

    // ----------- Getters ----------- //
    // public CpuComponent getComponent(ComponentName name){
    //     return COMPONENTS.get(name);
    // }
    public <T extends CpuComponent> T getComponent(ComponentName name){
        @SuppressWarnings("unchecked")
        T result = (T)COMPONENTS.get(name);
        return result;
    }

    public boolean hasHalted(){
        return halt;
    }

    @Override
    public void reset() {
        s1bus.reset();
        s2bus.reset();
        dbus.reset();

        alu.reset();
        generalRegisters.reset();
        register.reset();
        memory.reset();

        halt = false;
        connectBuses();
    }
}
