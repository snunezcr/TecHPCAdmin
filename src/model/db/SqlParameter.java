/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

/**
 * This class is used to contain information about parameters for stored procedures
 * @author rdinarte
 */
public class SqlParameter {

    // Attributes
    // -------------------------------------------------------------------------
    private int index;
    private Object value;

    // Constructor
    // -------------------------------------------------------------------------
    public SqlParameter(final int parameterIndex, final Object value)
    {
        this.index = parameterIndex;
        this.value = value;
    }

    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Returns the parameter index in the stored procedure parameters list
     * @return the value of the parameter index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the parameter value
     * @return the value of the parameter
     */
    public Object getValue()
    {
        return value;
    }
}
