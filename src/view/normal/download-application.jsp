<%-- 
    Document   : download-application
    Created on : Apr 23, 2011, 12:56:57 PM
    Author     : rdinarte
--%>

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="model.Application"%>
<%@page import="java.util.HashMap"%>
<%@page import="common.ServiceResult"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

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
        BufferedInputStream buffer = null;
        ServletOutputStream pageOutput = null;

        try
        {
            pageOutput = response.getOutputStream();
            String fileName = applications.get(appId).getRelativePath();
            File downloadFile = new File(basePath + fileName);

            //set response headers
            response.setContentType("text/plain");

            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            response.addHeader("Content-Disposition","attachment; filename=" + fileName);

            response.setContentLength((int)downloadFile.length());

            FileInputStream input = new FileInputStream(downloadFile);
            buffer = new BufferedInputStream(input);
            int readBytes = 0;

            //read from the file; write to the ServletOutputStream
            while((readBytes = buffer.read( )) != -1)
                pageOutput.write(readBytes);

        }
        catch (Exception ex){ }
        finally
        {
            if (pageOutput != null)
                pageOutput.close();
            if (buffer != null)
                buffer.close();
        }
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
        <h1>Hello World!</h1>
    </body>
</html>
