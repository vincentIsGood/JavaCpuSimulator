package com.vincentcodes.tests.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vincentcodes.simulator.ArithmeticLogicUnit;
import com.vincentcodes.simulator.ArithmeticLogicUnit.Operation;
import com.vincentcodes.simulator.CentralProcessor.ComponentName;
import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.Register;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("ALU Functionality Testing")
public class ALU_Test {
    private CentralProcessor cu;
    private ArithmeticLogicUnit alu;
    private Register register;

    @BeforeAll
    public void setup(){
        cu = new CentralProcessor();
        alu = cu.getComponent(ComponentName.ALU);
        register = cu.getComponent(ComponentName.REGISTER);
    }

    @Test
    public void execution_test(){
        alu.a = 1;
        alu.b = 2;
        alu.execute(Operation.ADD, true);
        assertEquals(3, alu.c);
    }

    @Test
    public void whether_zeroflag_is_set(){
        alu.a = 1;
        alu.b = 1;
        alu.execute(Operation.SUB, true);
        assertEquals(0, alu.c);
        assertEquals(1, register.zeroFlag);
    }
}
