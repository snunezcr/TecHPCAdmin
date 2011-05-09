/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;
import java.util.Date;
import java.util.LinkedList;

/**
 * This class is a container for an experiment configuration
 * @author rdinarte
 */
public class Experiment extends ExperimentBase
{

    // Constants
    // -------------------------------------------------------------------------
    public enum ExecStatus
    {
        Running,
        Stopped
    };

    // Attributes
    // -------------------------------------------------------------------------
    private Date creationDate;
    private ExecStatus status;
    private FolderStructure internalStructure;
    private LinkedList<ExperimentExecution> executionHistory;

    // Constructor
    // -------------------------------------------------------------------------
    public Experiment(
            int id, String name, String description, String executablePath,
            String inputParametersLine, String inputFilePath, Date creationDate,
            ExecStatus status, FolderStructure internalStructure)
    {
        super(id, name, description, executablePath, inputParametersLine, inputFilePath);
        this.creationDate = creationDate;
        this.status = status;
        this.internalStructure = internalStructure;
        this.executionHistory = new LinkedList<ExperimentExecution>();
    }
    /**
     * Returns the information of all the executions of this experiment (output
     * and statistics of the execution).
     * @return Array with execution information.
     */
    public ExperimentExecution[] getExecutionHistory()
    {
        return executionHistory.toArray(new ExperimentExecution[executionHistory.size()]);
    }
    /**
     * Sets the execution history of the experiment (mostly dates, output
     * and statistics for each each execution).
     * @param executionHistory Array with historic information for each execution
     * of the experiment.
     */
    public void AddExecutionHistory(ExperimentExecution executionHistory)
    {
        this.executionHistory.add(executionHistory);
    }

    /**
     * Returns internal structure of the experiment that represents all the files
     * that it contains
     * @return File structure of the experiment
     */
    public FolderStructure getInternalStructure()
    {
        return internalStructure;
    }

    /**
     * Execution status of the experiment
     * @return Tells whether the experiment is running or not
     */
    public ExecStatus getExecutionStatus()
    {
        return status;
    }

    /**
     * Date in which the experiment was configured
     * @return Configuration date
     */
    public Date getCreationDate()
    {
        return creationDate;
    }

    public String getProccessedParameterLine()
    {
        String finalParameterLine = getInputParametersLine();
        ExperimentParameter[] parameters = getParameters();
        for(int parI = 0; parI < parameters.length; parI++)
        {
            ExperimentParameter par = parameters[parI];
            String expression = "\\$\\(" + par.getName() + "\\)";
            finalParameterLine = finalParameterLine.replaceAll(
                    expression, par.getValue().toString());
        }
        return finalParameterLine;
    }

    /**
     * Sets the execution status of the experiment
     * @param status Tells whether the experiment is running or not
     */
    public void setExecutionStatus(ExecStatus status) {
        this.status = status;
    }

}
