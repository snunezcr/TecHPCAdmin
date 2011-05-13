/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package files.io;

import common.CommonFunctions;
import files.DirectoryManager;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class does all the related functions to creating, writing and reading from files
 * @author rdinarte
 */
public class FileIOManager {

    // Attributes
    // -------------------------------------------------------------------------
    private static FileIOManager instance = new FileIOManager();

    // Constructor
    // -------------------------------------------------------------------------
    private FileIOManager()
    {
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static FileIOManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Uploads a file to the specified path
     * @param request The page request object
     * @param formName The name of the form from which was invoked this method
     */
    public boolean CreateNewFile(final byte[] content,
            final String path, final String fileName, final boolean replace)
    {
        String fullName = path + fileName;
        File file = new File(fullName);
        if(replace || !file.exists())
        {
            try
            {
                FileOutputStream fileStream = new FileOutputStream(fullName);
                DataOutputStream stream = new DataOutputStream(fileStream);
                stream.write(content, 0, content.length);
                stream.flush();
                stream.close();
            }
            catch(Exception ex)
            {
                return false;
            }
            return true;
        }
        else
            return false;
    }

    public boolean CopyFile(String origin, String target)
    {
        try
        {
            String targetFolder = target;
            if(target.contains("/"))
                targetFolder = target.substring(0, target.lastIndexOf("/") + 1);
            DirectoryManager.GetInstance().CreateDirectory(targetFolder);

            File originFile = new File(origin);
            File targetFile = new File(target);

            InputStream in = new FileInputStream(originFile);
            OutputStream out = new FileOutputStream(targetFile);

            int length = CommonFunctions.LongToMaxInt(originFile.length());
            byte[] buffer = new byte[length];

            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);

            in.close();
            out.close();

            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

    public void RemoveDirectory(File file)
    {
        try
        {
            if (file.exists() && file.isDirectory())
            {
                String[] children = file.list();
                for (String child : children)
                    RemoveDirectory(new File(file, child));
            }
            file.delete();
        }
        catch(Exception ex) { }
    }

}