/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public static final String MyProgramsPage = "/Hpca/normal/my-programs.jsp";

    public static final String AllUsersPage = "all-users.jsp";
    private static final String AllUsersFullPage = "/administrator/all-users.jsp";

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
            case LoginExists: return loginExistsError;
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
        sendError(request, response, AllUsersFullPage, ErrorCodes.UserCreationError);
    }

    public static void SendLoginExistsError(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException
    {
        sendError(request, response, AllUsersFullPage, ErrorCodes.LoginExists);
    }

    private static void sendError(final HttpServletRequest request,
            final HttpServletResponse response, final String path, final ErrorCodes errorCode)
            throws ServletException, IOException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        request.setAttribute(errorParam, errorCode);
        dispatcher.forward(request, response);
    }

}
