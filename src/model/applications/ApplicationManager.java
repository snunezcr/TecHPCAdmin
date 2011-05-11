/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications;

import applications.db.ApplicationDataManager;
import common.CommonFunctions;
import common.LinuxUtilities;
import common.ServiceResult;
import files.DirectoryManager;
import files.io.FileIOManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import model.Application;
import model.ApplicationBase;

/**
 * This class does all the tasks related to applications, such as installing applications,
 * uploading or retrieving them
 * @see ApplicationDataManager
 * @author rdinarte
 */
public class ApplicationManager {

    // Attributes
    // -------------------------------------------------------------------------
    private ApplicationDataManager dataManager;
    private static ApplicationManager instance = new ApplicationManager();

    // Constructor
    // -------------------------------------------------------------------------
    private ApplicationManager()
    {
        dataManager = new ApplicationDataManager();
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static ApplicationManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Retrieves all the applications that were uploaded by a user
     * @param userId The id of the owner of the applications
     * @return A list with all the user applications
     */
    public ServiceResult<HashMap<Integer, Application>> GetUsersApplications(final int userId)
    {
        try
        {
            HashMap<Integer, Application> result = dataManager.GetUsersApplications(userId);
            return new ServiceResult<HashMap<Integer, Application>>(result);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Uploads a program an stores its metadata
     * @param application The application's metadata
     * @param ownerId The user that is uploading the application
     * @param inputFile The program that is being uploaded
     * @return A value indicating whether the operation was successful or not
     */
    public ServiceResult<Integer> CreateProgram(final ApplicationBase application,
                                                final int ownerId, final byte[] inputFile)
    {
        int applicationId = -1;
        try
        {
            //Let's check if the file is empty
            if(inputFile.length == 0)
                throw new Exception("El archivo estaba vacío.");

            //We're going to store the applications metadata in the db
            int resultId = dataManager.CreateProgram(application, ownerId);

            DirectoryManager dirManager = DirectoryManager.GetInstance();
            String path = dirManager.GetApplicationsPath(ownerId);
            FileIOManager ioManager = FileIOManager.GetInstance();
            String inputFileName = application.getRelativePath();
            String dir = inputFileName.substring(0, inputFileName.lastIndexOf('/'));

            //Now let's upload the file
            boolean result = DirectoryManager.GetInstance().CreateDirectory(path + dir);
            result &= ioManager.CreateNewFile(inputFile, path, inputFileName, true);
            if(!result)
                throw new Exception("No se pudo subir el ejecutable.");
            return new ServiceResult<Integer>(resultId);
        }
        catch(Exception ex)
        {
            if(applicationId != -1)
            {//If we didn't write to the db, then there's nothing to rollback
                //TODO: Eliminar el registro de la BD y el directorio
            }
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    /**
     * Installs a program from a compressed file or a script file
     * @param userId The id of the owner of the program
     * @param description The description of the installed program
     * @param folder The folder where the program will be stored
     * @param fileName The name of the installer file
     * @param fileContent The binary content of the installer file
     * @return True if the program could be installed, otherwise false
     */
    public ServiceResult<Boolean> InstallProgram(final int userId, final String description,
            final String executable, final String folder, final String fileName,
            final byte[] fileContent)
    {
        return
            InstallProgram(userId, description, executable, folder, false, fileName, fileContent,
            null, null);
    }

    /**
     * Installs a program from a version control repository
     * @param userId The id of the owner of the program
     * @param description The description of the installed program
     * @param folder The folder where the program will be stored
     * @param repository The repository type (csv, svn or github)
     * @param url The url of the server containing the code
     * @return True if the program could be installed, otherwise false
     */
    public ServiceResult<Boolean> InstallProgram(final int userId, final String description,
            final String executable, final String folder, final String repository, final String url)
    {
        return InstallProgram(userId, description, executable, folder, true, null, null, repository,
                url);
    }

    private ServiceResult<Boolean> InstallProgram(final int userId, final String description,
            final String executable, final String folder, final boolean fromRepository,
            final String fileName, final byte[] fileContent, final String repository,
            final String url)
    {
        try
        {
            //First let's create the target folders
            DirectoryManager dirManager = DirectoryManager.GetInstance();
            String applicationPath = dirManager.GetApplicationsPath(userId) + folder + "/";
            String codePath = dirManager.GetCodePath(userId) + folder + "/";
            dirManager.CreateDirectory(applicationPath);
            dirManager.CreateDirectory(codePath);

            if(fromRepository)
                installFromRepository(codePath, repository, url);
            else
                installFromCompressedFile(codePath, fileName, fileContent);

            executeInstallation(codePath, applicationPath);

            return new ServiceResult<Boolean>(true);
        }
        catch(Exception ex)
        {
            return CommonFunctions.CreateErrorServiceResult(ex);
        }
    }

    private void executeInstallation(final String path, final String applicationPath)
            throws Exception
    {
        String installerPath = path ;//+ "installer/";
        if(!new File(installerPath).exists())
            throw new Exception("No se pudo encontrar el folder de instalacion.");

        //We have to look for *.ac files
        boolean runConfigure = CommonFunctions.ListExtensionFiles(installerPath, ".ac").length > 0;
        Process process = null;
        if(runConfigure)
        {
            String configCommand = installerPath + "configure --bindir=" + applicationPath;
            process = Runtime.getRuntime().exec(configCommand);//Let's run configure
            process.waitFor();
        }
        //Let's run make
        String[] makefile = CommonFunctions.SearchFile(installerPath, "makefile");
        if(makefile.length > 0)
        {
            process = Runtime.getRuntime().exec("make -C " + installerPath);
            process.waitFor();
            process = Runtime.getRuntime().exec("make install -C " + installerPath);
            process.waitFor();
        }
        else
            throw new Exception("No se pudo encontrar el archivo de make.");
        if(!runConfigure)
        {//If there was no config files we have to copy all the executable files by ourselves
            String binPath = path + "bin/";
            if(!new File(binPath).exists())
                binPath = path;
            String[] executables = CommonFunctions.ListExecuableFiles(binPath);
            if(executables.length > 0)
                for(String file : executables)
                {
                    FileIOManager ioManager = FileIOManager.GetInstance();
                    String program = applicationPath + file;
                    ioManager.CopyFile(binPath + file, program);
                    LinuxUtilities.GetInstance().
                            SetFilePermissions(program, LinuxUtilities.Execution);
                }
        }
    }

    private void installFromRepository(final String path, final String repository,
            final String url) throws Exception
    {
        String command = "";
        if(repository.equals("gitHub"))
            command = "git clone " + url + " " + path;
        else
            command = "svn co " + url + " " + path;
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

    }

    private void installFromCompressedFile(final String path, final String fileName,
            final byte[] fileContent) throws IOException, InterruptedException
    {
        FileIOManager fileManager = FileIOManager.GetInstance();
        //We have to copy the file to work with it
        fileManager.CreateNewFile(fileContent, path, fileName, false);

        LinuxUtilities.GetInstance().UncompressTarFile(path + fileName, path);
    }

}