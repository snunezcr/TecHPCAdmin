/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;
import java.util.Date;

/**
 *
 * @author cfernandez
 */
public class ExperimentExecution
{
    // Attributes
    // -------------------------------------------------------------------------
    private Date startDate;
    private Date endDate;

    // Constructor
    // -------------------------------------------------------------------------
    public ExperimentExecution(Date startDate, Date endDate) 
    {
        /* Para string:
            import java.text.SimpleDateFormat;
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            String current_time = format.format(date);
         */
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the date at which the execution of the experiment ended
     * @return Execution end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Returns the date at which the execution of the experiment started
     * @return Execution start date
     */
    public Date getStartDate() {
        return startDate;
    }


}
