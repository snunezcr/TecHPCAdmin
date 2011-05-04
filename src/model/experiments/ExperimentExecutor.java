/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;

import files.DirectoryManager;
import java.io.IOException;
import model.Experiment;
import java.util.Date;
import java.util.Observable;
import model.UserExperimentMapping;

/**
 * This class does all the tasks related to the execution of the experiments.
 * It includes starting and stopping the experiment. It also checks if the
 * experiment execution finished.
 * @author cfernandez
 */
public class ExperimentExecutor
{
    // Attributes
    // -------------------------------------------------------------------------
    private Experiment executedExperiment;
    private Process experimentProcess;
    private ProcessVerifier verifier;
    private Date startDate;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentExecutor(Experiment experimentToExecute)
    {
        executedExperiment = experimentToExecute;
    }

    /**
     * Gets the experiment that is is being executed by this instance.
     * @return Experiment that was put under execution by this instance.
     */
    public Experiment getExecutedExperiment() {
        return executedExperiment;
    }
    /**
     * Date at which the experiment was put under execution.
     * @return Date of the execution start.
     */
    public Date getStartDate() {
        return startDate;
    }
    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Starts to run a experiment previously configured by the user
     * @param obs Object to notify when the experiment execution finishes.
     * @param userId User that is executing the experiment.
     * @return Indicates whether the experiment started successfully or not.
     */
    public Boolean RunExperiment(Observable obs, int userId)
    {
        try
        {
            // Address of the executable file of the experiment.
            String execAddr = DirectoryManager.GetInstance().
                              GetPathForExperimentExecution(userId, executedExperiment.getId()) +
                              executedExperiment.getExecutablePath() +
                              " " + executedExperiment.getInputParametersLine();
            experimentProcess = Runtime.getRuntime().exec(execAddr);
            startDate = new Date();
            verifier = new ProcessVerifier(experimentProcess, obs,
                    new UserExperimentMapping(executedExperiment.getId(), userId));
            verifier.start();
            return true;
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Stops the execution of the experiment ONLY if was previously running
     * @return Indicates whether the experiment started successfully or not.
     */
    public Boolean StopExperiment()
    {
        if(experimentProcess != null)
        {
            verifier.interrupt();
            experimentProcess = null;
            verifier = null;
            return true;
        }
        else return false;
    }
}
