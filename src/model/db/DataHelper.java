/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package db;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class can be used to execute stored procedures against an specific database
 * @author rdinarte
 */
public class DataHelper {
    
    // Attributes
    // -------------------------------------------------------------------------
    private Connection connection;
    private final static String Driver = "org.postgresql.Driver";
    private final static String ConnectionString = "jdbc:postgresql://localhost/HPCA";
    private final static String UserName = "postgres";
    private final static String Password = "123";

    // Constructor
    // -------------------------------------------------------------------------
    public DataHelper()
    {
        try
        {
            //This line is necessary to start using the jdbc driver
            Class.forName(Driver);
            connection = DriverManager.getConnection(ConnectionString,UserName, Password);
        }
        catch(Exception e)
        {
        }
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * This method returns a new connection instance
     * @throws ClassNotFoundException if the JDBC driver cannot be found
     * @throws SQLException if the connection attempt to the database fails
     */
    public static Connection GetNewConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName(Driver);
        return DriverManager.getConnection(ConnectionString, UserName, Password);
    }

    /**
     * When using pagination, this method can be used to know the start row that should be retrieved
     * from the database
     * @param pageNumber The page that will be displayed in the pagination
     * @param displayItems The number of items that are being displayed by page
     * @return The start row that should be requested
     */
    public static int GetStartRowForSP(final int pageNumber, final int displayItems)
    {
        return (pageNumber - 1) * displayItems + 1;
    }

    /**
     * When using pagination, this method can be used to know the final row that should be retrieved
     * from the database
     * @param pageNumber The page that will be displayed in the pagination
     * @param displayItems The number of items that are being displayed by page
     * @return The end row that should be requested
     */
    public static int GetEndRowForSP(final int pageNumber, final int displayItems)
    {
        return pageNumber * displayItems;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Executes a stored procedure that returns data from the database
     * @param spName The name of the stored procedure that will be executed
     * @param parameters The parameters of the stored procedure
     * @return The resulting resultset of the stored procedure
     * @throws SQLException if the stored procedure doesn't exist, the parameters are wrong or
     * the execution failed
     */
    public ResultSet ExecuteSP(final String spName, final SqlParameter[] parameters)
            throws SQLException
    {
        Connection conn = getConnection();
        ResultSet result = executeSP(conn, spName, parameters);
        return result;
    }

    /**
     * Executes a stored procedure that doesn't return data from the database
     * @param spName The name of the stored procedure that will be executed
     * @param parameters The parameters of the stored procedure
     * @return A boolean that indicates if the stored procedure returned any resultset
     * @throws SQLException if the stored procedure doesn't exist, the parameters are wrong or
     * the execution failed
     */
    public boolean ExecuteNoResultsetSP(final String spName, final SqlParameter[] parameters)
            throws SQLException
    {
        boolean boolResult = true;
        Connection conn = getConnection();
        ResultSet result = executeSP(conn, spName, parameters);
        if (result == null)
            boolResult = false;
        return boolResult;
    }

    /**
     * Closes the underlying connection
     */
    public void CloseConnection()
    {
        try
        {
            connection.close();
            connection = null;
        }
        catch(Exception e)
        {
        }
    }

    /**
     * Closes the connection associated to a resultset
     * @param result The resultset whose connection will be closed
     */
    public void CloseConnection(final ResultSet result)
    {
        try
        {
            Connection conn = result.getStatement().getConnection();
            result.getStatement().close();
            if(conn != null && !conn.isClosed())
                conn.close();
            conn = null;
            connection = null;
        }
        catch(Exception e)
        {
        }
    }

    // Private instance methods
    // -------------------------------------------------------------------------
    /**
     * Executes a stored procedure
     * @param conn The connection to execute the stored procedure
     * @param spName The name of the stored procedure that will be executed
     * @param parameters The parameters of the stored procedure
     * @return The resulting resultset of the stored procedure, if it is a write only stored
     * procedure returns null
     * @throws SQLException if the stored procedure doesn't exist, the parameters are wrong or
     * the execution failed
     */
    private ResultSet executeSP(final Connection conn, final String spName,
            final SqlParameter[] parameters) throws SQLException
    {
        String spText = "{ call " + spName + " ( ";
        int length = parameters.length;
        if(length > 0)
        {
            spText += "? ";
            for(int paramIndex = 1; paramIndex < length; paramIndex++)
                spText += ",? ";
        }
        spText += ") }";
        CallableStatement statement = conn.prepareCall(spText);
        for(int paramIndex = 0; paramIndex < length; paramIndex++)
        {
            SqlParameter param = parameters[paramIndex];
            Object value = param.getValue();
            if(value instanceof java.util.Date)
            {
                Date sqlDate = new Date(((java.util.Date) value).getTime());
                statement.setDate(param.getIndex(), sqlDate);
            }
            else
                statement.setObject(param.getIndex(), param.getValue());
        }
        ResultSet result = null;
        if(statement.execute())
            result = statement.getResultSet();
        return result;
    }

    /**
     * Opens the connection in case it was closed or disposed
     * @return An opened connection
     * @throws SQLException if the connection couldn't be retrieved
     */
    private Connection getConnection() throws SQLException
    {
        if (connection == null || connection.isClosed())
            openConnection();
        return connection;
    }

    /**
     * Opens a new connection
     * @throws SQLException if the connection couldn't be created
     */
    private void openConnection() throws SQLException
    {
        connection = DriverManager.getConnection(ConnectionString, UserName, Password);
    }

}
