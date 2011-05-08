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
public class StatisticsGenerator extends TimerTask
{
    private String processId;
    private int totalSamples;
    private float totalUsedMemory;
    private float totalCPUUsage;
    private String totalCPUTime;

    public StatisticsGenerator (int processId)
    {
        this.processId = Integer.toString(processId);
        totalSamples = 0;
        totalUsedMemory = 0;
        totalCPUUsage = 0;
    }

    public void run ()
    {
        try
        {
            String psCommand = "ps aux";
            Process psAux = Runtime.getRuntime().exec(psCommand);
            psAux.waitFor();
            BufferedReader stdOutStream = new BufferedReader(new
                 InputStreamReader(psAux.getInputStream()));

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
                    totalCPUUsage += Float.parseFloat(psAuxOutput[outIndex]);
                    outIndex++;
                    outIndex  = indexOfNextNonEmpty(outIndex, psAuxOutput);
                    // Obtain Used Memory percentage
                    totalUsedMemory += Float.parseFloat(psAuxOutput[outIndex]);
                    // Obtain System Time
                    final int COLUMNS_TIL_TIME = 6;
                    for(int colI = 0; colI < COLUMNS_TIL_TIME; colI++)
                        outIndex = indexOfNextNonEmpty(outIndex + 1, psAuxOutput);
                    totalCPUTime = psAuxOutput[outIndex];
                    totalSamples++;
                }
            }
        }
        catch(Exception ex)
        {   }
    }

    public int indexOfNextNonEmpty(int index, String[] array)
    {
        while(array[index].isEmpty())
            index++;
        return index;
    }

    public float getCPUUsagePercentage()
    {
        if(totalSamples > 0)
            return totalCPUUsage / totalSamples;
        else return 0;
    }

    public float getUsedMemoryPercentage()
    {
        if(totalSamples > 0)
            return totalUsedMemory / totalSamples;
        else return 0;
    }

    public int getCPUTimeSeconds()
    {
        if(totalSamples > 0)
        {
            String[] times = totalCPUTime.split(":");
            return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
        }
        else return 0;
    }

}
