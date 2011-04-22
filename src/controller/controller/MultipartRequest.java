/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.DefaultFileItem;
import org.apache.tomcat.util.http.fileupload.DiskFileUpload;

/**
 * Use this class to get the parameters from a request with a multipart/form-data form
 * @author rdinarte
 */
public class MultipartRequest {

    // Attributes
    // -------------------------------------------------------------------------
    HashMap<String, String> parameters;
    HashMap<String, byte[]> files;

    // Constructor
    // -------------------------------------------------------------------------
    public MultipartRequest(HttpServletRequest request, ServletContext context)
    {
        parameters = new HashMap<String, String>();
        files = new HashMap<String, byte[]>();
        parseFile(request, context);
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Gets a parameter from the request
     * @param name The parameter's name
     * @return The parameter's value
     */
    public String getParameter(String name)
    {//Let's implement the same functionality as the normal request
        if(parameters.containsKey(name))
            return parameters.get(name);
        return null;
    }

    /**
     * Gets a file content from the request
     * @param name The parameter's name
     * @return The file content
     */
    public byte[] getFile(String name)
    {//Let's implement the same functionality as the normal request
        if(files.containsKey(name))
            return files.get(name);
        return null;
    }

    /**
     * Extracts the parameters from the multipart/form-data form request
     * @param request The multipart/form-data form request
     */
    private void parseFile(HttpServletRequest request, ServletContext context)
    {
        try
        {
            DiskFileUpload upload = new DiskFileUpload();
            //Let's mark the input stream so it can be read again
            ServletInputStream stream = request.getInputStream();
            stream.mark(request.getContentLength()+10);
            // parse this request by the handler, this gives us a list of items from the request
            List<DefaultFileItem> items = upload.parseRequest(request);
            for(DefaultFileItem item : items)
                if(item.isFormField())
                    parameters.put(item.getFieldName(), item.getString());
                else
                {
                    parameters.put(item.getFieldName(), item.getName());
                    files.put(item.getFieldName(), item.get());
                }
        }
        catch(Exception ex)
        {
        }
    }

}
