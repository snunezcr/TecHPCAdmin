/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security.db;

import db.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import model.User;
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
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public UserBase Login(String userName, String password) throws SQLException
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

    /**
     * Stores a new users data to the DB
     * @param newUser The users data that will be saved
     * @return The new user id if the userName is unique, -1 otherwise
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public int CreateUser(User newUser) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.NewUserParamName, newUser.getName()),
            new SqlParameter(Constants.NewUserParamLastName1, newUser.getLastName1()),
            new SqlParameter(Constants.NewUserParamLastName2, newUser.getLastName2()),
            new SqlParameter(Constants.NewUserParamPassword, newUser.getPassword()),
            new SqlParameter(Constants.NewUserParamRole, newUser.getType()),
            new SqlParameter(Constants.NewUserParamUserName, newUser.getUserName())
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.NewUserSp, parameters);
        reader.next();
        int result = reader.getInt(Constants.NewExperimentColId);
        dataHelper.CloseConnection(reader);
        return result;
    }

    /**
     * Gets all the system users
     * @return all the system users
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public HashMap<Integer, User> GetAllUsers() throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[0];
        ResultSet reader = dataHelper.ExecuteSP(Constants.AllUsersSp, parameters);
        HashMap<Integer, User> result = new HashMap<Integer, User>();
        while(reader.next())
        {
            int id = reader.getInt(Constants.AllUsersColId);
            String name = reader.getString(Constants.AllUsersColName);
            String userName = reader.getString(Constants.AllUsersColUserName);
            String lastName1 = reader.getString(Constants.AllUsersColLastName1);
            String lastName2 = reader.getString(Constants.AllUsersColLastName2);
            String role = reader.getString(Constants.AllUsersColRole);
            boolean enabled = reader.getBoolean(Constants.AllUsersColEnabled);
            Date creationDate = reader.getDate(Constants.AllUsersColCreationDate);
            User user = new User(id, userName, name, lastName1, lastName2, role, "", enabled,
                    creationDate);
            result.put(id, user);
        }
        dataHelper.CloseConnection(reader);
        return result;
    }

}
