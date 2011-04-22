<%-- 
    Document   : main
    Created on : Mar 25, 2011, 4:59:46 PM
    Author     : rdinarte
--%>

<%@page import="controller.RequestManager"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>HPC Administration</title>
        <script language="javascript" type="text/javascript">
            function redirect(address) {
                this.document.getElementById('displayFrame').setAttribute('src', address);
            }
        </script>
        <link href="styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
    </head>
    <body>
        <table style="width: 100%;">
            <tr class="header">
                <td colspan="2">
                    <div class="siteName">
                        HPC<br />
                        Administration
                    </div>
                </td>
            </tr>
            <tr class="body">
                <td class="menu">
                    <br />
                    <div class="menuTitle">Menú Principal</div>
                    <ul style="list-style-type: circle">
                        <li><a href="#" onclick="redirect('normal/my-programs.jsp')">Mis ejecutables</a></li>
                        <li><a href="#" onclick="redirect('normal/my-experiments.jsp')">Mis experimentos</a></li>
                        <li><a href="#" onclick="redirect('normal/new-experiment.jsp')">Configurar nuevo experimento</a></li>
                        <li><a href="#" onclick="redirect('normal/upload-program.jsp')">Subir ejecutable</a></li>
                        <li><a href="#" onclick="redirect('normal/install-program.jsp')">Instalar programa</a></li>
                    </ul>
                    <br />
                    <div class="menuTitle">Menú de Administrador</div>
                    <ul style="list-style-type: circle">
                        <li><a href="#" onclick="redirect('administrator/all-users.jsp')">Lista de usuarios</a></li>
                        <li><a href="#" onclick="redirect('administrator/new-user.jsp')">Agregar usuario</a></li>
                    </ul>
                </td>
                <td valign="top">
                    <iframe width="100%" class="body" src="normal/new-experiment.jsp" id="displayFrame" frameborder="0"></iframe>
                </td>
            </tr>
            <tr class="footer">
                <td colspan="2">
                    HPC Administration - 2011
                </td>
            </tr>
        </table>
    </body>
</html>
