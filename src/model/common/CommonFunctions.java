/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import controller.Constants;

/**
 * Contains some commonly used functions
 * @author rdinarte
 */
public class CommonFunctions {

    /**
     * Returns the boolean value of a checkbox
     * @param text The text sent to the request parameters by the checkbox
     * @return A value indicating whether it's checked or not
     */
    public static boolean GetValue(String text)
    {
        return Constants.BooleanTrueText.equalsIgnoreCase(text);
    }

    public static <T> ServiceResult<T> CreateErrorServiceResult(Exception exception)
    {
        ServiceResult<T> result = new ServiceResult<T>();
        result.AddError(exception.getMessage());
        return result;
    }

    public static <T> ServiceResult<T> CreateErrorServiceResult(ServiceResult original)
    {
        ServiceResult<T> result = new ServiceResult<T>();
        for(String errorString : original.getErrors())
            result.AddError(errorString);
        return result;
    }

    public static int LongToMaxInt(long l)
    {
        if(l > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        return (int)l;
    }

}
