<%-- 
    Document   : new-user
    Created on : Apr 23, 2011, 7:46:23 PM
    Author     : rdinarte
--%>

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
    HpcaServiceAgent agent = new HpcaServiceAgent();

    ServiceResult<String[]> roles = agent.GetUserRoles(request);
    if(roles.getStatus() != ServiceResult.OperationResult.Succeeded || roles.getValue().length == 0)
    {//We couldn't get any roles from the database so there's nothing to do here
        enabled = Constants.DisabledControl;
        errorMessage = Constants.GetRolesErrorMessage;
        return;
    }

    String[] rolesArray = roles.getValue();
    for(String role : rolesArray)
        rolesHtml += "<option>" + role + "</option>";

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
                    hasText('txtLastName2') && hasText('txtUserName') && hasText('txtPassword') &&
                    hasText('txtConfirmPassword');
                if(!result)
                    alert("<%= Constants.NewUserIncompleteInfoErrorMessage %>");
                else if(document.getElementById('txtPassword').value !=
                    document.getElementById('txtConfirmPassword').value)
                {
                    result = false;
                    alert("<%= Constants.BadPasswordConfirmation %>");
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
        </script>
    </head>
    <body>
        <div class="title">Crear un nuevo usuario</div>
        <form method="post" name="form1" action="/Hpca/srvNewUser">
            <table style="width:100%"><tr><td align="center">
                <table class="table" width="70%">
                    <tr>
                        <td class="key">Nombre:</td>
                        <td class="key">
                            <input id="txtName" name="txtName" type="text" <%= enabled %> /></td>
                    </tr>
                    <tr>
                        <td class="key">Primer apellido:</td>
                        <td class="key">
                            <input id="txtLastName1" name="txtLastName1" type="text" <%= enabled %> /></td>
                    </tr>
                    <tr>
                        <td class="key">Segundo apellido:</td>
                        <td class="key">
                            <input id="txtLastName2" name="txtLastName2" type="text" <%= enabled %> /></td>
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
                        <td class="key">Login:</td>
                        <td class="key">
                            <input id="txtUserName" name="txtUserName" type="text" <%= enabled %> /></td>
                    </tr>
                    <tr>
                        <td class="key">Contraseña:</td>
                        <td class="key">
                            <input id="txtPassword" name="txtPassword" type="password" <%= enabled %> /></td>
                    </tr>
                    <tr>
                        <td class="key">Confirmar contraseña:</td>
                        <td class="key">
                            <input id="txtConfirmPassword" name="txtConfirmPassword" type="password" <%= enabled %> /></td>
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
