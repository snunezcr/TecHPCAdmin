<%-- 
    Document   : download-log
    Created on : May 7, 2011, 11:40:48 PM
    Author     : rdinarte
--%>

<%@page import="controller.Constants"%>
<%@page import="controller.RequestManager"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    if(!RequestManager.VerifyLogin(request, response)) return;
    int experimentId = -1;
    String expIdParam = request.getParameter(Constants.ExperimentId);
    String fileName = request.getParameter("file");
    try
    {
        experimentId = Integer.parseInt(expIdParam);
    }
    catch(Exception ex)
    {
        return;
    }

    HpcaServiceAgent agent = new HpcaServiceAgent();
    String basePath = agent.GetExperimentsOutputPath(request, experimentId);

    RequestManager.DownloadFile(response, basePath + fileName, fileName);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
    </body>
</html>
