/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 * This class is a container for a user's application metadata, it contains only the information
 * needed to upload an application
 * @author rdinarte
 */
public class ApplicationBase {

    // Attributes
    // -------------------------------------------------------------------------
    private String description;
    private String relativePath;

    // Constructor
    // -------------------------------------------------------------------------
    public ApplicationBase(final String description, final String relativePath)
    {
        this.description = description;
        this.relativePath = relativePath;
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

}