/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.lang.reflect.Field;

/**
 * This class contains operating system depending functions, that cannot be done otherwise because
 * of the java framework limitations
 * @author rdinarte
 */
public class LinuxUtilities {
    // Attributes
    // -------------------------------------------------------------------------
    public static String Execution = "755";
    private static LinuxUtilities instance = new LinuxUtilities();

    // Constructor
    // -------------------------------------------------------------------------
    private LinuxUtilities()
    {
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static LinuxUtilities GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Sets permissions for a file
     * @param path The path of the file
     * @param permissions The permissions to be assigned
     * @return True if it could set the permissions, false otherwise
     */
    public ServiceResult<Boolean> SetFilePermissions(final String path, final String permissions)
    {
        try
        {
            String command = "chmod " + permissions + " " + path;
            Runtime.getRuntime().exec(command);
            return new ServiceResult<Boolean>(true);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    public int GetUnixProcessPid(Process process)
    {
        if(process.getClass().getName().equals("java.lang.UNIXProcess"))
        {
            //(java.lang.UNIXProcess)
            try
            {
                Field field = process.getClass().getDeclaredField("pid");
                field.setAccessible(true);
                int pid = field.getInt(process);
                return pid;
            }
            catch (Throwable ex) { }
        }
        return -1;
    }

}
