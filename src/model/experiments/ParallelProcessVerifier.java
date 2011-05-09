/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;
import common.LinuxUtilities;
import java.util.Observable;
import java.util.Timer;
import model.UserExperimentMapping;

/**
 *
 * @author cfernandez
 */
public class ParallelProcessVerifier extends Thread
{
    // Attributes
    // -------------------------------------------------------------------------
    private Process process;
    private Observable obs;
    private UserExperimentMapping usrExpMap;
    private ParallelStatisticsGenerator gen;
    private String hosts[];

    // Constructor
    // -------------------------------------------------------------------------
    public ParallelProcessVerifier(Process process, Observable obs,
            UserExperimentMapping usrExpMap, String[] hosts)
    {
        this.process = process;
        this.obs = obs;
        this.usrExpMap = usrExpMap;
        this.hosts = hosts;
    }

    @Override
    public void run()
    {
        try
        {
            gen = new ParallelStatisticsGenerator(
                    LinuxUtilities.GetInstance().GetUnixProcessPid(process), hosts);
            Timer statsTimer = new Timer();
            long oneSecond = 1000;
            statsTimer.scheduleAtFixedRate(gen, 0, oneSecond);
            process.waitFor();
            statsTimer.cancel();
            ExperimentObservable expObs = (ExperimentObservable) obs;
            expObs.NotifiyExperimentManager(usrExpMap);
        }
        catch(InterruptedException ex)
        {
            process.destroy();
        }
    }

    public float[] getCPUUsagePercentage()
    {
        if(gen != null)
            return gen.getCPUUsagePercentage();
        else return new float[hosts.length];
    }

    public float[] getUsedMemoryPercentage()
    {
        if(gen != null)
            return gen.getUsedMemoryPercentage();
        else return new float[hosts.length];
    }

    public int[] getCPUTimeSeconds()
    {
        if(gen != null)
            return gen.getCPUTimeSeconds();
        else return new int[hosts.length];
    }

    public String[] getHosts()
    {
        return hosts;
    }
}
