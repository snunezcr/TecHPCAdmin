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
public class ParallelExpExecution extends ExperimentExecution
{

    private NodeStatistics[] nodeStats;

    public ParallelExpExecution(final int executionId, final Date startDate, final Date endDate,
            final String outputFilePath, final float usedMemoryPercentage,
            final float cpuUsagePercentage, final int cpuTimeSeconds, NodeStatistics[] nodeStats)
    {
        super (executionId, startDate, endDate, outputFilePath, usedMemoryPercentage,
            cpuUsagePercentage, cpuTimeSeconds);
        this.nodeStats = nodeStats;
    }

    public NodeStatistics[] getNodeStatistics()
    {
        return nodeStats;
    }
}
