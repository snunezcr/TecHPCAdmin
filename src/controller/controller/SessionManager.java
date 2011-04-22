/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import common.ServiceResult;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.Experiment;

/**
 * Manages all the information stored in the session object
 * @author rdinarte
 */
public class SessionManager {

    private static final String UserId = "UserId";
    private static final String ExperimentHash = "ExperimentHash";
    private static final String ParamTypes = "ParameterTypes";

    public static void Login(final HttpServletRequest request, final int userId)
    {
        HttpSession session = request.getSession();
        session.setAttribute(UserId, userId);
    }

    public static boolean IsLoggedIn(final HttpServletRequest request)
    {
        return request.getSession().getAttribute(UserId) != null;
    }

    public static Integer GetUserId(final HttpServletRequest request)
    {
        return (Integer)request.getSession().getAttribute(UserId);
    }

    public static ServiceResult<HashMap<Integer,Experiment>> GetExperiments
            (final HttpServletRequest request)
    {
        return (ServiceResult<HashMap<Integer,Experiment>>)request.getSession().
                getAttribute(ExperimentHash);
    }

    public static void SetExperiments
            (final HttpServletRequest request,
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
