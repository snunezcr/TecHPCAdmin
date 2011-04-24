/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security.db;

import db.*;
import java.sql.ResultSet;
import model.UserBase;

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
    public UserBase Login(String userName, String password) throws java.sql.SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.LoginParamUserName, userName),
            new SqlParameter(Constants.LoginParamPassword, password)
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.LoginSp, parameters);
        UserBase result = null;
        if(reader.next())
        {
            int id = reader.getInt(Constants.LoginColId);
            String name = reader.getString(Constants.LoginColName);
            String lastName1 = reader.getString(Constants.LoginColLastName1);
            String lastName2 = reader.getString(Constants.LoginColLastName2);
            String role = reader.getString(Constants.LoginColRole);
            result = new UserBase(id, userName, name, lastName1, lastName2, role);
        }
        dataHelper.CloseConnection(reader);
        return result;
    }

}
