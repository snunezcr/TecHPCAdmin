<%-- 
    Document   : install-program
    Created on : May 4, 2011, 10:48:52 AM
    Author     : rdinarte
--%>

<%@page import="controller.RequestManager"%>
<%@page import="controller.HpcaServiceAgent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(!RequestManager.VerifyLogin(request, response)) return;

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title></title>
        <link href="/Hpca/styles/HpcStyles.css" rel="Stylesheet" type="text/css" />
    <script language="javascript" type="text/javascript">
        function validateForm()
        {
            //First let's check if we have all the data
            var result = hasText('txtDescription') && hasText('txtFolder') && hasText('txtExecutable');
            if(document.getElementById('radRepository').checked)
                result &= hasText('cmbRepository') && hasText('txtUrl');
            else
                result &= hasText('dlgLocalFile');

            if(result)
            {//Now let's check if the user is uploading a compressed file
                if(document.getElementById('radLocalFile').checked)
                {
                    var extension = document.getElementById('dlgLocalFile').value.toLowerCase();
                    if(!/.tar.gz$/.test(extension) && !/.tar.bz2$/.test(extension))
                    {
                        alert('Solo se pueden instalar archivos comprimidos (*.tar.gz|*.tar.bz2)');
                        result = false;
                    }
                }
            }
            else
                alert('Deber completar todos los campos para poder instalar el programa.');
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

        function radRepository_onclick() {
            document.getElementById('txtUrl').disabled = false;
            document.getElementById('dlgLocalFile').disabled = true;
            document.getElementById('cmbRepository').disabled = false;
        }

        function radLocalFile_onclick() {
            document.getElementById('txtUrl').disabled = true;
            document.getElementById('dlgLocalFile').disabled = false;
            document.getElementById('cmbRepository').disabled = true;
        }

    </script>
    </head>
<body>
    <div class="title">Instalar un programa</div>
    <form method="post" name="form1" action="/Hpca/srvInstallProgram" enctype="multipart/form-data">
        <table style="width:100%"><tr><td align="center">
            <table class="table" width="70%">
                <tr>
                    <td class="key">Descripción:</td>
                    <td class="key"><input id="txtDescription" type="text" name="txtDescription" /></td>
                </tr>
                <tr>
                    <td class="key">Nombre del archivo que se debe ejecutar:</td>
                    <td class="key"><input id="txtExecutable" type="text" name="txtExecutable" /></td>
                </tr>
                <tr>
                    <td class="key">Origen de programa:</td>
                    <td class="key">
                        <input id="radRepository" checked="checked" name="radSource" type="radio"
                            value="repository" onclick="return radRepository_onclick()" />Repositorio
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <input id="radLocalFile" name="radSource" type="radio"
                               value="localFile" onclick="return radLocalFile_onclick()" />Archivo local</td>
                </tr>
                <tr>
                    <td class="key">Origen del repositorio:</td>
                    <td class="key">
                        <select id="cmbRepository" name="cmbRepository">
                            <option></option>
                            <option>gitHub</option>
                            <option>SVN</option>
                        </select></td>
                </tr>
                <tr>
                    <td class="key">URL del repositorio:</td>
                    <td class="key">
                        <input id="txtUrl" name="txtUrl" type="text" /></td>
                </tr>
                <tr>
                    <td class="key">Archivo local:</td>
                    <td class="key">
                        <input id="dlgLocalFile" name="dlgLocalFile" type="file" disabled="disabled" /></td>
                </tr>
                <tr>
                    <td class="key">Carpeta destino:</td>
                    <td class="key"><input id="txtFolder" name="txtFolder" type="text" /></td>
                </tr>
                <tr>
                    <td align="right" colspan="2">
                        <input type="button" onclick="window.location='my-programs.jsp'" value="Cancelar" />
                        &nbsp;
                        <input type="submit" onclick="if (!validateForm()) return false;" value="Guardar" >
                    </td>
                </tr>
            </table>
        </td></tr></table>
    </form>
</body>
</html>