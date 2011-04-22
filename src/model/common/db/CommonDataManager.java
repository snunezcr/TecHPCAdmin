/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common.db;

import db.Constants;
import db.DataHelper;
import db.SqlParameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class do all the database operations related to applications, it's an auxiliar class to
 * ApplicationsManager
 * @see applications.ApplicationManager
 * @author rdinarte
 */
public class CommonDataManager {

    // Attributes
    // -------------------------------------------------------------------------
    private DataHelper dataHelper;

    // Constructor
    // -------------------------------------------------------------------------
    public CommonDataManager()
    {
        dataHelper = new DataHelper();
    }

    // Instance methods
    // --------------------------------------------------------------------------
    /**
     * Gets the available parameter types
     * @return the available parameter types
     * @throws SQLException if the stored procedure couldn't be executed
     */
    public String[] GetParameterTypes() throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[0];
        ResultSet reader = dataHelper.ExecuteSP(Constants.ParamTypesSp, parameters);
        List<String> result = new LinkedList<String>();
        while(reader.next())
            result.add(reader.getString(Constants.ParamTypesColName));
        dataHelper.CloseConnection(reader);
        return result.toArray(new String[result.size()]);
    }

}
