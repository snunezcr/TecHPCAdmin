/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.db;

import db.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import model.Application;

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
    public Application[] GetUsersApplications(final int userId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.UserAppsParamUserId, userId),
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.UserAppsSp, parameters);
        List<Application> result = new LinkedList<Application>();
        while(reader.next())
        {
            int id = reader.getInt(Constants.UserAppsColId);
            String description = reader.getString(Constants.UserAppsColDescription);
            String relativePath = reader.getString(Constants.UserAppsColRelativePath);
            Date updateDate = reader.getDate(Constants.UserAppsColUpdateDate);
            Application application = new Application(id, description, relativePath, updateDate);
            result.add(application);
        }
        dataHelper.CloseConnection(reader);
        return result.toArray(new Application[result.size()]);
    }

}
