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
public class ProcessVerifier extends Thread
{
    // Attributes
    // -------------------------------------------------------------------------
    private Process process;
    private Observable obs;
    private UserExperimentMapping usrExpMap;
    private StatisticsGenerator gen;

    // Constructor
    // -------------------------------------------------------------------------
    public ProcessVerifier(Process process, Observable obs, UserExperimentMapping usrExpMap)
    {
        this.process = process;
        this.obs = obs;
        this.usrExpMap = usrExpMap;
    }

    @Override
    public void run()
    {
        try
        {
            gen = new StatisticsGenerator(
                    LinuxUtilities.GetInstance().GetUnixProcessPid(process));
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

    public float getCPUUsagePercentage()
    {
        if(gen != null)
            return gen.getCPUUsagePercentage();
        else return 0;
    }

    public float getUsedMemoryPercentage()
    {
        if(gen != null)
            return gen.getUsedMemoryPercentage();
        else return 0;
    }

    public int getCPUTimeSeconds()
    {
        if(gen != null)
            return gen.getCPUTimeSeconds();
        else return 0;
    }
}
