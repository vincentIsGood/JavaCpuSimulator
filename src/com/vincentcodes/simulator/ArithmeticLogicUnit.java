package com.vincentcodes.simulator;

public class ArithmeticLogicUnit implements CpuComponent{
    private Register register;

    // Bus Endpoints
    public int a;
    public int b;
    public int c;

    public ArithmeticLogicUnit(Register register){
        this.register = register;
    }

    /**
     * Use current values in a, b to do an operation.
     * Result is stored in c. 
     */
    public void execute(Operation op, boolean setFlag){
        switch(op){
            case COPY: copy(); break;
            case ADD:  add();  break;
            case SUB:  sub();  break;
            case AND:  and();  break;
            case OR:   or();   break;
            case NOT:  not();  break;
        }
        if(setFlag)
            register.zeroFlag = (c == 0)? 1 : 0;
    }

    // Supported operations
    public static enum Operation{
        COPY, ADD, SUB, AND, OR, NOT,
    }

    public void copy(){
        c = a;
    }

    public void add(){
        c = a + b;
    }

    public void sub(){
        c = a - b;
    }

    public void and(){
        c = a & b;
    }

    public void or(){
        c = a | b;
    }

    public void not(){
        c = ~a;
    }

    @Override
    public void reset() {
        a = 0;
        b = 0;
        c = 0;
    }
}
