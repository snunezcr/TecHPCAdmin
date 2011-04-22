/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security.db;

import db.*;
import java.sql.ResultSet;

/**
 * This class do all the database operations related to security, it's an auxiliar class to
 * SecurityManager
 * @author rdinarte
 */
public class SecurityDataManager {

    // Attributes
    // -------------------------------------------------------------------------
    private DataHelper dataHelper;

    // Constructor
    // -------------------------------------------------------------------------
    public SecurityDataManager()
    {
        dataHelper = new DataHelper();
    }

    // Instance methods
    // --------------------------------------------------------------------------
    /**
     * Executes a stored procedure to validate if a user with the given username and password exists
     * @param userName The userName to validate
     * @param password The password to validate
     * @return The id of the logged user
     * @throws java.sql.SQLException if the stored procedure couldn't be executed
     */
    public Integer Login(String userName, String password) throws java.sql.SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.LoginParamUserName, userName),
            new SqlParameter(Constants.LoginParamPassword, password)
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.LoginSp, parameters);
        Integer result = -1;
        if(reader.next())
            result = reader.getInt(Constants.LoginColResult);
        dataHelper.CloseConnection(reader);
        return result;
    }

}
