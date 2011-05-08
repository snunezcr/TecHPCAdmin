/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package config.io;

import db.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * This class do all the files operations related to configuration, it's an auxiliar class to
 * ConfigurationManager
 * @author rdinarte
 */
public class ConfigurationDataManager {

    // Attributes
    // -------------------------------------------------------------------------
    private HashMap<String, String> data;

    // Constructor
    // -------------------------------------------------------------------------
    public ConfigurationDataManager() throws Exception
    {
        data = new HashMap<String, String>();
        loadData();
    }

    // Instance methods
    // -------------------------------------------------------------------------
    public int GetNodes()
    {
        return Integer.parseInt(data.get(Constants.ConfigNodes));
    }
    
    public int GetDefaultNodes()
    {
        return Integer.parseInt(data.get(Constants.ConfigDefaultNodes));
        
    }
    
    public String GetUsersPath()
    {
        return data.get(Constants.ConfigDataPath);
    }

    private void loadData() throws Exception
    {
        FileReader file = new FileReader(Constants.ConfigurationFilePath);
        BufferedReader reader = new BufferedReader(file);
        String line = reader.readLine();
        while(line != null)
        {
            String[] lineData = line.split("=");
            if(lineData.length == 2)
            {
                if(lineData[0].equals(Constants.ConfigDataPath) && !lineData[1].endsWith("/"))
                    lineData[1] += 1;
                data.put(lineData[0], lineData[1]);
            }
            line = reader.readLine();
        }
        reader.close();
    }

}
