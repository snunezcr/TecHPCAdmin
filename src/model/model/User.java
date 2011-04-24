/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.Date;

/**
 * This class is a container for the full information of a user
 * @author rdinarte
 */
public class User extends UserBase
{

    // Attributes
    // -------------------------------------------------------------------------
    private String password;
    private boolean enabled;
    private Date creationDate;

    // Constructor
    // -------------------------------------------------------------------------
    public User(int userId, String userName, String name, String lastName1, String lastName2,
                    String type, String password, boolean enabled, Date creationDate)
    {
        super(userId, userName, name, lastName1, lastName2, type);
        this.password = password;
        this.enabled = enabled;
        this.creationDate = creationDate;
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

    /**
     * Get the date when the user was created
     * @return the date when the user was created
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * A value indicating if the user is active
     * @return a value indicating if the user is active
     */
    public boolean isEnabled() {
        return enabled;
    }

}