/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.EnumMap;

/**
 *
 * @author rdinarte
 */
public class ConfigurationManager {

    // Attributes
    // -------------------------------------------------------------------------
    private EnumMap<ConfigurationOptions, Object> data;
    private static ConfigurationManager instance = new ConfigurationManager();

    private enum ConfigurationOptions
    {
        DefaultNumberOfClusterNodes,
        NumberOfNodesForExecution,
        PathForUsersData
    }

    // Constructor
    // -------------------------------------------------------------------------
    private ConfigurationManager()
    {
        LoadData();
    }

    // Class methods
    // -------------------------------------------------------------------------
    /**
     * Gets the singleton instance for this class
     * @return The singleton instance for this class
     */
    public static ConfigurationManager GetInstance()
    {
        return instance;
    }

    // Instance methods
    // -------------------------------------------------------------------------
/**
     * Gets the number of nodes that can be used for execution in the cluster
     * @return The number of nodes that can be used for execution in the cluster
     */
    public int GetNumberOfClusterNodes()
    {
        return (Integer)data.get(ConfigurationOptions.NumberOfNodesForExecution);
    }

    /**
     * Gets the default number of nodes that will be used for an experiment creation
     * @return The default number of nodes that will be used for an experiment creation
     */
    public int GetDefaultNumberOfNodesForExecution()
    {
        return (Integer)data.get(ConfigurationOptions.DefaultNumberOfClusterNodes);
    }

    public String GetDataBasePath()
    {
        return (String)data.get(ConfigurationOptions.PathForUsersData);
    }

    public void LoadData()
    {
        //TODO: Hacer este metodo
        data = new EnumMap(ConfigurationOptions.class);
        data.put(ConfigurationOptions.NumberOfNodesForExecution, 10);
        data.put(ConfigurationOptions.DefaultNumberOfClusterNodes, 4);
        data.put(ConfigurationOptions.PathForUsersData, "/home/grupo13/users/");
    }

}