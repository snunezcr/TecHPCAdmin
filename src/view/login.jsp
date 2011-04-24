<%-- 
    Document   : login
    Created on : Mar 25, 2011, 9:56:58 AM
    Author     : rdinarte
--%>

<%@page import="controller.RequestManager"%>
<%
    //In case there was an error let's display it
    String errorMessage = RequestManager.GetError(request);
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HPC Administration</title>
    <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
</head>
<body>
    <table style="width: 100%;">
        <tr class="header">
            <td>
                <div class="siteName">
                    HPC<br />
                    Administration
                </div>
            </td>
        </tr>
        <tr class="body">
            <td valign="middle" align="center">
                <form action="srvLogin" method="post" name="form1">
                    <table class="table" width="20%">
                        <tr>
                            <td colspan="2"><div class="menuTitle">Autenticación de usuario</div></td>
                        </tr>
                        <tr>
                            <td class="key">Login:</td>
                            <td class="key">
                                <input id="txtUserName" name="txtUserName" type="text" value="rdinarte" /></td>
                        </tr>
                        <tr>
                            <td class="key">Contraseña:</td>
                            <td class="key">
                                <input id="txtPassword" name="txtPassword" type="password" value="123" /></td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <input id="btnSave" type="submit" value="Aceptar" /></td>
                        </tr>
                    </table>
                </form>
                <label class="error" id="lblError" ><%= errorMessage %></label>
            </td>
        </tr>
        <tr class="footer">
            <td>
                HPC Administration - 2011
            </td>
        </tr>
    </table>
</body>
</html>
