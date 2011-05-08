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
import model.NodeStatistics;
import model.User;
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

    /**
     * Creates a new user
     * @param newUser The users data that will be saved
     * @return The new user's id if the userName is unique, empty otherwise
     */
    public ServiceResult<Integer> CreateUser(User newUser)
    {
         return SecurityManager.GetInstance().CreateUser(newUser);
    }

    /**
     * Gets all the system users
     * @return all the system users
     */
    public ServiceResult<HashMap<Integer, User>> GetAllUsers()
    {
        return SecurityManager.GetInstance().GetAllUsers();
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
     * Gets the existing user roles
     * @param request The request of the page that is invoking the function
     * @return the existing user roles
     */
    public ServiceResult<String[]> GetUserRoles(final HttpServletRequest request)
    {
        ServiceResult<String[]> result = SessionManager.GetUserRoles(request);
        if(result == null)
        {
            result = CommonManager.GetInstance().GetUserRoles();
            SessionManager.SetUserRoles(request, result);
        }
        return result;
    }

    // Application methods
    // -------------------------------------------------------------------------
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
     * @param request The request of the page that is invoking this method
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

    /**
     * Installs a program from a compressed file or a script file
     * @param request The request of the page that is invoking this method
     * @param description The description of the installed program
     * @param folder The folder where the program will be stored
     * @param fileName The name of the installer file
     * @param fileContent The binary content of the installer file
     * @return True if the program could be installed, otherwise false
     */
    public ServiceResult<Boolean> InstallProgram(final HttpServletRequest request,
            final String description, final String folder, final String fileName,
            final byte[] fileContent)
    {
        int userId = SessionManager.GetUserId(request);
        return ApplicationManager.GetInstance()
                .InstallProgram(userId, description, folder, fileName, fileContent);
    }

    /**
     * Installs a program from a repository
     * @param request The request of the page that is invoking this method
     * @param description The description of the installed program
     * @param folder The folder where the program will be stored
     * @param repository The repository from which the installation is done (CVS, SVN or github)
     * @param url The url of the repository
     * @return True if the program could be installed, otherwise false
     */
    public ServiceResult<Boolean> InstallProgram(final HttpServletRequest request,
            final String description, final String folder, final String repository,
            final String url)
    {
        int userId = SessionManager.GetUserId(request);
        return ApplicationManager.GetInstance()
                .InstallProgram(userId, description, folder, repository, url);
    }

    // Experiment methods
    // -------------------------------------------------------------------------
    /**
     * Gets the path in which an experiment stores it's output
     * @param request The request of the page that is invoking this function
     * @param experimentId The id of the experiment
     * @return the path in which an experiment stores it's output
     */
    public String GetExperimentsOutputPath(final HttpServletRequest request, final int experimentId)
    {
        int userId = SessionManager.GetUserId(request);
        return DirectoryManager.GetInstance().GetPathForExperimentOutput(userId, experimentId);
    }

    /**
     * Gets the statistics for each node for an specific execution
     * @param executionId The execution id
     * @return The nodes statistics
     */
    public ServiceResult<NodeStatistics[]> GetNodeStats(int executionId)
    {
        return ExperimentManager.GetInstance().GetNodeStats(executionId);
    }

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
