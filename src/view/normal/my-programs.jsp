<%-- 
    Document   : my-programs
    Created on : Apr 23, 2011, 11:43:57 AM
    Author     : rdinarte
--%>

<%@page import="java.util.Collection"%>
<%@page import="java.util.HashMap"%>
<%@page import="common.ServiceResult"%>
<%@page import="model.Application"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;
    String errorMessage = "";
    String applicationsHtml = "";

    HpcaServiceAgent agent = new HpcaServiceAgent();
    ServiceResult<HashMap<Integer, Application>> result = agent.GetUsersApplications(request);
    if(result.getStatus() == ServiceResult.OperationResult.Error)
        errorMessage = "Error al cargar las aplicaciones. ";
    else
    {
        Collection<Application> applications = result.getValue().values();
        for(Application app : applications)
        {
            applicationsHtml += "<tr>" +
                    "<td>" + app.getDescription() + "</td>" +
                    "<td>" + app.getRelativePath() + "</td>" +
                    "<td>" + app.getUpdateDate() + "</td>" +
                    "<td align='center'><a href='download-application.jsp?id=" +  app.getId()  + "'>Descargar</a></tr>";
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
        <link href="../styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
    </head>
    <body>
        <p><font color="red"><%= errorMessage %></font></p>
        <div class="title">Mis ejecutables</div>
        <table width="100%" class="table" border="1">
            <tr class="tableHeader">
                <td>Descripción</td>
                <td>Dirección</td>
                <td>Última actualización</td>
                <td>Descargar</td>
            </tr>
            <%= applicationsHtml %>
        </table>
    </body>
</html>
