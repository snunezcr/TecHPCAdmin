/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import common.CommonFunctions;
import common.ServiceResult;
import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ExperimentBase;
import model.ExperimentParameter;
import controller.HpcaServiceAgent;
import controller.MultipartRequest;
import controller.RequestManager;
import controller.SessionManager;

/**
 * This is the controller for the page where new experiments are created
 * @author rdinarte
 */
public class srvNewExperiment extends HttpServlet {

    private final String MyAddress = "normal/new-experiment.jsp";
    private final String IsBeingFilled = "isFilling";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        //We need to use the MultipartRequest because there's an input of type file
        MultipartRequest mrequest = new MultipartRequest(request, getServletContext());
        String action = mrequest.getParameter("hdAction");
        if(action.equalsIgnoreCase("add"))
            add(request, response, mrequest);
        else if (action.equalsIgnoreCase("delete"))
            delete(request, response, mrequest);
        else
            save(request, response, mrequest);
    }

    private void save(final HttpServletRequest request, final HttpServletResponse response,
            final MultipartRequest mrequest) throws IOException, ServletException
    {
        //Since this form needs to upload a file, it uses enctype="multipart/form-data", so we need
        //a special way to parse the request
        HpcaServiceAgent agent = new HpcaServiceAgent();
        //Extract the information from the webform
        String name = mrequest.getParameter("txtName");
        String description = mrequest.getParameter("txtDescription");
        String application = mrequest.getParameter("cmbApplication");
        String inputLine = mrequest.getParameter("txtInputParams");
        String inputFile = mrequest.getParameter("dlgInputFile");
        byte[] fileContent = mrequest.getFile("dlgInputFile");
        String parallelText = mrequest.getParameter("chkParallel");
        Boolean parallelExecution = CommonFunctions.GetValue(parallelText);
        //We don't have the experiment id yet
        ExperimentBase experiment = new ExperimentBase(-1, name, description, application,
                                                       inputLine, inputFile);
        //Check if the user selected to execute the experiment using multiple processors
        if(parallelExecution)
        {
            int numOfProcessors = Integer.parseInt(mrequest.getParameter("cmbProcessors"));
            String saveLogText = mrequest.getParameter("chkNodeLog");
            Boolean saveEachNodeLog = CommonFunctions.GetValue(saveLogText);
            String sharedWorkingDir = mrequest.getParameter("txtSharedDir");
            String middleware = mrequest.getParameter("txtMiddleware");
            experiment.AddParallelConfiguration(numOfProcessors, middleware, sharedWorkingDir,
                                                saveEachNodeLog);
        }

        LinkedList<ExperimentParameter> params = getParamsFromRequest(mrequest);
        for(ExperimentParameter param : params)
            experiment.AddParameter(param);
        
        ServiceResult<Integer> result = agent.CreateExperiment(request, experiment, fileContent);

        if(result.getStatus() == ServiceResult.OperationResult.Succeeded)
        {//The experiment was created without errors
            SessionManager.SetExperiments(request, null);//Let's empty the cache
            response.sendRedirect(RequestManager.MyExperimentsPage);
        }
        else //An error occured, inform the user
            RequestManager.SendExperimentCreationError(request, response);
    }

    private void delete(final HttpServletRequest request, final HttpServletResponse response,
            final MultipartRequest mrequest) throws IOException, ServletException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher(MyAddress);
        LinkedList<ExperimentParameter> params = getParamsFromRequest(mrequest);
        String indexTxt = mrequest.getParameter("hdDeleteRow");
        params.remove(Integer.parseInt(indexTxt));
        request.setAttribute("expParams", params.toArray(new ExperimentParameter[params.size()]));
        //We need to indicate that this experiment is in the middle of the process of being filled
        request.setAttribute(IsBeingFilled, true);
        //Let's copy the request values so we don't lose any information
        prepareRequest(request, mrequest);
        dispatcher.forward(request, response);

    }

    private void add(final HttpServletRequest request, final HttpServletResponse response,
            final MultipartRequest mrequest) throws IOException, ServletException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher(MyAddress);
        //We need to get the recently included parameter
        String name = mrequest.getParameter("txtNewParam");
        String type = mrequest.getParameter("cmbNewType");
        String value = mrequest.getParameter("txtNewValue");
        ExperimentParameter newParam = new ExperimentParameter(name, type, value);
        //Now let's include the new param in the request
        LinkedList<ExperimentParameter> params = getParamsFromRequest(mrequest);
        params.add(newParam);
        request.setAttribute("expParams", params.toArray(new ExperimentParameter[params.size()]));
        
        //We need to indicate that this experiment is in the middle of the process of being filled
        request.setAttribute(IsBeingFilled, true);
        //Let's copy the request values so we don't lose any information
        prepareRequest(request, mrequest);
        dispatcher.forward(request, response);
    }

    private LinkedList<ExperimentParameter> getParamsFromRequest(final MultipartRequest mrequest)
    {
        LinkedList<ExperimentParameter> result = new LinkedList<ExperimentParameter>();
        String params[] = mrequest.getParameter("hdParams").split("!_@_!");
        for(int paramNumber = 0; paramNumber < params.length - 2; paramNumber += 3)
        {
            String name = params[paramNumber];
            String type = params[paramNumber + 1];
            String value = params[paramNumber + 2];
            ExperimentParameter param = new ExperimentParameter(name, type, value);
            result.add(param);
        }
        return result;
    }

    private void prepareRequest(final HttpServletRequest request, final MultipartRequest mrequest)
    {
        prepareRequest(request, mrequest, "txtName");
        prepareRequest(request, mrequest, "txtDescription");
        prepareRequest(request, mrequest, "cmbApplication");
        prepareRequest(request, mrequest, "chkParallel");
        prepareRequest(request, mrequest, "cmbProcessors");
        prepareRequest(request, mrequest, "chkNodeLog");
        prepareRequest(request, mrequest, "txtSharedDir");
        prepareRequest(request, mrequest, "txtMiddleware");
        prepareRequest(request, mrequest, "txtInputParams");
        prepareRequest(request, mrequest, "dlgInputFile");
    }

    private void prepareRequest(final HttpServletRequest request, final MultipartRequest mrequest,
            String property)
    {
        String value = mrequest.getParameter(property);
        //We don't want null values in the jsp
        request.setAttribute(property, value != null ? value : "");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
