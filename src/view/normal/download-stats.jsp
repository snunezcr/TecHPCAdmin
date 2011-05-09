<%-- 
    Document   : download-stats
    Created on : May 8, 2011, 1:21:13 PM
    Author     : rdinarte
--%>

<%@page import="common.CommonFunctions"%>
<%@page import="model.NodeStatistics"%>
<%@page import="model.ExperimentExecution"%>
<%@page import="common.ServiceResult"%>
<%@page import="model.Experiment"%>
<%@page import="java.util.HashMap"%>
<%@page import="controller.Constants"%>
<%@page import="controller.RequestManager"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    if(!RequestManager.VerifyLogin(request, response)) return;
    int experimentId = -1;
    String expIdParam = request.getParameter(Constants.ExperimentId);
    int statsId = -1;
    String statsIdParam = request.getParameter("exec");
    try
    {
        experimentId = Integer.parseInt(expIdParam);
        statsId = Integer.parseInt(statsIdParam);
    }
    catch(Exception ex)
    {
        return;
    }

    HpcaServiceAgent agent = new HpcaServiceAgent();
    ServiceResult<HashMap<Integer, Experiment>> service = agent.GetExperiments(request);
    if(service.getStatus() != ServiceResult.OperationResult.Succeeded)
        return;

    HashMap<Integer, Experiment> experiments = service.getValue();
    if(!experiments.containsKey(experimentId))
        return;

    Experiment experiment = experiments.get(experimentId);
    ExperimentExecution execution = null;
    for(ExperimentExecution executionTemp : experiment.getExecutionHistory())
        if(executionTemp.getExecutionId() == statsId)
        {
            execution = executionTemp;
            break;
        }

    if(execution == null)
        return;

    ServiceResult<NodeStatistics[]> nodeStats = agent.GetNodeStats(statsId);
    if(nodeStats.getStatus() != ServiceResult.OperationResult.Succeeded)
        return;

    NodeStatistics[] nodes = nodeStats.getValue();
    RequestManager.DownloadStatistics(response, experiment, execution, nodes, "Statistics");
    
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
