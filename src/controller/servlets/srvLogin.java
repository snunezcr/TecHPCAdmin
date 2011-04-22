/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import common.ServiceResult;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import controller.HpcaServiceAgent;
import controller.RequestManager;

/**
 * This is the controller for the login web page
 * @author rdinarte
 */
public class srvLogin extends HttpServlet {
   
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
        HpcaServiceAgent agent = new HpcaServiceAgent();
        //Extract the information from the webform
        String userName = request.getParameter("txtUserName");
        String password = request.getParameter("txtPassword");
        ServiceResult<Integer> result = agent.Login(userName, password);
        if(result.getStatus() == ServiceResult.OperationResult.Succeeded)
        {//The login process was completed without errors
            if(result.getValue() > 0)
                //Valid credentials
                RequestManager.Login(request, response, result.getValue());
            else
                RequestManager.SendInvalidCredentialsError(request, response);
        }
        else //An error occured, inform the user
            RequestManager.SendLoginServerError(request, response);
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
