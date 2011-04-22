/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security;

import common.CommonFunctions;
import common.ServiceResult;
import security.db.SecurityDataManager;

/**
 * This class does all the tasks related to security, such as authentication, authorization
 * and users administration.
 * @author rdinarte
 */
public class SecurityManager {

    // Attributes
    // -------------------------------------------------------------------------
    private SecurityDataManager dataManager;
    private static SecurityManager instance = new SecurityManager();

    // Constructor
    // -------------------------------------------------------------------------
    private SecurityManager()
    {
        dataManager = new SecurityDataManager();
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static SecurityManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Validates user's credentials
     * @param userName The userName to validate
     * @param password The password to validate
     * @return A value indicating if the credentials were valid
     */
    public ServiceResult<Integer> Login(final String userName, final String password)
    {
        try
        {
            Integer result = dataManager.Login(userName, password);
            return new ServiceResult<Integer>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

}
