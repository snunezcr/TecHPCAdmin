/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import common.db.CommonDataManager;

/**
 * This class does tasks related to data that is shared or could be common to multiple packages
 * @see ApplicationDataManager
 * @author rdinarte
 */
public class CommonManager {

    // Attributes
    // -------------------------------------------------------------------------
    private CommonDataManager dataManager;
    private static CommonManager instance = new CommonManager();

    // Constructor
    // -------------------------------------------------------------------------
    private CommonManager()
    {
        dataManager = new CommonDataManager();
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static CommonManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Gets the available parameter types
     * @return the available parameter types
     */
    public ServiceResult<String[]> GetParameterTypes()
    {
        try
        {
            String[] result = dataManager.GetParameterTypes();
            return new ServiceResult<String[]>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Gets the existing user roles
     * @return the existing user roles
     */
    public ServiceResult<String[]> GetUserRoles()
    {
        try
        {
            String[] result = dataManager.GetUserRoles();
            return new ServiceResult<String[]>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

}