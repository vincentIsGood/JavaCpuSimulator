package com.vincentcodes.simulator.impl;

import java.util.HashSet;
import java.util.Set;

import com.vincentcodes.simulator.ArithmeticLogicUnit;
import com.vincentcodes.simulator.Bus;
import com.vincentcodes.simulator.BusEndPoint;
import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.CentralProcessor.ComponentName;
import com.vincentcodes.simulator.Register;
import com.vincentcodes.simulator.RegisterFile;

public class BusImpl implements Bus{
    private Set<BusEndPoint> fromEndpoints;
    private Set<BusEndPoint> toEndpoints;

    private final Register register;
    private final RegisterFile generalRegisters;
    private final ArithmeticLogicUnit alu;
    
    public BusImpl(CentralProcessor controlUnit){
        fromEndpoints = new HashSet<>();
        toEndpoints = new HashSet<>();

        register = controlUnit.getComponent(ComponentName.REGISTER);
        generalRegisters = controlUnit.getComponent(ComponentName.GENERAL_REGISTERS);
        alu = controlUnit.getComponent(ComponentName.ALU);
    }

    @Override
    public void connectFrom(BusEndPoint... from) {
        for(BusEndPoint pt : from)
            fromEndpoints.add(pt);
    }

    @Override
    public void connectTo(BusEndPoint... to) {
        for(BusEndPoint pt : to)
            toEndpoints.add(pt);
    }

    /**
     * Transfer from one endpoint to another. Just like moving
     * data using the bus to another place in CPU.
     */
    @Override
    public void transfer(BusEndPoint from, BusEndPoint to) {
        if(!fromEndpoints.contains(from) || !toEndpoints.contains(to))
            throw new IllegalArgumentException("Connection from " + from.name() + " to " + to.name() + " does not exist");
        
        int fromValue = 0;
        switch(from){
            case A:      fromValue = alu.a; break;
            case B:      fromValue = alu.b; break;
            case C:      fromValue = alu.c; break;
            case RFIN:   fromValue = generalRegisters.rfin; break;
            case RFOUT1: fromValue = generalRegisters.rfout1; break;
            case RFOUT2: fromValue = generalRegisters.rfout2; break;
            case PC:     fromValue = register.pc; break;
            case MAR:    fromValue = register.mar; break;
            case MBR:    fromValue = register.mbr; break;
            case SP:     fromValue = register.sp; break;
        }
        switch(to){
            case A:      alu.a = fromValue; break;
            case B:      alu.b = fromValue; break;
            case C:      alu.c = fromValue; break;
            case RFIN:   generalRegisters.rfin   = fromValue; break;
            case RFOUT1: generalRegisters.rfout1 = fromValue; break;
            case RFOUT2: generalRegisters.rfout2 = fromValue; break;
            case PC:     register.pc  = fromValue; break;
            case MAR:    register.mar = fromValue; break;
            case MBR:    register.mbr = fromValue; break;
            case SP:     register.sp  = fromValue; break;
        }
    }

    @Override
    public void reset() {
        fromEndpoints = new HashSet<>();
        toEndpoints = new HashSet<>();
    }
    
}
