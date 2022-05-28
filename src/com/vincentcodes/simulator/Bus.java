package com.vincentcodes.simulator;

public interface Bus extends CpuComponent{
    /**
     * @param from defines what EndPoints can go from this bus
     */
    void connectFrom(BusEndPoint... from);

    /**
     * @param to defines where data can go to
     */
    void connectTo(BusEndPoint... to);

    /**
     * Transfer data from one point to another.
     * Remember to add functionality to each endpoints.
     * 
     * It is not recommended to use {@code default}
     * case, if you are doing a {@code switch} statement.
     */
    void transfer(BusEndPoint from, BusEndPoint to);
}
