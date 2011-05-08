<%-- 
    Document   : experiment-details
    Created on : Mar 27, 2011, 6:26:24 PM
    Author     : cfernandez
--%>

<%@page import="common.ServiceResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.Experiment"%>
<%@page import="model.ExperimentExecution"%>
<%@page import="model.ExperimentParameter"%>
<%@page import="model.FolderStructure"%>
<%@page import="controller.Constants"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;
    String errorMessage = "";
    String expName = "" ;
    String expDescription = "" ;
    String expExecutable = "" ;
    String expCreationDate = "" ;
    String expParameters = "" ;
    String expInputFile = "";
    String expStatus = "" ;
    String expSharedWorkDir = "" ;
    String expProcessors = "" ;
    String expLogForNode = "" ;
    String expMiddleware = "" ;
    String expParallelism = "" ;
    String expParameterHTML = "";
    String expInternalStructHTML = "";
    String expExecutionsHTML = "";
    boolean isRunning = false;

   errorMessage = (String) session.getAttribute(Constants.ExecutionError);
    if(errorMessage == null)
        errorMessage = "";

    HpcaServiceAgent agent = new HpcaServiceAgent();
    ServiceResult<HashMap<Integer,Experiment>> experiments = agent.GetExperiments(request);
    String expIdParam = request.getParameter(Constants.ExperimentId);
    boolean isNumber = true;
    int expId = 0;
    try
    {
        expId = Integer.parseInt(expIdParam);
    }
    catch(Exception ex)
    {
        isNumber = false;
    }
    if(expIdParam == null || experiments.getStatus() == ServiceResult.OperationResult.Error || !isNumber)
        errorMessage = "Error al cargar el experimento.";
    else
    {
        HashMap<Integer, Experiment> hash = experiments.getValue();
        Experiment expDetail = hash.get(expId);
        session.setAttribute(Constants.ExperimentId, expId);

        // We establish values for general experiments
        expName = expDetail.getName();
        expDescription = expDetail.getDescription();
        expExecutable = expDetail.getExecutablePath();
        expCreationDate = expDetail.getCreationDate().toString();
        expParameters = expDetail.getInputParametersLine();
        expInputFile = expDetail.getInputFilePath();
        if(expInputFile.isEmpty())
            expInputFile = "No tiene";
        expStatus = "";

        if(expDetail.getExecutionStatus() == Experiment.ExecStatus.Running)
        {
            expStatus = "Corriendo";
            isRunning = true;
        }
        else
        {
            expStatus = "Detenido";
            isRunning = false;
        }

        // We establish values for parallel experiments
        if(expDetail.usesParallelExecution())
        {
            expParallelism = "S&iacute";
            expProcessors = Integer.toString(expDetail.getNumberOfProcessors());
            expLogForNode = expDetail.willSaveEachNodeLog() ? "S&iacute" : "No";
            expMiddleware = expDetail.getMiddleware();
            expSharedWorkDir = expDetail.getSharedWorkingDirectory();
        }
        else
        {
            expParallelism = "No";
            expProcessors = "1";
            expLogForNode = "No aplica (Guardar&aacute; del &uacute;nico nodo)";
            expMiddleware = "No aplica";
            expSharedWorkDir = "No aplica";
        }
        // We display the parameters of the experiment
        ExperimentParameter[] parameters = expDetail.getParameters();
        for(int paramI = 0; paramI < parameters.length; paramI++)
        {
            ExperimentParameter param = parameters[paramI];
            String type = param.getType();
            expParameterHTML +=
                "<tr>"
                + "<td>" + param.getName() + "</td> "
                + "<td>" + type + "</td> "
                + "<td>" + param.getValue().toString() + "</td>"
                + "</tr>";
        }
        // We display the internal structure of the experiment
        expInternalStructHTML = expDetail.getInternalStructure().GetHTML();

        // We display the execution history
        ExperimentExecution[] executions = expDetail.getExecutionHistory();
        for(int execI = 0; execI < executions.length; execI++)
        {
            ExperimentExecution execution = executions[execI];
            expExecutionsHTML +=
                "<tr>"
                + "<td align='left'>"
                            + execution.getStartDate().toString()
                            + "</td> "
                + "<td align='left'>"
                            + execution.getEndDate().toString()
                            + "</td> "
                + "<td align='center'><a href='ExperimentStatistics.htm' "
                            + "target='_blank'>Ver</a></td>"
                + "<td align='center'><a href='Estadisticas.rar' "
                            + "target='_blank'>Descargar</a></td>"
                + "<td align='center'><a href='" + execution.getOutputPath() + "' "
                            + "target='_blank'>Descargar</a></td>"
                + "</tr>";
        }
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title></title>
        <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
    </head>
    <body>
        <div class="title"><%= expDescription %></div>
        <table style="width:100%"><tr><td align="center">
            <p><font color="red"><%= errorMessage %></font></p>
            <table class="table" width="70%">
                <tr>
                    <td class="key">Nombre:</td>
                    <td class="value"><%= expName %></td>
                </tr>
                <tr>
                    <td class="key">Ejecutable:</td>
                    <td class="value"><%= expExecutable %></td>
                </tr>
                <tr>
                    <td class="key">Estado:</td>
                    <td class="value"><%= expStatus %></td>
                </tr>
                <tr>
                    <td class="key">Ejecuci&oacute;n en paralelo:</td>
                    <td class="value"><%= expParallelism %></td>
                </tr>
                <tr>
                    <td class="key">Cantidad de procesadores:</td>
                    <td class="value"><%= expProcessors %></td>
                </tr>{
                <tr>
                    <td class="key">Guardar log de cada nodo:</td>
                    <td class="value"><%= expLogForNode %></td>
                </tr>
                <tr>
                    <td class="key">Directorio compartido de trabajo:</td>
                    <td class="value"><%= expSharedWorkDir %></td>
                </tr>
                <tr>
                    <td class="key">Middleware:</td>
                    <td class="value"><%= expMiddleware %></td>
                </tr>
                <tr>
                    <td class="key">Parámetros de entrada:</td>
                    <td class="value"><%= expParameters %></td>
                </tr>
                <tr>
                    <td class="key">Archivo de entrada:</td>
                    <td class="value"><a href="Entrada.rar"><%= expInputFile %></a></td>
                </tr>
                <tr>
                    <td class="key">Fecha de creaci&oacute;n:</td>
                    <td class="value"><%= expCreationDate %></td>
                </tr>
                <tr>
                    <td align="right" colspan="2">
                        <form action="../srvExperimentExecution" method="post" name="form1">
                            <input id="btnIniciar" type="submit" value="Iniciar" 
                                   <%= isRunning ? Constants.DisabledControl : "" %>/>
                        </form>
                        <form action="../srvExperimentStop" method="post" name="form1">
                            <input id="btnTerminar" type="submit" value="Detener"
                                   <%= !isRunning ? Constants.DisabledControl : "" %>>
                        </form>
                    </td>
                </tr>
            </table>
        </td></tr></table>

        <div class="title">Par&aacute;metros de entrada</div>
        <table style="width:100%"><tr><td align="center">
            <table class="table" width="70%" border="1">
                <tr class="tableHeader">
                    <td>Nombre</td>
                    <td>Tipo</td>
                    <td>Valor</td>
                </tr>
                <%= expParameterHTML %>
            </table>
        </td></tr></table>

        <div class="title">Corridas</div>
        <table style="width:100%"><tr><td align="center">
            <table class="table" width="70%" border="1">
                <tr class="tableHeader">
                    <td>Fecha inicio</td>
                    <td>Fecha finalizaci&oacute;n</td>
                    <td colspan="2">Estad&iacute;sticas</td>
                    <td>Resultados</td>
                </tr>
                <%= expExecutionsHTML %>
            </table>
        </td></tr></table>

        <div class="title">Contenidos del directorio</div>
        <table style="width:100%"><tr><td align="center">
            <table class="table" width="70%" border="1">
                <tr>
                    <td align="left">
                        <ul>
                            <%= expInternalStructHTML %>
                        </ul>
                    </td>
                </tr>
            </table>
        </td></tr></table>
    </body>
</html>

