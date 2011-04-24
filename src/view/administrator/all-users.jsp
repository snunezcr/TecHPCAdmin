<%-- 
    Document   : all-users
    Created on : Apr 23, 2011, 10:35:11 PM
    Author     : rdinarte
--%>

<%@page import="java.util.Collection"%>
<%@page import="model.User"%>
<%@page import="java.util.HashMap"%>
<%@page import="common.ServiceResult"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(!RequestManager.VerifyAdminRights(request, response)) return;
    String errorMessage = "";
    String usersHtml = "";

    HpcaServiceAgent agent = new HpcaServiceAgent();
    ServiceResult<HashMap<Integer, User>> result = agent.GetAllUsers();
    if(result.getStatus() == ServiceResult.OperationResult.Error)
        errorMessage = "Error al cargar los usuarios. ";
    else
    {
        Collection<User> users = result.getValue().values();
        for(User user : users)
        {
            usersHtml += "<tr>" +
                    "<td>" + user.getFullName() + "</td>" +
                    "<td>" + user.getUserName() + "</td>" +
                    "<td>" + user.getType() + "</td>" +
                    "<td>" + user.getCreationDate() + "</td>" +
                    "<td>" + (user.isEnabled() ? "Si" : "No") + "</td>" +
                    "</tr>";
        }
    }

    //Let's check if there was an error while uploading a program
    errorMessage += RequestManager.GetError(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
        <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
    </head>
    <body>
        <label class="error"><%= errorMessage %></label>
        <div class="title">Usuarios del sistema</div>
        <table width="100%" class="table" border="1">
            <tr class="tableHeader">
                <td>Nombre completo</td>
                <td>Login</td>
                <td>Rol</td>
                <td>Fecha de creación</td>
                <td>Habilitado</td>
            </tr>
            <%= usersHtml %>
        </table>
    </body>
</html>
