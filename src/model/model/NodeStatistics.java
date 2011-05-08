/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author rdinarte
 */
public class NodeStatistics
{    // Attributes
    // -------------------------------------------------------------------------
    private int nodeNumber;
    private int totalTime;
    private float usedMemory;
    private float cpuUsage;

    // Constructor
    // -------------------------------------------------------------------------
    public NodeStatistics(final int nodeNumber, final int totalTime, final float usedMemory,
                          final float cpuUsage)
    {
        this.nodeNumber = nodeNumber;
        this.totalTime = totalTime;
        this.usedMemory = usedMemory;
        this.cpuUsage = cpuUsage;
    }

    /**
     * Get the id of the node
     * @return The id of the node
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * Gets the time that the experiment was using the processor in this node
     * @return the time that the experiment was using the processor in this node
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Gets the amount of memory that the experiment was using in this node
     * @return the amount of memory that the experiment was using in this node 
     */
    public float getUsedMemory() {
        return usedMemory;
    }

    /**
     * Gets the cpu usage of the experiment in this node
     * @return the cpu usage of the experiment in this node
     */
    public float getCpuUsage() {
        return cpuUsage;
    }

}
