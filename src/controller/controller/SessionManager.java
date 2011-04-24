/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import common.ServiceResult;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.Application;
import model.Experiment;
import model.UserBase;

/**
 * Manages all the information stored in the session object
 * @author rdinarte
 */
public class SessionManager {

    private static final String UserId = "UserId";
    private static final String UserRole = "UserRole";
    private static final String ExperimentHash = "ExperimentHash";
    private static final String ApplicationsHash = "ApplicationsHash";
    private static final String ParamTypes = "ParameterTypes";

    public static void Login(final HttpServletRequest request, final UserBase user)
    {
        HttpSession session = request.getSession();
        session.setAttribute(UserId, user.getUserId());
        session.setAttribute(UserRole, user.getType());
    }

    public static boolean IsLoggedIn(final HttpServletRequest request)
    {
        return request.getSession().getAttribute(UserId) != null;
    }

    public static Integer GetUserId(final HttpServletRequest request)
    {
        return (Integer)request.getSession().getAttribute(UserId);
    }

    public static String GetUserRole(final HttpServletRequest request)
    {
        return (String)request.getSession().getAttribute(UserRole);
    }

    public static ServiceResult<HashMap<Integer, Application>> GetApplications
            (final HttpServletRequest request)
    {
        return (ServiceResult<HashMap<Integer, Application>>)request.getSession().
                getAttribute(ApplicationsHash);
    }

    public static void SetApplications(final HttpServletRequest request,
            final ServiceResult<HashMap<Integer, Application>> applications)
    {
        request.getSession().setAttribute(ApplicationsHash, applications);
    }

    public static ServiceResult<HashMap<Integer,Experiment>> GetExperiments
            (final HttpServletRequest request)
    {
        return (ServiceResult<HashMap<Integer,Experiment>>)request.getSession().
                getAttribute(ExperimentHash);
    }

    public static void SetExperiments(final HttpServletRequest request,
            final ServiceResult<HashMap<Integer,Experiment>> experiments)
    {
        request.getSession().setAttribute(ExperimentHash, experiments);
    }

    public static void SetParameterTypes(final HttpServletRequest request,
            final ServiceResult<String[]> types)
    {
        request.getSession().setAttribute(ParamTypes, types);
    }

    public static ServiceResult<String[]> GetParameterTypes(final HttpServletRequest request)
    {
        return (ServiceResult<String[]>)request.getSession().getAttribute(ParamTypes);
    }

}
