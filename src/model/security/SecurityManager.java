/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security;

import common.CommonFunctions;
import common.ServiceResult;
import files.DirectoryManager;
import java.util.HashMap;
import model.User;
import model.UserBase;
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
    public ServiceResult<UserBase> Login(final String userName, final String password)
    {
        try
        {
            UserBase result = dataManager.Login(userName, password);
            if(result != null)
                return new ServiceResult<UserBase>(result);
            else 
                return new ServiceResult<UserBase>();
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Creates a new user
     * @param newUser The users data that will be saved
     * @return The new user's id if the userName is unique, empty otherwise
     */
    public ServiceResult<Integer> CreateUser(User newUser)
    {
        int userId = -1;
        try
        {
            userId = dataManager.CreateUser(newUser);
            if(userId == -1)//The userName already exists
                return new ServiceResult<Integer>();

            //Now we have to create the user directory structure
            DirectoryManager dirManager = DirectoryManager.GetInstance();
            newUser.setUserId(userId);
            boolean dirResult = dirManager.CreateUserStructure(newUser).getValue();
            if(!dirResult) //We have to manage this in the same way as a DB error
                throw new Exception("No se pudo crear la estructura de directorios.");

            //Everything went fine
            return new ServiceResult<Integer>(userId);
        }
        catch(Exception ex)
        {
            if(userId != -1)
                CleanUserData(userId); //An error occurred while trying to create the folder struct
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Gets all the system users
     * @return all the system users
     */
    public ServiceResult<HashMap<Integer, User>> GetAllUsers()
    {
        try
        {
            return new ServiceResult<HashMap<Integer, User>>(dataManager.GetAllUsers());
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    private void CleanUserData(final int userId)
    {//TODO: Eliminar el registro de la BD

    }

}
