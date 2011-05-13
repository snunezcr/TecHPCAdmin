/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.db;

import db.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import model.Application;
import model.ApplicationBase;

/**
 * This class do all the database operations related to applications, it's an auxiliar class to
 * ApplicationsManager
 * @see applications.ApplicationManager
 * @author rdinarte
 */
public class ApplicationDataManager {

    // Attributes
    // -------------------------------------------------------------------------
    private DataHelper dataHelper;

    // Constructor
    // -------------------------------------------------------------------------
    public ApplicationDataManager()
    {
        dataHelper = new DataHelper();
    }

    // Instance methods
    // --------------------------------------------------------------------------

    /**
     * Retrieves all the applications that were uploaded by a user
     * @param userId The id of the owner of the applications
     * @return A list with all the user applications
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public HashMap<Integer, Application> GetUsersApplications(final int userId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.UserAppsParamUserId, userId),
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.UserAppsSp, parameters);
        HashMap<Integer, Application> result = new HashMap<Integer, Application>();
        while(reader.next())
        {
            int id = reader.getInt(Constants.UserAppsColId);
            String description = reader.getString(Constants.UserAppsColDescription);
            String relativePath = reader.getString(Constants.UserAppsColRelativePath);
            Date updateDate = reader.getDate(Constants.UserAppsColUpdateDate);
            Application application = new Application(id, description, relativePath, updateDate);
            result.put(id, application);
        }
        dataHelper.CloseConnection(reader);
        return result;
    }

    /**
     * Saves the application's metadata to the database
     * @param application The applications metadata
     * @param ownerId The user that is uploading the application
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public int CreateProgram(final ApplicationBase application, final int ownerId)
            throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.NewProgramParamDescription, application.getDescription()),
            new SqlParameter(Constants.NewProgramParamRelativePath, application.getRelativePath()),
            new SqlParameter(Constants.NewProgramParamOwnerId, ownerId)
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.NewProgramSp, parameters);
        reader.next();
        int result = reader.getInt(Constants.NewProgramColId);
        dataHelper.CloseConnection(reader);
        return result;
    }

    public void RemoveApplication(final int id)
    {
        try
        {
            SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.RemoveAppParamId, id) };
            dataHelper.ExecuteNoResultsetSP(Constants.RemoveAppSp, parameters);
        }
        catch(Exception ex) { }
    }

}
