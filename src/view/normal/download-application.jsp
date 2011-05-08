<%-- 
    Document   : download-application
    Created on : Apr 23, 2011, 12:56:57 PM
    Author     : rdinarte
--%>

<%@page import="controller.RequestManager"%>
<%@page import="model.Application"%>
<%@page import="java.util.HashMap"%>
<%@page import="common.ServiceResult"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    if(!RequestManager.VerifyLogin(request, response)) return;
    
    HpcaServiceAgent agent = new HpcaServiceAgent();
    String basePath = agent.GetApplicationsPath(request);
    ServiceResult<HashMap<Integer, Application>> result = agent.GetUsersApplications(request);
    HashMap<Integer, Application> applications = result.getValue();

    int appId = -1;
    try
    {
        appId = Integer.parseInt(request.getParameter("id").toString());
    }
    catch(Exception ex){ }

    if(appId > 0 && result.getStatus() != ServiceResult.OperationResult.Error
            && applications.containsKey(appId))
    {
        String fileName = applications.get(appId).getRelativePath();
        basePath += fileName;
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        RequestManager.DownloadFile(response, basePath, fileName);
    }
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
