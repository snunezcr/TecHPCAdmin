/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.LinkedList;

/**
 * This class is a container for an experiment configuration
 * @author rdinarte
 */
public class ExperimentBase {

    // Attributes
    // -------------------------------------------------------------------------
    private int id;
    private String name;
    private String description;
    private String executablePath;
    private boolean parallelExecution = false;
    private String inputParametersLine;
    private String inputFilePath;
    private LinkedList<ExperimentParameter> parameters;

    // Attributes for parallel execution
    // -------------------------------------------------------------------------
    private int numberOfProcessors = 1;
    private String middleware = "";
    private String sharedWorkingDirectory = "";
    private boolean saveEachNodeLog = false;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentBase(final int id, final String name, final String description,
                      final String executablePath, final String inputParametersLine,
                      final String inputFilePath)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.executablePath = executablePath;
        this.inputParametersLine = inputParametersLine;
        this.inputFilePath = inputFilePath;
        this.parameters = new LinkedList<ExperimentParameter>();
    }

    /**
     * Set the id of the experiment
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the id of the experiment
     * @return the id of the experiment
     */
    public int getId() {
        return id;
    }

    /**
     * Get the description of the experiment
     * @return the description of the experiment
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the path of the application that will be executed in the experiment
     * @return the application's path
     */
    public String getExecutablePath() {
        return executablePath;
    }

    /**
     * Get the path of the input file that will be used for the experiment
     * @return the input file's path
     */
    public String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * Get the line used as an input for the experiment
     * @return the experiment input line
     */
    public String getInputParametersLine() {
        return inputParametersLine;
    }

    /**
     * Get the name of the experiment
     * @return the name of the experiment
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates if the experiment will be executed using multiple processors
     * @return if the experiment will be executed using multiple processors
     */
    public boolean usesParallelExecution() {
        return parallelExecution;
    }

    /**
     * Get the path of the middleware used to execute the experiment
     * @return the middleware path
     */
    public String getMiddleware() {
        return middleware;
    }

    /**
     * Get the number of processors used to execute the experiment
     * @return the number of processors used to execute the experiment
     */
    public int getNumberOfProcessors() {
        return numberOfProcessors;
    }

    /**
     * Indicates if the log of each node should be saved, or only one log will be saved
     * @return the indication of saving each node's log
     */
    public boolean willSaveEachNodeLog() {
        return saveEachNodeLog;
    }

    /**
     * Get the name of the shared directory used for the experiment execution
     * @return the shared working directory
     */
    public String getSharedWorkingDirectory() {
        return sharedWorkingDirectory;
    }

    /**
     * Returns the configured parameters for the executable of the experiment
     * @return Experiment parameter array
     */
    public ExperimentParameter[] getParameters()
    {
        return parameters.toArray(new ExperimentParameter[parameters.size()]);
    }


    // Instance Methods
    // -------------------------------------------------------------------------
    /**
     * Adds the configuration for executing the experiment using multiple processors
     * @param numberOfProcessors The number of processors used for executing the experiment
     * @param middleware The path of auxiliar software used to execute the experiment
     * @param sharedWorkingDirectory The path of the shared directory used for the execution
     * @param saveEachNodeLog Indicates if each node's log should be saved separately
     */
    public void AddParallelConfiguration(final int numberOfProcessors, final String middleware,
                                         final String sharedWorkingDirectory,
                                         final boolean saveEachNodeLog)
    {
        this.parallelExecution = true;
        this.numberOfProcessors = numberOfProcessors;
        this.middleware = middleware;
        this.sharedWorkingDirectory = sharedWorkingDirectory;
        this.saveEachNodeLog = saveEachNodeLog;
    }

    public void AddParameter(ExperimentParameter parameter)
    {
        parameters.add(parameter);
    }

}
