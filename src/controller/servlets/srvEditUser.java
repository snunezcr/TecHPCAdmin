/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import common.CommonFunctions;
import common.ServiceResult;
import controller.HpcaServiceAgent;
import controller.RequestManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserBase;

/**
 *
 * @author rdinarte
 */
public class srvEditUser extends HttpServlet {
   
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
        String name = request.getParameter("txtName");
        String lastName1 = request.getParameter("txtLastName1");
        String lastName2 = request.getParameter("txtLastName2");
        String userName = request.getParameter("hdUserName");
        String role = request.getParameter("cmbRole");
        boolean enabled = CommonFunctions.GetValue(request.getParameter("chkEnabled"));
        UserBase user = new UserBase(-1, userName, name, lastName1, lastName2, role);

        HpcaServiceAgent agent = new HpcaServiceAgent();
        ServiceResult<Boolean> result = agent.UpdateUserPersonalInfo(user, enabled);

        if(result.getStatus() == ServiceResult.OperationResult.Succeeded)
            response.sendRedirect(RequestManager.AllUsersFullPage);
        else //An unexpected error occured
            RequestManager.SendUserCreationError(request, response);
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
