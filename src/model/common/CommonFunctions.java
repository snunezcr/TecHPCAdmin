/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import controller.Constants;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.text.SimpleDateFormat;

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

    public static <T> ServiceResult<T> CreateErrorServiceResult(final Exception exception)
    {
        ServiceResult<T> result = new ServiceResult<T>();
        result.AddError(exception.getMessage());
        return result;
    }

    public static <T> ServiceResult<T> CreateErrorServiceResult(final ServiceResult original)
    {
        ServiceResult<T> result = new ServiceResult<T>();
        for(String errorString : original.getErrors())
            result.AddError(errorString);
        return result;
    }

    public static int LongToMaxInt(final long l)
    {
        if(l > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        return (int)l;
    }

    public static String[] ListExtensionFiles(final String path, final String extension)
    {
        File dir = new File(path);
        FilenameFilter fileFilter = new FilenameFilter() {
            public boolean accept(File file, String string) {
                return string.toLowerCase().endsWith(extension);
            }
        };
        return dir.list(fileFilter);
    }

    public static String[] SearchFile(final String path, final String name)
    {
        File dir = new File(path);
        FilenameFilter fileFilter = new FilenameFilter() {
            public boolean accept(File file, String string) {
                return string.toLowerCase().endsWith(name);
            }
        };
        return dir.list(fileFilter);
    }

    public static String GetDateText(final Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss");
        return format.format(date);
    }

}
