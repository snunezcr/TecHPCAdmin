/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 * This class is to map an experiment to a user.
 * @author cfernandez
 */
public class UserExperimentMapping
{
    // Attributes
    // -------------------------------------------------------------------------
    private int expId;
    private int userId;

    // Constructor
    // -------------------------------------------------------------------------
    public UserExperimentMapping(int expId, int userId) {
        this.expId = expId;
        this.userId = userId;
    }

    public int getExpId() {
        return expId;
    }

    public int getUserId() {
        return userId;
    }

}
