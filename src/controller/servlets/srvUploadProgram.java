/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import common.ServiceResult;
import controller.HpcaServiceAgent;
import controller.MultipartRequest;
import controller.RequestManager;
import controller.SessionManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ApplicationBase;

/**
 *
 * @author rdinarte
 */
public class srvUploadProgram extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        //We need to use the MultipartRequest because there's an input of type file
        MultipartRequest mrequest = new MultipartRequest(request, getServletContext());
        String description = mrequest.getParameter("txtDescription");
        String folder = mrequest.getParameter("txtFolder");
        String inputFile = mrequest.getParameter("dlgInputFile");
        byte[] fileContent = mrequest.getFile("dlgInputFile");
        ApplicationBase application = new ApplicationBase(description, folder + "/" + inputFile);

        //Now let's upload the program
        HpcaServiceAgent agent = new HpcaServiceAgent();
        ServiceResult<Integer> result = agent.CreateProgram(request, application, fileContent);

        if(result.getStatus() == ServiceResult.OperationResult.Succeeded)
        {//The program was uploaded without errors
            //Let's empty the cache
            SessionManager.SetApplications(request, null);
            response.sendRedirect(RequestManager.MyProgramsFullPage);
        }
        else //An error occured, inform the user
            RequestManager.SendProgramCreationError(request, response);
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
