package com.vincentcodes.tests.simulator;

import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.CentralProcessor.ComponentName;
import com.vincentcodes.simulator.Memory;
import com.vincentcodes.simulator.RegisterFile;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ApplicationTest {
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
    public void test(){
        Memory memory = cu.getComponent(ComponentName.MEMORY);
        RegisterFile generalRegister = cu.getComponent(ComponentName.GENERAL_REGISTERS);
        var machineCode = """
                0600ff04
                0000003c
                0600ff01
                00000040
                05010002
                0600ff03
                00000044
                00040104
                00010201
                01030105
                0802ff00
                0000001c
                0704ff00
                00000048
                09000000
                00000000
                00000001
                0000000a
                00000000
                """;
        for(String line : machineCode.split("\n"))
            memory.write(memoryIndex++, Integer.parseInt(line, 16));
        cu.start();
    }
}
