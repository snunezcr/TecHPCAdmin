<%-- 
    Document   : new-experiment
    Created on : Mar 26, 2011, 8:56:48 PM
    Author     : rdinarte
--%>

<%@page import="java.util.Collection"%>
<%@page import="java.util.HashMap"%>
<%@page import="common.CommonFunctions"%>
<%@page import="model.ExperimentParameter"%>
<%@page import="common.ServiceResult"%>
<%@page import="model.Application"%>
<%@page import="controller.Constants"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;
    //Variables
    HpcaServiceAgent agent = new HpcaServiceAgent();
    String parallelEnabled = "disabled='disabled'";
    String errorMessage = "";
    String enabled = "";
    String applicationOptions = "";
    String nodeSelection = "";
    String paramsText = "";
    String paramsTypesText = "";
    String hdParams = "";
    String txtName = "";
    String txtDescription = "";
    String chkParallel = "";
    String txtSharedDir = "";
    String chkNodeLog = "";
    String txtMiddleware = "";
    String txtInputParams = "";
    String dlgInputFile = "";

    int maxNodes = agent.GetNumberOfClusterNodes();
    int defaultNodes = agent.GetDefaultNumberOfNodesForExecution();
    //Fill the number of nodes combobox, in case this experiment is configured with parallel
    //execution
    for(int nodes = 1; nodes <= maxNodes; nodes++)
        if(nodes != defaultNodes)
            nodeSelection+= String.format("<option value='%1$d'>%1$d</option>", nodes);
        else
            nodeSelection+= String.format("<option value='%1$d' selected>%1$d</option>", nodes);

    //Get this user's applications
    ServiceResult<HashMap<Integer, Application>> applicationsResult = agent.GetUsersApplications(request);
    if(applicationsResult.getStatus() == ServiceResult.OperationResult.Error
            || applicationsResult.getValue().values().size() == 0)
    {
        //An error occurred so report it to the user and disable the controls
        if(applicationsResult.getValue().values().size() == 0)
            errorMessage = Constants.ZeroApplicationsErrorMessage;
        else
            errorMessage = Constants.GetApplicationsErrorMessage;
        enabled = Constants.DisabledControl;
    }
    else
    {
        Collection<Application> applications = applicationsResult.getValue().values();
        for(Application app : applications)
            applicationOptions += String.format("<option value='%1$s'>%1$s</option>",
                                                app.getRelativePath());
    }

    //Get the parameters types
    ServiceResult<String[]> typesResult = agent.GetParameterTypes(request);
    if(typesResult.getStatus() == ServiceResult.OperationResult.Error
            || typesResult.getValue().length == 0)
    {
        //An error occurred so report it to the user and disable the controls
        if(typesResult.getValue().length == 0)
            errorMessage = Constants.ZeroTypesErrorMessage;
        else
            errorMessage = Constants.GetTypesErrorMessage;
        enabled = Constants.DisabledControl;
    }
    else
    {
        String[] types = typesResult.getValue();
        for(String type : types)
            paramsTypesText += String.format("<option value='%1$s'>%1$s</option>", type);
    }
    //This means that this page was invoked from the servlet, because a parameter was added or
    //deleted, so we need to recover the previous information
    if(request.getAttribute("isFilling") != null)
    {
        ExperimentParameter[] expParams = (ExperimentParameter[])request.getAttribute("expParams");
        int paramNumber = 0;
        for(ExperimentParameter expParam : expParams)
        {
            hdParams += String.format("%1$s!_@_!%2$s!_@_!%3$s!_@_!", expParam.getName(), expParam.getType(), expParam.getValue());
            paramsText += "<tr><td align='center'>";
            paramsText += String.format("<input type='text' value='%1$s' disabled>", expParam.getName());
            paramsText += "</td><td align='center'>";
            paramsText += String.format("<input type='text' value='%1$s' disabled>", expParam.getType());
            paramsText += "</td><td align='center'>";
            paramsText += String.format("<input type='text' value='%1$s' disabled>", expParam.getValue());
            paramsText += "</td><td align='center'>";
            paramsText += String.format("<input type='submit' value='Eliminar' onclick='erase(%1$d)' >", paramNumber++);
            paramsText += "</td></tr>";
        }

        txtName = request.getAttribute("txtName").toString();
        txtDescription = request.getAttribute("txtDescription").toString();
        boolean parallel = CommonFunctions.GetValue((String)request.getAttribute("chkParallel"));
        if(parallel)
        {
            chkParallel = "checked='true'";
            parallelEnabled= "";
        }
        
        txtSharedDir = request.getAttribute("txtSharedDir").toString();
        boolean nodeLog = CommonFunctions.GetValue((String)request.getAttribute("chkNodeLog"));
        chkNodeLog = nodeLog ? "checked='true'" : "";
        txtMiddleware = request.getAttribute("txtMiddleware").toString();
        txtInputParams = request.getAttribute("txtInputParams").toString();
        dlgInputFile = request.getAttribute("dlgInputFile").toString();
    }

%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
        <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
        <script language="javascript" type="text/javascript">
            function enable()
            {
                var disabled = !document.getElementById('chkParallel').checked;
                document.getElementById('cmbProcessors').disabled = disabled;
                document.getElementById('chkNodeLog').disabled = disabled;
                document.getElementById('txtSharedDir').disabled = disabled;
                document.getElementById('txtMiddleware').disabled = disabled;
            }

            function validateForm()
            {
                var result = hasText('txtName') && hasText('txtDescription');
                if(!result)
                    alert("<%= Constants.NewExperimentIncompleteInfoErrorMessage %>");
                else if(document.getElementById('chkParallel').checked)
                {
                    result = hasText('txtSharedDir') && hasText('txtMiddleware');
                    if(!result)
                        alert("<%= Constants.IncompleteParallelInfoErrorMessage %>");
                }
                return result;
            }

            function trim(val)
            {
                var result = val.replace(/^\s+/, '');
                return result.replace(/\s+$/, '');
            }

            function hasText(control)
            {
                return trim(document.getElementById(control).value).length > 0;
            }
            
            function validateParam()
            {
                var result = hasText('txtNewParam') && hasText('txtNewValue');
                if(!result)
                    alert("<%= Constants.IncompleteParamErrorMessage %>");
                return result;
            }

            function erase(row)
            {
                setAction('delete');
                document.getElementById('hdDeleteRow').value = row;
            }

            function setAction(action)
            {
                document.getElementById('hdAction').value = action;
            }
        </script>
        <style type="text/css">
            #txtDescription
            {
                height: 60px;
                width: 205px;
            }
            #txtName
            {
                width: 200px;
            }
            #txtSharedDir
            {
                width: 200px;
            }
            #txtMiddleware
            {
                width: 200px;
            }
            #txtInputParams
            {
                width: 353px;
            }
        </style>
    </head>
    <body>
        <div class="title">Nuevo experimento</div>
        <form method="post" name="form1" action="/Hpca/srvNewExperiment" enctype="multipart/form-data"><table style="width:100%"><tr><td align="center">
            <table class="table" width="70%">
                <tr>
                    <td class="key">Nombre:</td>
                    <td class="key">
                        <input id="txtName" name="txtName" id="txtName" type="text" <%= enabled %> value="<%= txtName %>" />
                    </td>
                </tr>
                <tr>
                    <td class="key">Descripción:</td>
                    <td class="key">
                        <textarea id="txtDescription" rows="2" name="txtDescription" <%= enabled %> ><%= txtDescription %></textarea>
                    </td>
                </tr>
                <tr>
                    <td class="key">Ejecutable:</td>
                    <td class="key">
                        <select id="cmbApplication" name="cmbApplication" <%= enabled %> >
                            <%= applicationOptions %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="key">Ejecutar en paralelo:</td>
                    <td class="key">
                        <input id="chkParallel" name="chkParallel" type="checkbox" onclick="enable();" <%= enabled %> <%= chkParallel %> />
                    </td>
                </tr>
                <tr>
                    <td class="key">Cantidad de procesadores:</td>
                    <td class="key">
                        <select id="cmbProcessors" <%= parallelEnabled %> name="cmbProcessors">
                            <%= nodeSelection %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="key">Guardar log de cada nodo:</td>
                    <td class="key">
                        <input id="chkNodeLog" name="chkNodeLog" <%= parallelEnabled %> type="checkbox" <%= chkNodeLog %> />
                    </td>
                </tr>
                <tr>
                    <td class="key">Directorio compartido de trabajo:</td>
                    <td class="key">
                        <input id="txtSharedDir" name="txtSharedDir" type="text" <%= parallelEnabled %> value="<%= txtSharedDir %>" />
                    </td>
                </tr>
                <tr>
                    <td class="key">Middleware:</td>
                    <td class="key">
                        <input id="txtMiddleware" name="txtMiddleware" type="text" <%= parallelEnabled %> value="<%= txtMiddleware %>" />
                    </td>
                </tr>
                <tr>
                    <td class="key">Parámetros de entrada:</td>
                    <td class="key">
                        <input id="txtInputParams" type="text" name="txtInputParams" <%= enabled %> value="<%= txtInputParams %>" />
                    </td>
                </tr>
                <tr>
                    <td class="key">Archivo de entrada:</td>
                    <td class="key">
                        <input id="dlgInputFile" name="dlgInputFile" type="file" <%= enabled %> value="<%= dlgInputFile %>" />
                    </td>
                </tr>
            </table>
            <label class="error" id="lblError" ><%= errorMessage %></label>
            </td></tr>
            <tr><td align="center">
                <div class="title">Parámetros de entrada</div>
            </td></tr>
            <tr><td align="center">
                <input type="hidden" id="hdDeleteRow" value="-1" name="hdDeleteRow">
                <input type="hidden" id="hdAction" value="-1" name="hdAction">
                <input type="hidden" id="hdParams" value="<%= hdParams %>" name="hdParams">
                <table class="table" width="70%">
                    <tr class="tableHeader">
                        <td>Nombre</td>
                        <td>Tipo</td>
                        <td>Valor</td>
                        <td>Acción</td>
                    </tr>
                    <%= paramsText %>
                    <tr>
                        <td align="center">
                            <input type="text" name="txtNewParam" id="txtNewParam" <%= enabled %>>
                        </td>
                        <td align="center">
                            <select id="cmbNewType" name="cmbNewType" <%= enabled %> >
                                <%= paramsTypesText %>
                            </select>
                        </td>
                        <td align="center">
                            <input type="text" name="txtNewValue" id="txtNewValue" <%= enabled %>>
                        </td>
                        <td align="center">
                            <input type="submit" value="Agregar" onclick="if (!validateParam()) return false; setAction('add');" <%= enabled %>>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" align="center">
                            <label class="error" id="lblParamError"></label>
                        </td>
                    </tr>
                </table>
            </td></tr>
            <tr><td align="center">
                <table width="70%">
                <tr><td colspan="2" /></tr>
                <tr>
                    <td align="right" colspan="2">
                        <button onclick="window.location='my-experiments.jsp'">Cancelar</button>
                        &nbsp;
                        <input type="submit" onclick="if (!validateForm()) return false; setAction('save');" <%= enabled %> value="Guardar" >
                    </td>
                </tr>
                </table>
        </td></tr></table></form>
    </body>
</html>
