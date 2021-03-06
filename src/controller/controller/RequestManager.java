/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import common.CommonFunctions;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Experiment;
import model.ExperimentExecution;
import model.NodeStatistics;
import model.UserBase;

/**
 *
 * @author rdinarte
 */
public class RequestManager {

    // Error codes
    // -------------------------------------------------------------------------
    private enum ErrorCodes
    {
        InvalidCredentials,
        LoginServerError,
        ExperimentCreationError,
        ProgramCreationError,
        UserCreationError,
        UserEditionError,
        LoginExists
    }

    // Page names
    // -------------------------------------------------------------------------
    /**
     * The name of the login page
     */
    private static final String LoginPage = "/login.jsp";
    private static final String FullLoginPage = "/Hpca/login.jsp";
    /**
     * The name of the main menu page
     */
    private static final String MainMenuPage = "/Hpca/main.jsp";

    public static final String MyExperimentsPage = "/Hpca/normal/my-experiments.jsp";

    public static final String MyProgramsFullPage = "/Hpca/normal/my-programs.jsp";
    private static final String MyProgramsPage = "/normal/my-programs.jsp";

    public static final String AllUsersFullPage = "/Hpca/administrator/all-users.jsp";
    private static final String AllUsersPage = "/administrator/all-users.jsp";

    // Request parameters
    // -------------------------------------------------------------------------
    /**
     * The name of the request parameter in which errors will be reported
     */
    private static final String errorParam = "error";

    // Error messages
    // -------------------------------------------------------------------------
    /**
     * Error message reported to the user when he tries to login with invalid
     * credentials
     */
    private static final String invalidCredentialsMessage =
            "Nombre de usuario y/o contraseña incorrectos.";
    /**
     * Error message reported to the user when an unexpected error occurs while
     * trying to login
     */
    private static final String loginServerErrorMessage = "Se produjo un error al intentar "
            + "conectarse al sistema.<br />Por favor contacte al administrador del sitio.";

    private static final String experimentCreationError = "Se produjo un error al intentar "
            + "crear el experimento.<br />Por favor contacte al administrador del sitio.";

    private static final String userCreationError = "Se produjo un error al intentar "
            + "crear el usuario.<br />Por favor contacte al administrador del sitio.";

    private static final String userEditionError = "Se produjo un error al intentar  modificar los "
            + "datos del usuario.<br />Por favor contacte al administrador del sitio.";

    private static final String programUploadError = "Se produjo un error al intentar "
            + "subir el programa.";

    private static final String loginExistsError = "No se pudo crear el usuario. El login ya está "
            + "en uso";

    // Class methods
    // -------------------------------------------------------------------------
    public static void Login(final HttpServletRequest request,
            final HttpServletResponse response, final UserBase userData) throws IOException
    {
        SessionManager.Login(request, userData);
        response.sendRedirect(MainMenuPage);
    }

    public static boolean VerifyLogin(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException
    {
        boolean result = SessionManager.IsLoggedIn(request);
        if(!result)
            response.sendRedirect(FullLoginPage);
        return result;
    }

    public static boolean HasAdminRights(final HttpServletRequest request) throws IOException
    {
        return SessionManager.IsLoggedIn(request) && SessionManager.IsAdministrator(request);
    }

    public static boolean VerifyAdminRights(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException
    {
        boolean result = HasAdminRights(request);
        if(!result)
            response.sendRedirect(FullLoginPage);
        return result;
    }

    public static String GetError(final HttpServletRequest request)
    {
        Object requestError = request.getAttribute(errorParam);
        if(requestError == null)
            return "";
        ErrorCodes code = (ErrorCodes)requestError;
        switch (code)
        {
            case InvalidCredentials: return invalidCredentialsMessage;
            case LoginServerError: return loginServerErrorMessage;
            case ExperimentCreationError: return experimentCreationError;
            case UserCreationError: return userCreationError;
            case UserEditionError: return userEditionError;
            case LoginExists: return loginExistsError;
            case ProgramCreationError: return programUploadError;
            default: return "";
        }
    }

    public static void SendExperimentCreationError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, MyExperimentsPage, ErrorCodes.ExperimentCreationError);
    }

    public static void SendProgramCreationError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, MyProgramsPage, ErrorCodes.ProgramCreationError);
    }

    public static void SendInvalidCredentialsError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, LoginPage, ErrorCodes.InvalidCredentials);
    }

    public static void SendLoginServerError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, LoginPage, ErrorCodes.LoginServerError);
    }

    public static void SendUserCreationError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, AllUsersPage, ErrorCodes.UserCreationError);
    }

    public static void SendUserEditionError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, AllUsersPage, ErrorCodes.UserEditionError);
    }

    public static void SendLoginExistsError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, AllUsersPage, ErrorCodes.LoginExists);
    }

    private static void sendError(final HttpServletRequest request,
            final HttpServletResponse response, final String path, final ErrorCodes errorCode)
            throws ServletException, IOException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        request.setAttribute(errorParam, errorCode);
        dispatcher.forward(request, response);
    }

    public static void DownloadFile(final HttpServletResponse response, final String path,
                                    final String fileName)
    {
        BufferedInputStream buffer = null;
        ServletOutputStream pageOutput = null;

        try
        {
            pageOutput = response.getOutputStream();
            File downloadFile = new File(path);

            //set response headers
            response.setContentType("text/plain");

            response.addHeader("Content-Disposition","attachment; filename=" + fileName);

            response.setContentLength((int)downloadFile.length());

            FileInputStream input = new FileInputStream(downloadFile);
            buffer = new BufferedInputStream(input);
            int readBytes = 0;

            //read from the file; write to the ServletOutputStream
            while((readBytes = buffer.read( )) != -1)
                pageOutput.write(readBytes);

        }
        catch (Exception ex){ }
        finally
        {
            try
            {
                if (pageOutput != null)
                    pageOutput.close();
                if (buffer != null)
                    buffer.close();
            }
            catch(Exception ex) { }
        }
    }

    public static void DownloadStatistics(final HttpServletResponse response,
            final Experiment experiment, final ExperimentExecution execution,
            final NodeStatistics[] nodes, final String fileName)
    {

        ServletOutputStream pageOutput = null;

        try
        {
            pageOutput = response.getOutputStream();

            //set response headers
            response.setContentType("text/plain");

            response.addHeader("Content-Disposition","attachment; filename=Statistics");

            int length = 0;
            String tempString = "Estadisticas para ejecución del experimento "
                    + experiment.getName();
            length += addContent(pageOutput, tempString);

            length += addContent(pageOutput, "");

            tempString = "Fecha de inicio: "
                    + CommonFunctions.GetDateText(execution.getStartDate());
            length += addContent(pageOutput, tempString);

            tempString = "Fecha de fin: " + CommonFunctions.GetDateText(execution.getEndDate());
            length += addContent(pageOutput, tempString);

            tempString = "Tiempo total del CPU (en segundos): "
                    + Integer.toString(execution.getCPUTimeSeconds());
            length += addContent(pageOutput, tempString);

            tempString = "Memoria utilizada : " + Float.toString(execution.getUsedMemory()) + "%";
            length += addContent(pageOutput, tempString);

            tempString = "Uso del CPU : " + Float.toString(execution.getCPUUsage()) + "%";
            length += addContent(pageOutput, tempString);

            if(nodes.length > 0)
            {
                length += addContent(pageOutput, "");
                length += addContent(pageOutput, "");
                tempString = "Estadisticas paras los nodos:";
                length += addContent(pageOutput, tempString);
                for(NodeStatistics node : nodes)
                {
                    length += addContent(pageOutput, "");

                    tempString = "Nodo " + Integer.toString(node.getNodeNumber());
                    length += addContent(pageOutput, tempString);

                    tempString = "Tiempo total del CPU (en segundos): "
                            + Integer.toString(node.getTotalTime());
                    length += addContent(pageOutput, tempString);

                    tempString = "Memoria utilizada : "
                            + Float.toString(node.getUsedMemory()) + "%";
                    length += addContent(pageOutput, tempString);

                    tempString = "Uso del CPU : " 
                            + Float.toString(node.getCpuUsage()) + "%";
                    length += addContent(pageOutput, tempString);
                }
            }
            response.setContentLength(length);
        }
        catch (Exception ex){ }
        finally
        {
            try
            {
                if (pageOutput != null)
                    pageOutput.close();
            }
            catch(Exception ex) { }
        }
    }

    private static int addContent(final ServletOutputStream pageOutput, String text) throws Exception
    {
        pageOutput.println(text);
        return text.length() + 2;
    }

}
