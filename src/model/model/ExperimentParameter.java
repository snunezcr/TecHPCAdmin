/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author cfernandez
 */
public class ExperimentParameter
{

    // Attributes
    // -------------------------------------------------------------------------
    private String name;
    private String type;
    private Object value;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentParameter(String name, String type, Object value)
    {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the name of the parameter. Is unique per experiment
     * @return Parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the parameter. It can be float, integer, string or char
     * @return Parameter type, see the enumeration above.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the value of the parameter. It is in accord to its type
     * @return Parameter value. Depends on its type.
     */
    public Object getValue() {
        return value;
    }

}
