/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;
import java.util.Observable;
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
            process.waitFor();
            ExperimentObservable expObs = (ExperimentObservable) obs;
            expObs.NotifiyExperimentManager(usrExpMap);
        }
        catch(InterruptedException ex)
        {
            process.destroy();
        }
    }
}
