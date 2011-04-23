/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications;

import applications.db.ApplicationDataManager;
import common.CommonFunctions;
import common.ServiceResult;
import files.DirectoryManager;
import files.io.FileIOManager;
import java.util.HashMap;
import model.Application;
import model.ApplicationBase;

/**
 * This class does all the tasks related to applications, such as installing applications,
 * uploading or retrieving them
 * @see ApplicationDataManager
 * @author rdinarte
 */
public class ApplicationManager {

    // Attributes
    // -------------------------------------------------------------------------
    private ApplicationDataManager dataManager;
    private static ApplicationManager instance = new ApplicationManager();

    // Constructor
    // -------------------------------------------------------------------------
    private ApplicationManager()
    {
        dataManager = new ApplicationDataManager();
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static ApplicationManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Retrieves all the applications that were uploaded by a user
     * @param userId The id of the owner of the applications
     * @return A list with all the user applications
     */
    public ServiceResult<HashMap<Integer, Application>> GetUsersApplications(final int userId)
    {
        try
        {
            HashMap<Integer, Application> result = dataManager.GetUsersApplications(userId);
            return new ServiceResult<HashMap<Integer, Application>>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Uploads a program an stores its metadata
     * @param application The application's metadata
     * @param ownerId The user that is uploading the application
     * @param inputFile The program that is being uploaded
     * @return A value indicating whether the operation was successful or not
     */
    public ServiceResult<Integer> CreateProgram(final ApplicationBase application,
                                                final int ownerId, final byte[] inputFile)
    {
        int applicationId = -1;
        try
        {
            //Let's check if the file is empty
            if(inputFile.length == 0)
                throw new Exception("El archivo estaba vacío.");

            //We're going to store the applications metadata in the db
            int resultId = dataManager.CreateProgram(application, ownerId);

            DirectoryManager dirManager = DirectoryManager.GetInstance();
            String path = dirManager.GetApplicationsPath(ownerId);
            FileIOManager ioManager = FileIOManager.GetInstance();
            String inputFileName = application.getRelativePath();
            String dir = inputFileName.substring(0, inputFileName.lastIndexOf('/'));

            //Now let's upload the file
            boolean result = DirectoryManager.GetInstance().CreateDirectory(path + dir);
            result &= ioManager.CreateNewFile(inputFile, path, inputFileName, true);
            if(!result)
                throw new Exception("No se pudo subir el ejecutable.");
            return new ServiceResult<Integer>(resultId);
        }
        catch(Exception ex)
        {
            if(applicationId != -1)
            {//If we didn't write to the db, then there's nothing to rollback
                //TODO: Eliminar el registro de la BD y el directorio
            }
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

}