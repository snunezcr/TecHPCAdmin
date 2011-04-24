/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import applications.ApplicationManager;
import common.CommonFunctions;
import common.CommonManager;
import common.ServiceResult;
import config.ConfigurationManager;
import experiments.ExperimentManager;
import files.DirectoryManager;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import model.Application;
import model.ApplicationBase;
import model.Experiment;
import model.ExperimentBase;
import model.UserBase;
import security.SecurityManager;

/**
 * This is a facade to invoke all the business logic
 * @author rdinarte
 */
public class HpcaServiceAgent {

    // Constructor
    // -------------------------------------------------------------------------
    public HpcaServiceAgent()
    {
    }

    // Security methods
    // -------------------------------------------------------------------------
    /**
     * Validates user's credentials
     * @param userName The userName to validate
     * @param password The password to validate
     * @return A value indicating if the credentials were valid
     */
    public ServiceResult<UserBase> Login(final String userName, final String password)
    {
        return SecurityManager.GetInstance().Login(userName, password);
    }

    // Common methods
    // -------------------------------------------------------------------------
    public ServiceResult<String[]> GetParameterTypes(final HttpServletRequest request)
    {
        ServiceResult<String[]> result = SessionManager.GetParameterTypes(request);
        if(result == null)
        {
            result = CommonManager.GetInstance().GetParameterTypes();
            SessionManager.SetParameterTypes(request, result);
        }
        return result;
    }

    /**
     * Gets the path in which applications are stored for the current user
     * @param request The request of the page that is invoking this function
     * @return the path in which applications are stored for the current user
     */
    public String GetApplicationsPath(final HttpServletRequest request)
    {
        int userId = SessionManager.GetUserId(request);
        return DirectoryManager.GetInstance().GetApplicationsPath(userId);
    }

    // Application methods
    // -------------------------------------------------------------------------
    /**
     * Retrieves all the applications that were uploaded by the current user
     * @return A list with all the user applications
     */
    public ServiceResult<HashMap<Integer, Application>> GetUsersApplications(
            final HttpServletRequest request)
    {
        ServiceResult<HashMap<Integer, Application>> result =
                SessionManager.GetApplications(request);
        if(result == null)
        {
            int userId = SessionManager.GetUserId(request);
            result = ApplicationManager.GetInstance().GetUsersApplications(userId);
            SessionManager.SetApplications(request, result);
        }
        return result;
    }

    /**
     * Uploads a program an stores its metadata
     * @param request
     * @param application The application's metadata
     * @param inputFile The program that is being uploaded
     * @return A value indicating whether the operation was successful or not
     */
    public ServiceResult<Integer> CreateProgram(final HttpServletRequest request,
            final ApplicationBase application, final byte[] inputFile)
    {
        int userId = SessionManager.GetUserId(request);
        return ApplicationManager.GetInstance().CreateProgram(application, userId, inputFile);
    }

    // Experiment methods
    // -------------------------------------------------------------------------
    /**
     * Creates a new experiment
     * @param experiment The experiment that will be created
     * @param  inputFile The contents of the input file for the experiment
     * @return
     */
    public ServiceResult<Integer> CreateExperiment(final HttpServletRequest request, 
            final ExperimentBase experiment, final byte[] inputFile)
    {
        int ownerId = SessionManager.GetUserId(request);
        ServiceResult<Integer> result = ExperimentManager.GetInstance().
                CreateExperiment(ownerId, experiment, inputFile);
        return result;
    }

    /**
     * Verifies if the HttpSession already has the experiments of the user.
     * If not, it fills the session with the user experiments.
     * @param session Client session where the experiments should be saved
     */
    public ServiceResult<HashMap<Integer,Experiment>>
            GetExperiments(final HttpServletRequest request)
    {
        ServiceResult<HashMap<Integer,Experiment>> result = SessionManager.GetExperiments(request);
        if(result == null)
        {
            int userId = SessionManager.GetUserId(request);
            ServiceResult<Experiment[]> expResult = ExperimentManager.GetInstance().
                    GetUsersExperiments(userId);
            
            if(expResult.getStatus() == ServiceResult.OperationResult.Error)
                result = CommonFunctions.CreateErrorServiceResult(expResult);
            else
            {
                Experiment[] experiments = expResult.getValue();
                HashMap<Integer, Experiment> expHash = new HashMap<Integer, Experiment>();
                for(Experiment exp : experiments)
                    expHash.put(exp.getId(), exp);
                result = new ServiceResult<HashMap<Integer, Experiment>>(expHash);
            }
            SessionManager.SetExperiments(request, result);
        }
        return result;
    }

    /**
     * Starts the execution of a experiment with its configured parameters
     * @param session Client session where the experiments information should be
     * @param exId Identifier of the experiment to be started
     * @return Returns whether the experiment started successfully or not
     */
    public ServiceResult<Boolean> StartExperiment(final HttpServletRequest request, int exId)
    {
        ServiceResult<HashMap<Integer,Experiment>> experiments = GetExperiments(request);
        ServiceResult<Boolean> result;
        if(experiments.getStatus() != ServiceResult.OperationResult.Error)
        {
            HashMap<Integer, Experiment> expHash = experiments.getValue();
            if(expHash.containsKey(exId))
            {
                Experiment exp = expHash.get(exId);
                int userId = SessionManager.GetUserId(request);
                result = ExperimentManager.GetInstance().StartExperiment(userId, exp);
            }
            else
            {
                result = new ServiceResult<Boolean>(false);
                result.AddError(Constants.ExperimentDoesNotExist);
            }
        }
        else
        {
            result = new ServiceResult<Boolean>(false);
            result.AddError(Constants.ExperimentStartFailed);
        }
        return result;
    }

    /**
     * Stops a experiment that was previously launched by the user
     * @param session Client session where the experiments information should be
     * @param exId Identifier of the experiment to be started
     * @return Returns whether the experiment stopped successfully or not
     */
    public ServiceResult<Boolean> StopExperiment(final HttpServletRequest request, int exId)
    {
        ServiceResult<HashMap<Integer,Experiment>> experiments = GetExperiments(request);
        ServiceResult<Boolean> result;
        if(experiments.getStatus() != ServiceResult.OperationResult.Error)
        {
            HashMap<Integer, Experiment> expHash = experiments.getValue();
            if(expHash.containsKey(exId))
            {
                int userId = SessionManager.GetUserId(request);
                Experiment exp = expHash.get(exId);
                return ExperimentManager.GetInstance().StopExperiment(userId, exp);
            }
            else
            {
                result = new ServiceResult<Boolean>();
                result.AddError(Constants.ExperimentDoesNotExist);
            }
        }
        else
        {
            result = new ServiceResult<Boolean>();
            result.AddError(Constants.ExperimentStopFailed);
        }
        return result;
    }

    // Configuration methods
    // -------------------------------------------------------------------------
    /**
     * Gets the number of nodes that can be used for execution in the cluster
     * @return The number of nodes that can be used for execution in the cluster
     */
    public int GetNumberOfClusterNodes()
    {
        return ConfigurationManager.GetInstance().GetNumberOfClusterNodes();
    }

    /**
     * Gets the default number of nodes that will be used for an experiment creation
     * @return The default number of nodes that will be used for an experiment creation
     */
    public int GetDefaultNumberOfNodesForExecution()
    {
        return ConfigurationManager.GetInstance().GetDefaultNumberOfNodesForExecution();
    }

}
