/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;

import common.CommonFunctions;
import common.ServiceResult;
import experiments.db.ExperimentDataManager;
import files.DirectoryManager;
import files.io.FileIOManager;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Observer;
import java.util.Observable;
import model.ExperimentBase;
import model.Experiment;
import model.ExperimentExecution;
import model.ExperimentParameter;
import model.UserExperimentMapping;
import controller.Constants;
import files.LinuxUtilities;

/**
 * This class is responsible for all tasks related to experiments, such as creating new ones,
 * retrieving them or execute them
 * @see ExperimentDataManager
 * @author rdinarte
 */
public class ExperimentManager implements Observer
{

    // Attributes
    // -------------------------------------------------------------------------
    private ExperimentDataManager dataManager;
    private static ExperimentManager instance = new ExperimentManager();
    private static HashMap<Integer, HashMap<Integer, ExperimentExecutor>> eHash
            = new HashMap<Integer, HashMap<Integer, ExperimentExecutor>>();

    // Constructor
    // -------------------------------------------------------------------------
    private ExperimentManager()
    {
        dataManager = new ExperimentDataManager();
    }

    // Interface methods
    // -------------------------------------------------------------------------
    /**
     * We are using Observer Pattern to know when the execution of an experiment
     * has finished. This method removes a finished experiment of the
     * executingExperiment hash and calls the statistics manager
     * @param obs Observable that just finished to execute a experiment
     * @param x Data parameter from the observable. In this case is the experiment id.
     */
    public void update(Observable obs, Object x)
    {
        UserExperimentMapping usrExpMap = (UserExperimentMapping) x;
        if(eHash.containsKey(usrExpMap.getUserId()))
        {
            HashMap<Integer, ExperimentExecutor> userHash = eHash.get(usrExpMap.getUserId());
            if(userHash.containsKey(usrExpMap.getExpId()))
            {
                ExperimentExecutor executor = userHash.get(usrExpMap.getExpId());
                Experiment finishedExperiment = executor.getExecutedExperiment();
                finishedExperiment.setExecutionStatus(Experiment.ExecStatus.Stopped);
                Date startDate = executor.getStartDate();
                Date endDate = new Date();
                ExperimentExecution execution = new ExperimentExecution(startDate, endDate);
                try
                {
                    int result = dataManager.SaveExperimentExecution(execution,
                                                                     finishedExperiment.getId());
                    finishedExperiment.AddExecutionHistory(execution);
                }
                catch(Exception ex)
                {
                }
                executor.StopExperiment();
                userHash.remove(usrExpMap.getExpId());
            }
        }
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static ExperimentManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Creates a new experiment and creates the necessary disk structure
     * @param userId The id of the owner of the experiment
     * @param experiment The experiment that will be created
     * @return A structure to indicate if the creation was successful
     */
    public ServiceResult<Integer> CreateExperiment(final int userId, final ExperimentBase experiment,
            final byte[] inputFile)
    {
        try
        {//NOTE: If the creation of the directory structure or uploading the file fails, we should
         //remove the register from the db and try to delete the directory structure
            int experimentId = dataManager.CreateExperiment(userId, experiment);
            experiment.setId(experimentId);
            try
            {
                if(experiment.usesParallelExecution())
                    dataManager.AddParallelConfiguration(experiment);
                dataManager.AddExperimentParameters(experiment);
            }
            catch(Exception ex)
            {
                removeExperimentRegister(experimentId);
                return CommonFunctions.CreateErrorServiceResult(ex);
            }

            //Let's create the directory structure
            DirectoryManager dirManager = DirectoryManager.GetInstance();
            ServiceResult<Boolean> directoryResult = dirManager.
                    CreateExperimentsStructure(userId, experimentId);
            if(directoryResult.getStatus() != ServiceResult.OperationResult.Error)
            {
                String inputFileName = experiment.getInputFilePath();
                //Now let's upload the file
                String path = dirManager.GetPathForExperimentExecution(userId, experimentId);
                FileIOManager ioManager = FileIOManager.GetInstance();
                boolean result = inputFile.length == 0 ||
                        ioManager.CreateNewFile(inputFile, path, inputFileName, true);
                //Now let's copy the executable
                if (result)
                {
                    String experimentPath = experiment.getExecutablePath();
                    String originalPath = dirManager.GetApplicationsPath(userId) +
                            experimentPath;
                    String targetPath = dirManager.
                            GetPathForExperimentExecution(userId, experimentId) + experimentPath;
                    result = ioManager.CopyFile(originalPath, targetPath);
                    ServiceResult<Boolean> permissionsResult = LinuxUtilities.GetInstance().
                            SetFilePermissions(targetPath, LinuxUtilities.Execution);
                    result &= permissionsResult.getStatus() != ServiceResult.OperationResult.Error;
                }
                //Let's check if there was some error
                if(!result)
                {
                    cleanExperiment(experimentId);
                    Exception ex = new IOException("Couldn't create the file or set permissions.");
                    return CommonFunctions.CreateErrorServiceResult(ex);
                }
            }
            else
            {
                cleanExperiment(experimentId);
                return CommonFunctions.CreateErrorServiceResult(directoryResult);
            }
            //If we get until here then everything went fine
            return new ServiceResult<Integer>(experimentId);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Retrieves all the experiments that were configured by a user
     * @param userId The id of the owner of the experiments
     * @return A list with all the experiment configurations
     */
    public ServiceResult<Experiment[]> GetUsersExperiments(int userId)
    {
        try
        {
            Experiment[] result = dataManager.GetExperimentsGenInfo(userId);
            for(Experiment exp : result)
            {
                ExperimentParameter[] paramsResult = dataManager.GetExperimentParams(exp.getId());
                ExperimentExecution[] historyResult = dataManager.GetExperimentExecs(exp.getId());
                for(ExperimentParameter param : paramsResult)
                    exp.AddParameter(param);
                for(ExperimentExecution exec : historyResult)
                    exp.AddExecutionHistory(exec);
                // We verifiy if the experiment is already running
                if(eHash.containsKey(userId))
                {
                    HashMap<Integer, ExperimentExecutor> userHash = eHash.get(userId);
                    if(userHash.containsKey(exp.getId()))
                        exp.setExecutionStatus(Experiment.ExecStatus.Running);
                }
            }
            return new ServiceResult<Experiment[]>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }
    /**
     * Starts the execution of a experiment with its configured parameters
     * @param userId The id of the user that is running the experiment
     * @param exp Configuration information of the experiment to be run
     * @return Returns whether the experiment started successfully or not
     */
    public ServiceResult<Boolean> StartExperiment(
            final int userId, Experiment exp)
    {
        if(!eHash.containsKey(userId))
            eHash.put(userId, new HashMap<Integer, ExperimentExecutor>());
        HashMap expHash = eHash.get(userId);
        if(expHash.containsKey(exp.getId()))
        {
            ServiceResult<Boolean> errResult = new ServiceResult<Boolean>();
            errResult.AddError(Constants.AlreadyRunningErrorMessage);
            return errResult;
        }
        ExperimentExecutor exec = new ExperimentExecutor(exp);
        // We want the observable to notify us when the execution finished.
        Observable obs = new ExperimentObservable();
        obs.addObserver(this);
        boolean isRunning = exec.RunExperiment(obs, userId);
        ServiceResult<Boolean> result = new ServiceResult<Boolean>(isRunning);
        if(isRunning)
        {
            expHash.put(exp.getId(), exec);
            exp.setExecutionStatus(Experiment.ExecStatus.Running);
        }
        else
            result.AddError(Constants.ExperimentStartFailed);
        return result;
    }
    /**
     * Stops a experiment that was previously launched by the user
     * @param userId The id of the user that is stopping the experiment
     * @param exp Configuration information of the experiment to be stopped
     * @return Returns whether the experiment stopped successfully or not
     */
    public ServiceResult<Boolean> StopExperiment(
            final int userId, Experiment exp)
    {
        if(!eHash.containsKey(userId))
            eHash.put(userId, new HashMap<Integer, ExperimentExecutor>());
        HashMap<Integer, ExperimentExecutor> expHash = eHash.get(userId);
        if(!expHash.containsKey(exp.getId()))
        {
            ServiceResult<Boolean> errResult = new ServiceResult<Boolean>();
            errResult.AddError(Constants.NotRunningErrorMessage);
            return errResult;
        }
        ExperimentExecutor exec = expHash.get(exp.getId());
        boolean stopRun = exec.StopExperiment();
        ServiceResult<Boolean> result = new ServiceResult<Boolean>(stopRun);
        if(stopRun)
        {
            expHash.remove(exp.getId());
            exp.setExecutionStatus(Experiment.ExecStatus.Stopped);
        }
        else
            result.AddError(Constants.ExperimentStopFailed);
        return result;
    }

    private void cleanExperiment(final int experimentId)
    {
        //TODO: Implementar, deberia eliminar la estructura del directorio
        removeExperimentRegister(experimentId);
    }

    private void removeExperimentRegister(final int experimentId)
    {
        //TODO: Implementar, deberia eliminar el registro de la BD
    }

}