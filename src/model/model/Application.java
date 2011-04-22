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
public class Application {

    // Attributes
    // -------------------------------------------------------------------------
    private int id;
    private String description;
    private String relativePath;
    private Date updateDate;

    // Constructor
    // -------------------------------------------------------------------------
    public Application(final int id, final String description, final String relativePath,
                       final Date updateDate)
    {
        this.id = id;
        this.description = description;
        this.relativePath = relativePath;
        this.updateDate = updateDate;
    }

    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Gets the application description
     * @return The application description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the application relative path, it is relative to where all the user applications are
     * stored
     * @return The application relative path
     */
    public String getRelativePath() {
        return relativePath;
    }

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
