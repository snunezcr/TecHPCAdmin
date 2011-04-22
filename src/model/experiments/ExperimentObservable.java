/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;
import java.util.Observable;

/**
 *
 * @author cfernandez
 */
public class ExperimentObservable extends Observable
{
    public void NotifiyExperimentManager(Object value)
    {
        setChanged();
        notifyObservers(value);
    }
}
