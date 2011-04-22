/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications;

import applications.db.ApplicationDataManager;
import common.CommonFunctions;
import common.ServiceResult;
import model.Application;

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
    public ServiceResult<Application[]> GetUsersApplications(final int userId)
    {
        try
        {
            Application[] result = dataManager.GetUsersApplications(userId);
            return new ServiceResult<Application[]>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

}