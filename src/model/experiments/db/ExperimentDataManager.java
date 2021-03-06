/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments.db;

import db.*;
import files.DirectoryManager;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import model.Experiment;
import model.ExperimentBase;
import model.ExperimentParameter;
import model.ExperimentExecution;
import model.FolderStructure;
import model.NodeStatistics;
import model.ParallelExpExecution;

/**
 * This class do all the database operations related to experiments, it's an auxiliar class to
 * ExperimentManager
 * @see experiments.ExperimentManager
 * @author rdinarte
 */
public class ExperimentDataManager {

    // Attributes
    // -------------------------------------------------------------------------
    private DataHelper dataHelper;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentDataManager()
    {
        dataHelper = new DataHelper();
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Builds a tree with all the children files of a file and returns it in a FolderStructure
     * The tree can have more than one level.
     * @param realFile The parent file of the tree
     * @return Returns a file system tree in a FolderStructure.
     */
    private FolderStructure BuildFolderStructure(File realFile)
    {
        String name = realFile.getName();
        LinkedList<FolderStructure> children = new LinkedList<FolderStructure>();
        if(realFile.isDirectory())
        {
            File[] fileChildren = realFile.listFiles();
            for(int fileI = 0; fileI < fileChildren.length; fileI++)
                children.add(BuildFolderStructure(fileChildren[fileI]));
        }
        return new FolderStructure(name, children);
    }

    /**
     * Retrieves all the experiments that were configured by a user
     * @param userId The id of the owner of the experiments
     * @return A list with all the experiment configurations. They do not contain
     * the experiment parameters nor the historic executions.
     */
    public Experiment[] GetExperimentsGenInfo(int userId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.UserExpParamUserId, userId),
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.UserExpSp, parameters);
        LinkedList<Experiment> resultList = new LinkedList<Experiment>();
        while(reader.next())
        {
            int id = reader.getInt(Constants.UserExpColId);
            String name = reader.getString(Constants.UserExpColName);
            String description = reader.getString(Constants.UserExpColDescription);
            String executablePath = reader.getString(Constants.UserExpColExecPath);
            boolean isParallel = reader.getBoolean(Constants.UserExpColParallelExec);
            String inputLineParams = reader.getString(Constants.UserExpColInputLine);
            String inputFilePath = reader.getString(Constants.UserExpColInFilePath);
            Date creationDate = reader.getDate(Constants.UserExpColCreationDate);
            String baseAddress = DirectoryManager.GetInstance().GetPathForExperiment(userId, id);
            File experimentDir = new File(baseAddress);

            Experiment exp = new Experiment
                    (id, name, description, executablePath, inputLineParams,
                    inputFilePath, creationDate, Experiment.ExecStatus.Stopped,
                    BuildFolderStructure(experimentDir));
            if(isParallel)
                GetParallelConfig(exp);
            resultList.add(exp);
        }
        dataHelper.CloseConnection(reader);
        return resultList.toArray(new Experiment[resultList.size()]);
    }

    /**
     * It establishes the parallelism execution configuration for an experiment.
     * If the experiment doesn't have any parallelism configuration, the method
     * has no effect.
     * @param userId The id of the owner of the experiments
     * @return A list with all the experiment configurations. They do not contain
     * the experiment parameters nor the historic executions.
     */
    private void GetParallelConfig(Experiment exp)
    {
        try
        {
            SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.ExpParConfExpId, exp.getId()),
            };
            ResultSet reader = dataHelper.ExecuteSP(Constants.ExpParConfSp, parameters);
            if(reader.next())
            {
                int procNumber = reader.getInt(Constants.ExpParConfColProcNumb);
                boolean saveForForEachNode = reader.getBoolean(Constants.ExpParConfSaveNodeLog);
                String shrDir = reader.getString(Constants.ExpParConfShrWorkDir);
                String middware = reader.getString(Constants.ExpParConfMiddleware);
                exp.AddParallelConfiguration(procNumber, middware, shrDir, saveForForEachNode);
            }
            dataHelper.CloseConnection(reader);
        }
        catch(Exception ex)
        {
        }
    }

    /**
     * Retrieves all the parameters of an experiment configured by a user
     * @param expId Unique id of the experiment
     * @return A list with all the experiment parameters of an experiment.
     */
    public ExperimentParameter[] GetExperimentParams(int expId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.ExpParamParamExpId, expId),
            };
        ResultSet reader = dataHelper.ExecuteSP(Constants.ExpParamSp, parameters);
        LinkedList<ExperimentParameter> resultList = new LinkedList<ExperimentParameter>();
        while(reader.next())
        {
            String name = reader.getString(Constants.ExpParamColName);
            String type = reader.getString(Constants.ExpParamColType);
            String value = reader.getString(Constants.ExpParamColValue);
            ExperimentParameter param = new ExperimentParameter(name, type, value);
            resultList.add(param);
        }
        dataHelper.CloseConnection(reader);
        return resultList.toArray(new ExperimentParameter[resultList.size()]);
    }

    /**
     * Retrieves all the historical executions of an experiment previously configured
     * by the user.
     * @param expId Unique id of the experiment
     * @return A list with all the historical executions of the experiment.
     */
    public HashMap<Integer, ExperimentExecution> GetExperimentExecs(int expId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.ExpStatsParamExpId, expId),
            };
        ResultSet reader = dataHelper.ExecuteSP(Constants.ExpStatsSp, parameters);
        HashMap<Integer, ExperimentExecution> result = new HashMap<Integer, ExperimentExecution>();
        while(reader.next())
        {
            int id = reader.getInt(Constants.ExpStatsColId);
            Date startDate = reader.getDate(Constants.ExpStatsColStartDate);
            Date finishDate = reader.getDate(Constants.ExpStatsColFinishDate);
            String outputPath = reader.getString(Constants.ExpStatsColOutputPath);
            float cpuUsage = reader.getFloat(Constants.ExpStatsColCPUUsage);
            float memUsed = reader.getFloat(Constants.ExpStatsColUsedMemory);
            int wallClockTime = reader.getInt(Constants.ExpStatsColWallClockTime);

            ExperimentExecution param = new ExperimentExecution(
                    id, startDate, finishDate, outputPath, memUsed, cpuUsage, wallClockTime);
            result.put(id, param);
        }
        dataHelper.CloseConnection(reader);
        return result;
    }

    /**
     * Gets the statistics for each node for an specific execution
     * @param executionId The execution id
     * @return The nodes statistics
     * @throws SQLException if the SP couldn't be executed
     */
    public NodeStatistics[] GetNodeStats(int executionId) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.NodeStatsParamExpId, executionId),
            };
        ResultSet reader = dataHelper.ExecuteSP(Constants.NodeStatsSp, parameters);
        LinkedList<NodeStatistics> result = new LinkedList<NodeStatistics>();
        while(reader.next())
        {
            int number = reader.getInt(Constants.NodeStatsColNumber);
            int totalTime = reader.getInt(Constants.NodeStatsColTime);
            float memory = reader.getFloat(Constants.NodeStatsColMemory);
            float cpu = reader.getFloat(Constants.NodeStatsColCpu);
            NodeStatistics stats = new NodeStatistics(number, totalTime, memory, cpu);
            result.add(stats);
        }
        dataHelper.CloseConnection(reader);
        return result.toArray(new NodeStatistics[result.size()]);
    }

    /**
     * Saves the statistics of an experiment execution.
     * @param expExec Information of the experiment execution to be saved
     * @param expId Unique identifier of the experiment that was execute.
     * @return Id of the execution in the data base.
     */
    public int SaveExperimentExecution(ExperimentExecution exec, int expId) throws SQLException
    {
        try
        {
            SqlParameter[] parameters = new SqlParameter[]{
                    new SqlParameter(Constants.SaveExecParamStartDate, exec.getStartDate()),
                    new SqlParameter(Constants.SaveExecParamFinishDate, exec.getEndDate()),
                    new SqlParameter(Constants.SaveExecParamExpId, expId),
                    new SqlParameter(Constants.SaveExecParamUsedMemory, exec.getUsedMemory()),
                    new SqlParameter(Constants.SaveExecParamWallCLockTime, 
                            exec.getCPUTimeSeconds()),
                    new SqlParameter(Constants.SaveExecParamOutputFilePath, exec.getOutputPath()),
                    new SqlParameter(Constants.SaveExecParamCPUUsage, exec.getCPUUsage()),
                };
            ResultSet reader = dataHelper.ExecuteSP(Constants.SaveExecSp, parameters);
            reader.next();
            int resultValue = reader.getInt(1);
            if (exec instanceof ParallelExpExecution)
            {
                NodeStatistics[] nodeStats = ((ParallelExpExecution) exec).getNodeStatistics();
                for(int nodeI = 0; nodeI < nodeStats.length; nodeI++)
                    SaveNodeStats(nodeStats[nodeI], resultValue);
            }
            dataHelper.CloseConnection(reader);
            return resultValue;
        }
        catch(Exception ex)
        {
            return -1;
        }
    }

    /**
     * Saves the statistics of an experiment execution.
     * @param expExec Information of the experiment execution to be saved
     * @param expId Unique identifier of the experiment that was execute.
     * @return Id of the execution in the data base.
     */
    public int SaveNodeStats(NodeStatistics nodeStats, int execId) throws SQLException
    {
        try
        {
            SqlParameter[] parameters = new SqlParameter[]{
                    new SqlParameter(Constants.SaveNodeStatsParamExecId, execId),
                    new SqlParameter(Constants.SaveNodeStatsParamCPUUsage,
                            nodeStats.getCpuUsage()),
                    new SqlParameter(Constants.SaveNodeStatsParamCPUTime,
                            nodeStats.getTotalTime()),
                    new SqlParameter(Constants.SaveNodeStatsParamNodeNumber,
                            nodeStats.getNodeNumber()),
                    new SqlParameter(Constants.SaveNodeStatsParamUsedMemory,
                            nodeStats.getUsedMemory())
                };
            ResultSet reader = dataHelper.ExecuteSP(Constants.SaveNodeStatsSp, parameters);
            Integer resultValue = 0;
            dataHelper.CloseConnection(reader);
            return resultValue;
        }
        catch(Exception ex)
        {
            return -1;
        }
    }

    /**
     * Stores a new experiment configuration in the database
     * @param userId The id of the owner of the experiment
     * @param experiment The experiment that will be created
     * @return The id of the new experiment
     * @throws SQLException if the experiment configuration couldn't be stored
     */
    public int CreateExperiment(final int userId, final ExperimentBase experiment) throws SQLException
    {
        SqlParameter[] parameters = new SqlParameter[]{
            new SqlParameter(Constants.NewExperimentParamName, experiment.getName()),
            new SqlParameter(Constants.NewExperimentParamDescription, experiment.getDescription()),
            new SqlParameter(Constants.NewExperimentParamApplication,
                             experiment.getExecutablePath()),
            new SqlParameter(Constants.NewExperimentParamParallelExecution,
                             experiment.usesParallelExecution()),
            new SqlParameter(Constants.NewExperimentParamInputLine,
                             experiment.getInputParametersLine()),
            new SqlParameter(Constants.NewExperimentParamInputPath, experiment.getInputFilePath()),
            new SqlParameter(Constants.NewExperimentParamOwnerId, userId)
        };
        ResultSet reader = dataHelper.ExecuteSP(Constants.NewExperimentSp, parameters);
        reader.next();
        int result = reader.getInt(Constants.NewExperimentColId);
        return result;
    }

    /**
     * Stores the parallel execution configuration of an experiment
     * @param experiment The experiment to associate the parallel configuration
     * @throws SQLException if the configuration couldn't be saved
     */
    public void AddParallelConfiguration(final ExperimentBase experiment) throws SQLException
    {
        if(experiment.usesParallelExecution())
        {
            SqlParameter[] parameters = new SqlParameter[]{
                new SqlParameter(Constants.ParallelParamExperimentId, experiment.getId()),
                new SqlParameter(Constants.ParallelParamProcessors,
                                 experiment.getNumberOfProcessors()),
                new SqlParameter(Constants.ParallelParamSaveNodeLog,
                                 experiment.willSaveEachNodeLog()),
                new SqlParameter(Constants.ParallelParamSharedWorkingDir,
                                 experiment.getSharedWorkingDirectory()),
                new SqlParameter(Constants.ParallelParamMiddleware, experiment.getMiddleware())
            };
            dataHelper.ExecuteNoResultsetSP(Constants.ParallelSp, parameters);
        }
    }

    public void AddExperimentParameters(ExperimentBase experiment) throws SQLException
    {
        for(ExperimentParameter param : experiment.getParameters())
        {
            SqlParameter[] sqlParameters = new SqlParameter[]{
                    new SqlParameter(Constants.AddParamsParamExperimentId, experiment.getId()),
                    new SqlParameter(Constants.AddParamsParamName, param.getName()),
                    new SqlParameter(Constants.AddParamsParamType, param.getType()),
                    new SqlParameter(Constants.AddParamsParamValue, param.getValue())
                };
            dataHelper.ExecuteNoResultsetSP(Constants.AddParamsSp, sqlParameters);
        }
        dataHelper.CloseConnection();
    }

}
