/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 * This class is a container for the full information of a user
 * @author rdinarte
 */
public class User extends UserBase
{

    // Attributes
    // -------------------------------------------------------------------------
    private String password;

    // Constructor
    // -------------------------------------------------------------------------
    public User(int userId, String userName, String name, String lastName1, String lastName2,
                    String type, String password)
    {
        super(userId, userName, name, lastName1, lastName2, type);
        this.password = password;
    }


    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Get the user's password
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

}