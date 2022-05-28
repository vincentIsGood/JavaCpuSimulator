package com.vincentcodes.tests.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.CentralProcessor.ComponentName;
import com.vincentcodes.simulator.Memory;
import com.vincentcodes.simulator.Register;
import com.vincentcodes.simulator.RegisterFile;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Control Unit Tests")
public class ControlUnit_Test {
    private CentralProcessor cu;
    private int memoryIndex = 0;

    @BeforeAll
    public void setup(){
        cu = new CentralProcessor();
    }

    @BeforeEach
    public void reset(){
        cu.reset();
        memoryIndex = 0;
    }

    @Test
    public void given_empty_memory_then_can_run_without_halt(){
        cu.start();
    }

    // Program address ALWAYS starts from 0, in this CPU simulator.
    @Test
    public void test_hlt(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x09000000);
        cu.start();
        assertEquals(true, cu.hasHalted());
        assertEquals(4, register.pc);
    }

    @Test
    public void test_load(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        RegisterFile generalRegister = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        memory.write(memoryIndex++, 0x0600ff01); // Address 0: 0x0600ff01
        memory.write(memoryIndex++, 0x0000000c);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x000000ff); // Address C: 0x000000ff
        cu.start();
        assertEquals(0xff, generalRegister.read(1));
    }

    @Test
    public void test_store(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x00000014);
        memory.write(memoryIndex++, 0x0701ff00);
        memory.write(memoryIndex++, 0x00000018);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x00000000);
        cu.start();
        assertEquals(0xff, memory.read(6));
    }

    @Test
    public void test_add(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        RegisterFile generalRegister = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x00000018);
        memory.write(memoryIndex++, 0x0600ff02);
        memory.write(memoryIndex++, 0x0000001c);
        memory.write(memoryIndex++, 0x00010203);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x000000ff);
        cu.start();
        assertEquals(0xff + 0xff, generalRegister.read(3));
    }

    @Test
    public void test_sub_with_result_zero(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        RegisterFile generalRegister = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x00000018);
        memory.write(memoryIndex++, 0x0600ff02);
        memory.write(memoryIndex++, 0x0000001c);
        memory.write(memoryIndex++, 0x01010203);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x000000ff);
        cu.start();
        assertEquals(0xff - 0xff, generalRegister.read(3));
    }

    @Test
    public void test_unconditional_jmp(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x0000001c);
        memory.write(memoryIndex++, 0x0600ff02);
        memory.write(memoryIndex++, 0x00000020);
        memory.write(memoryIndex++, 0x00010203);
        memory.write(memoryIndex++, 0x0800ff00);
        memory.write(memoryIndex++, 0x00000024);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x09000000);
        cu.start();
        assertEquals(40, register.pc);
    }

    @Test
    public void test_jmp_zero(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x0000001c);
        memory.write(memoryIndex++, 0x0600ff02);
        memory.write(memoryIndex++, 0x00000020);
        memory.write(memoryIndex++, 0x01010203);
        memory.write(memoryIndex++, 0x0801ff00);
        memory.write(memoryIndex++, 0x00000024);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x09000000);
        cu.start();
        assertEquals(40, register.pc);
    }

    @Test
    public void test_jmp_not_zero(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x0000001c);
        memory.write(memoryIndex++, 0x0600ff02);
        memory.write(memoryIndex++, 0x00000020);
        memory.write(memoryIndex++, 0x00010203);
        memory.write(memoryIndex++, 0x0802ff00);
        memory.write(memoryIndex++, 0x00000024);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x000000ff);
        memory.write(memoryIndex++, 0x09000000);
        cu.start();
        assertEquals(40, register.pc);
    }

    @Test
    public void test_push(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x00000010);
        memory.write(memoryIndex++, 0x0a010000);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x00000005);
        cu.start();
        assertEquals(5, memory.read(register.sp / 4));
    }

    @Test
    public void test_pop(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        // Register register = cu.getComponent(ComponentName.REGISTER);
        memory.write(memoryIndex++, 0x0600ff01);
        memory.write(memoryIndex++, 0x00000014);
        memory.write(memoryIndex++, 0x0a010000);
        memory.write(memoryIndex++, 0x0b000002);
        memory.write(memoryIndex++, 0x09000000);
        memory.write(memoryIndex++, 0x00000005);
        cu.start();
        RegisterFile rf = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        assertEquals(5, rf.read(2));
    }

    @Test
    public void test_call_ret(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        // Register register = cu.getComponent(ComponentName.REGISTER);
        /*  0 */ memory.write(memoryIndex++, 0x0600ff01); // main:
        /*  1 */ memory.write(memoryIndex++, 0x00000034);
        /*  2 */ memory.write(memoryIndex++, 0x0600ff02);
        /*  3 */ memory.write(memoryIndex++, 0x00000030);
        /*  4 */ memory.write(memoryIndex++, 0x0c00ff00); // call addOne
        /*  5 */ memory.write(memoryIndex++, 0x00000024);
        /*  6 */ memory.write(memoryIndex++, 0x00020202);
        /*  7 */ memory.write(memoryIndex++, 0x0800ff00); // jmp exit
        /*  8 */ memory.write(memoryIndex++, 0x0000002c);
        /*  9 */ memory.write(memoryIndex++, 0x00010201); // addOne:
        /* 10 */ memory.write(memoryIndex++, 0x0d000000); // ret
        /* 11 */ memory.write(memoryIndex++, 0x09000000); // exit:
        /* 12 */ memory.write(memoryIndex++, 0x00000001);
        /* 13 */ memory.write(memoryIndex++, 0x00000005);
        cu.start();
        RegisterFile rf = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        assertEquals(6, rf.read(1));
        assertEquals(2, rf.read(2));
    }
}
