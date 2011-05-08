/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package files;

import common.CommonFunctions;
import common.ServiceResult;
import config.ConfigurationManager;
import java.io.File;
import java.io.IOException;
import model.UserBase;

/**
 *
 * @author rdinarte
 */
public class DirectoryManager {
    // Attributes
    // -------------------------------------------------------------------------
    private static DirectoryManager instance = new DirectoryManager();

    // Constructor
    // -------------------------------------------------------------------------
    private DirectoryManager()
    {
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static DirectoryManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    public String GetPathForUser(final int userId)
    {
        return ConfigurationManager.GetInstance().GetDataBasePath()
                + Integer.toString(userId) + "/";
    }

    public String GetPathForExperiment(final int userId, final int experimentId)
    {
        return GetPathForUser(userId) + "experiments/" + Integer.toString(experimentId) + "/";
    }

    public String GetPathForExperimentExecution(final int userId, final int experimentId)
    {
        return GetPathForUser(userId) + "experiments/" + Integer.toString(experimentId) +
                "/bin/";
    }

    public String GetPathForExperimentStats(final int userId, final int experimentId)
    {
        return GetPathForUser(userId) + "experiments/" + Integer.toString(experimentId) +
                "/stats/";
    }

    public String GetPathForExperimentOutput(final int userId, final int experimentId)
    {
        return GetPathForUser(userId) + "experiments/" + Integer.toString(experimentId) +
                "/log/";
    }

    public String GetApplicationsPath(final int userId)
    {
        return GetPathForUser(userId) + "bin/";
    }

    public ServiceResult<Boolean> CreateExperimentsStructure(final int userId,
                                                             final int experimentId)
    {
        try
        {
            String base = GetPathForExperiment(userId, experimentId);
            boolean success = CreateDirectory(base + "bin")
                              && CreateDirectory(base + "log")
                              && CreateDirectory(base + "stats");
            return new ServiceResult<Boolean>(success);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Creates the data structure for a user
     * @param user The user's information
     * @return True if the structure could be created, false otherwise
     */
    public ServiceResult<Boolean> CreateUserStructure(final UserBase user)
    {
        try
        {
            String base = GetPathForUser(user.getUserId());
            boolean success = CreateDirectory(base + "bin")
                              && CreateDirectory(base + "data")
                              && CreateDirectory(base + "experiments");
            return new ServiceResult<Boolean>(success);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    public boolean CreateDirectory(String path)
    {
        return new File(path).mkdirs();
    }

    public boolean DeleteFile(final String fileName)
    {
        try
        {
            String compressCommand = "rm -r " + fileName;
            Runtime.getRuntime().exec(compressCommand);
            return true;
        }
        catch(IOException ex)
        {
            return false;
        }
    }

    public boolean CreateCompressedDir(final String fileName, final String sourcePath)
    {
        try
        {
            String compressCommand = "tar -zcvf " + sourcePath + fileName + ".tar.gz -C " +
                    sourcePath + " " + fileName;
            Runtime.getRuntime().exec(compressCommand);
            return true;
        }
        catch(IOException ex)
        {
            return false;
        }
    }

}
