/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 * This class is a container for the information that is returned when the user authenticates
 * @author rdinarte
 */
public class UserBase {

    // Attributes
    // -------------------------------------------------------------------------
    private int userId;
    private String userName;
    private String name;
    private String lastName1;
    private String lastName2;
    private String type;

    public UserBase(int userId, String userName, String name, String lastName1, String lastName2, 
                    String type)
    {
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.lastName1 = lastName1;
        this.lastName2 = lastName2;
        this.type = type;
    }

    // Constructor
    // -------------------------------------------------------------------------


    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Get the user's type (administrator, normal user, etc)
     *
     * @return the user's type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the user's lastName 2
     *
     * @return the user's lastName 2
     */
    public String getLastName2() {
        return lastName2;
    }

    /**
     * Get the user's lastName 1
     *
     * @return the user's lastName 1
     */
    public String getLastName1() {
        return lastName1;
    }

    /**
     * Get the user's name
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the user's login
     *
     * @return the user's login
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get the user's id
     *
     * @return the user's id
     */
    public int getUserId() {
        return userId;
    }

}