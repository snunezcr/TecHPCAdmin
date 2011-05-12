<%-- 
    Document   : edit-user
    Created on : May 11, 2011, 2:04:14 PM
    Author     : rdinarte
--%>

<%@page import="model.User"%>
<%@page import="java.util.HashMap"%>
<%@page import="controller.Constants"%>
<%@page import="common.ServiceResult"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page import="controller.RequestManager"%>
<%
    if(!RequestManager.VerifyAdminRights(request, response)) return;

    //Variables
    String enabled = "";
    String rolesHtml = "";
    String errorMessage = "";
    String userName = "";
    String name = "";
    String lastName1 = "";
    String lastName2 = "";
    String userEnabled = "";
    HpcaServiceAgent agent = new HpcaServiceAgent();

    ServiceResult<HashMap<Integer, User>> result = agent.GetAllUsers();
    int userId = -1;
    try
    {
        userId = Integer.parseInt(request.getParameter("id").toString());
    }
    catch(Exception ex) { }

    User user = null;
    if(result.getStatus() == ServiceResult.OperationResult.Succeeded && userId > 0 && result.getValue().containsKey(userId))
    {
        user = result.getValue().get(userId);
        userName = user.getUserName();
        name = user.getName();
        lastName1 = user.getLastName1();
        lastName2 = user.getLastName2();
        userEnabled = user.isEnabled() ? "checked" : "";
    }
    else
    {
        enabled = Constants.DisabledControl;
        errorMessage = "Error al cargar la información del usuario.";
        return;
    }

   ServiceResult<String[]> roles = agent.GetUserRoles(request);
    if(roles.getStatus() != ServiceResult.OperationResult.Succeeded || roles.getValue().length == 0)
    {//We couldn't get any roles from the database so there's nothing to do here
        enabled = Constants.DisabledControl;
        errorMessage = Constants.GetRolesErrorMessage;
        return;
    }

    String[] rolesArray = roles.getValue();
    for(String role : rolesArray)
    {
        if(!role.equals(user.getType()))
            rolesHtml += "<option>" + role + "</option>";
        else
            rolesHtml += "<option selected>" + role + "</option>";
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


            function validateForm()
            {
                var result = hasText('txtName') && hasText('txtLastName1') &&
                    hasText('txtLastName2') && hasText('txtUserName');
                if(!result)
                    alert("<%= Constants.NewUserIncompleteInfoErrorMessage %>");
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
        </script>
    </head>
    <body>
        <div class="title">Editar un usuario</div>
        <form method="post" name="form1" action="/Hpca/srvEditUser">
            <table style="width:100%"><tr><td align="center">
                <table class="table" width="70%">
                    <tr>
                        <td class="key">Login:</td>
                        <td class="key">
                            <input id="txtUserName" name="txtUserName" type="text" disabled="disabled" value="<%= userName %>" />
                            <input id="hdUserName" name="hdUserName" type="hidden" value="<%= userName %>" />
                        </td>
                    </tr>
                    <tr>
                        <td class="key">Nombre:</td>
                        <td class="key">
                            <input id="txtName" name="txtName" type="text" <%= enabled %> value="<%= name %>" /></td>
                    </tr>
                    <tr>
                        <td class="key">Primer apellido:</td>
                        <td class="key">
                            <input id="txtLastName1" name="txtLastName1" type="text" <%= enabled %> value="<%= lastName1 %>" /></td>
                    </tr>
                    <tr>
                        <td class="key">Segundo apellido:</td>
                        <td class="key">
                            <input id="txtLastName2" name="txtLastName2" type="text" <%= enabled %> value="<%= lastName2 %>" /></td>
                    </tr>
                    <tr>
                        <td class="key">Rol:</td>
                        <td class="key">
                            <select id="cmbRole" name="cmbRole" <%= enabled %> >
                                <%= rolesHtml %>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="key">Habilitado:</td>
                        <td class="key">
                            <input type="checkbox" <%= userEnabled %> id="chkEnabled" name="chkEnabled" />
                        </td>
                    </tr>
                    <tr>
                        <td align="right" colspan="2">
                            <input type="button" onclick="window.location='all-users.jsp'" value="Cancelar" />
                            &nbsp;
                            <input type="submit" onclick="if (!validateForm()) return false;" <%= enabled %> value="Guardar" >
                        </td>
                    </tr>
                </table>
                <label class="error" id="lblError" ><%= errorMessage %></label>
            </td></tr></table>
        </form>
    </body>
</html>
