/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author cfernandez
 */
public class ParallelStatisticsGenerator extends TimerTask
{
    private String processId;
    private int totalSamples;
    private float[] totalUsedMemory;
    private float[] totalCPUUsage;
    private String[] totalCPUTime;
    private String[] hosts;

    public ParallelStatisticsGenerator (int processId, String[] hosts)
    {
        this.processId = Integer.toString(processId);
        totalSamples = 0;
        // We are also counting the master node
        totalUsedMemory = new float[hosts.length];
        totalCPUUsage = new float[hosts.length];
        totalCPUTime = new String[hosts.length];
        this.hosts = hosts;
    }

    private void processNodeOutput(int nodeIndex, BufferedReader stdOutStream)
    {
        try
        {
            String stdOut = "";
            String newLine = "";
            while((newLine = stdOutStream.readLine()) != null)
            {
                int start = newLine.indexOf(processId);
                if (start >= 0)
                {
                    stdOut = newLine.substring(start);
                    break;
                }
            }
            if(!stdOut.isEmpty())
            {
                String[] psAuxOutput = stdOut.split(" ");
                int outIndex = 1;
                if(outIndex < psAuxOutput.length)
                {
                    outIndex  = indexOfNextNonEmpty(outIndex, psAuxOutput);
                    // Obtain CPU Usage percentage
                    totalCPUUsage[nodeIndex] += Float.parseFloat(psAuxOutput[outIndex]);
                    outIndex++;
                    outIndex  = indexOfNextNonEmpty(outIndex, psAuxOutput);
                    // Obtain Used Memory percentage
                    totalUsedMemory[nodeIndex] += Float.parseFloat(psAuxOutput[outIndex]);
                    // Obtain System Time
                    final int COLUMNS_TIL_TIME = 6;
                    for(int colI = 0; colI < COLUMNS_TIL_TIME; colI++)
                        outIndex = indexOfNextNonEmpty(outIndex + 1, psAuxOutput);
                    totalCPUTime[nodeIndex] = psAuxOutput[outIndex];
                }
            }
        }
        catch(Exception ex)
        {   }
    }

    public void run ()
    {
        for(int hostI = 0; hostI < hosts.length; hostI++)
        {
            String host = hosts[hostI];
            try
            {
                //ToDo: Secure Shell para cada uno de los nodos
                //String psCommand = "ssh " + host + " 'ps aux'";
                String psCommand = "ps aux";
                Process psAux = Runtime.getRuntime().exec(psCommand);
                psAux.waitFor();
                BufferedReader stdOutStream = new BufferedReader(new
                     InputStreamReader(psAux.getInputStream()));
                processNodeOutput(hostI, stdOutStream);
            }
            catch(Exception ex)
            {   }
        }
        totalSamples++;
    }

    public int indexOfNextNonEmpty(int index, String[] array)
    {
        while(array[index].isEmpty())
            index++;
        return index;
    }

    public float[] getCPUUsagePercentage()
    {
        if(totalSamples > 0)
        {
            float[] CPUUsagePercentageList = new float[hosts.length];
            for(int hostI = 0; hostI < hosts.length; hostI++)
                CPUUsagePercentageList[hostI] = totalCPUUsage[hostI] / totalSamples;
            return CPUUsagePercentageList;
        }
        else return new float[hosts.length];
    }

    public float[] getUsedMemoryPercentage()
    {
        if(totalSamples > 0)
        {
            float[] UsedMemoryPercentageList = new float[hosts.length];
            for(int hostI = 0; hostI < hosts.length; hostI++)
                UsedMemoryPercentageList[hostI] = totalUsedMemory[hostI] / totalSamples;
            return UsedMemoryPercentageList;
        }
        else return new float[hosts.length];
    }

    public int[] getCPUTimeSeconds()
    {
        if(totalSamples > 0)
        {
            int[] CPUTimeSecondsPercentageList = new int[hosts.length];
            for(int hostI = 0; hostI < hosts.length; hostI++)
            {
                String[] times = totalCPUTime[hostI].split(":");
                CPUTimeSecondsPercentageList[hostI] =
                        Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
            }
            return CPUTimeSecondsPercentageList;
        }
        else return new int[hosts.length];
    }

    public String[] getHosts()
    {
        return hosts;
    }

}
