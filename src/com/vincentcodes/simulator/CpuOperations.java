package com.vincentcodes.simulator;

import com.vincentcodes.simulator.util.VoidFunction;

/**
 * Defines a bunch of supported instructions for
 * the Control Unit.
 */
public interface CpuOperations {
    default VoidFunction[] getAllOperations() {
        return new VoidFunction[]{
            this::add, this::sub, this::not, this::and, this::or, 
            this::mov, this::ld, this::st, this::jmp, this::hlt,
            this::push, this::pop, this::call, this::ret
        };
    }

    void add();
    void sub();
    void not();
    void and();
    void or();
    void mov();
    void ld();
    void st();
    void jmp();
    void hlt();
    void push();
    void pop();
    void call();
    void ret();
}
