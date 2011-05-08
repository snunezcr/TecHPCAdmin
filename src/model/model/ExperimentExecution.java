/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;
import java.util.Date;

/**
 *
 * @author cfernandez
 */
public class ExperimentExecution
{
    // Attributes
    // -------------------------------------------------------------------------
    private Date startDate;
    private Date endDate;
    private String outputFilePath;
    private float usedMemoryPercentage;
    private float cpuUsagePercentage;
    private int cpuTimeSeconds;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentExecution(final Date startDate, final Date endDate,
            final String outputFilePath, final float usedMemoryPercentage,
            final float cpuUsagePercentage, final int cpuTimeSeconds)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.outputFilePath = outputFilePath;
        this.usedMemoryPercentage = usedMemoryPercentage;
        this.cpuUsagePercentage = cpuUsagePercentage;
        this.cpuTimeSeconds = cpuTimeSeconds;
    }

    /**
     * Returns the date at which the execution of the experiment ended
     * @return Execution end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Returns the date at which the execution of the experiment started
     * @return Execution start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the file path of the experiment execution output
     * @return Execution start date
     */
    public String getOutputPath() {
        return outputFilePath;
    }

    /**
     * Returns percentage of used memory. Range 0.0 to 100.0
     * @return Execution average used memory percentage
     */
    public float getUsedMemory() {
        return usedMemoryPercentage;
    }

    /**
     * Returns percentage of CPU usage. Range 0.0 to 100.0
     * @return Execution average CPU usage percentage
     */
    public float getCPUUsage() {
        return cpuUsagePercentage;
    }

    /**
     * Returns in seconds the time the CPU has been used.
     * @return System time of experiment CPU usage
     */
    public int getCPUTimeSeconds() {
        return cpuTimeSeconds;
    }
}
