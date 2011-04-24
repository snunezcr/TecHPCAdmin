<%-- 
    Document   : my-experiments
    Created on : Mar 26, 2011, 8:55:36 PM
    Author     : rdinarte
--%>

<%@page import="common.ServiceResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="model.Experiment"%>
<%@page import="model.ExperimentExecution"%>
<%@page import="controller.Constants"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;
    String errorMessage = "";
    String experimentHtml = "";

       HpcaServiceAgent agent = new HpcaServiceAgent();
    ServiceResult<HashMap<Integer,Experiment>> result = agent.GetExperiments(request);
    if(result.getStatus() == ServiceResult.OperationResult.Error)
        errorMessage = "Error al cargar experimentos. ";
    else
    {
        HashMap<Integer, Experiment> hash = result.getValue();
        Iterator<Experiment> ite = hash.values().iterator();
        while(ite.hasNext())
        {
            Experiment exp = ite.next();
            experimentHtml += "<tr>" +
                    "<td>" + exp.getName() + "</td>" +
                    "<td>" + exp.getDescription() + "</td>" +
                    "<td>" + exp.getExecutablePath() + "</td>" +
                    "<td align='center'><a href='Resultados.rar'>Descargar resultados</a></td>" +
                    "<td align='center'><a href='ExperimentStatistics.htm'>Ver estad&iacute;sticas</a></td>" +
                    "<td align='center'><a href='" + Constants.ExperimentDetailsPage + "?" +
                    Constants.ExperimentId + "=" + Integer.toString(exp.getId()) +
                    "'>Ver detalles</a></td></tr>";
        }
    }

    //Let's check if there was an error while creating a new experiment
    errorMessage += RequestManager.GetError(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title></title>
    <link href="../styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
</head>
<body>
    <label class="error"><%= errorMessage %></label>
    <div class="title">Mis experimentos</div>
    <table width="100%" class="table" border="1">
        <tr class="tableHeader">
            <td>Nombre</td>
            <td>Descripción</td>
            <td>Ejecutable</td>
            <td colspan="2">Última ejecución</td>
            <td>Ver detalles</td>
        </tr>
        <%= experimentHtml %>
    </table>
</body>
</html>
