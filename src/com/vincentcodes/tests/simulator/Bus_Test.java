package com.vincentcodes.tests.simulator;

import static org.junit.Assert.assertThrows;

import com.vincentcodes.simulator.BusEndPoint;
import com.vincentcodes.simulator.CentralProcessor;
import com.vincentcodes.simulator.Bus;
import com.vincentcodes.simulator.impl.BusImpl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Bus Test")
public class Bus_Test {
    private Bus basicBus;

    @BeforeAll
    public void setup(){
        basicBus = new BusImpl(new CentralProcessor());
        basicBus.connectFrom(BusEndPoint.A);
        basicBus.connectTo(BusEndPoint.B);
    }

    @Test
    public void test_transfer_success(){
        basicBus.transfer(BusEndPoint.A, BusEndPoint.B);
    }

    @Test
    public void test_transfer_failure1(){
        assertThrows(IllegalArgumentException.class, ()->{
            basicBus.transfer(BusEndPoint.B, BusEndPoint.A);
        });
    }

    @Test
    public void test_transfer_failure2(){
        assertThrows(IllegalArgumentException.class, ()->{
            basicBus.transfer(BusEndPoint.A, BusEndPoint.C);
        });
    }
}
