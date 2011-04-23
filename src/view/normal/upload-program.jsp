<%-- 
    Document   : upload-program
    Created on : Apr 22, 2011, 7:52:20 PM
    Author     : rdinarte
--%>

<%@page import="controller.Constants"%>
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
        <title></title>
        <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
        <script language="javascript" type="text/javascript">
            function validateForm()
            {
                var result = hasText('txtFolder') && hasText('txtDescription') && hasText('dlgInputFile');
                if(!result)
                    alert("<%= Constants.UploadProgramaIncompleteInfoErrorMessage %>");
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
        <div class="title">Subir un ejecutable</div>
        <form method="post" name="form1" action="/Hpca/srvUploadProgram" enctype="multipart/form-data">
            <table style="width:100%"><tr><td align="center">
                <table class="table" width="70%">
                    <tr>
                        <td class="key">Descripción:</td>
                        <td class="key"><input id="txtDescription" name="txtDescription" type="text" /></td>
                    </tr>
                    <tr>
                        <td class="key">Ejecutable:</td>
                        <td class="key">
                            <input id="dlgInputFile" name="dlgInputFile" type="file" /></td>
                    </tr>
                    <tr>
                        <td class="key">Carpeta destino:</td>
                        <td class="key"><input id="txtFolder" name="txtFolder" type="text" /></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="2">
                            <button onclick="window.location='my-programs.jsp'">Cancelar</button>
                            &nbsp;
                            <input type="submit" onclick="if (!validateForm()) return false;" value="Guardar" >
                        </td>
                    </tr>
                </table>
            </td></tr></table>
        </form>
    </body>
</html>
