/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.Date;

/**
 * This class is a container for a user's application metadata
 * @author rdinarte
 */
public class Application extends ApplicationBase {

    // Attributes
    // -------------------------------------------------------------------------
    private int id;
    private Date updateDate;

    // Constructor
    // -------------------------------------------------------------------------
    public Application(final int id, final String description, final String relativePath,
                       final Date updateDate)
    {
        super(description, relativePath);
        this.id = id;
        this.updateDate = updateDate;
    }

    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Gets the application id
     * @return The application id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the application last update date
     * @return The application update date
     */
    public Date getUpdateDate() {
        return updateDate;
    }

}
