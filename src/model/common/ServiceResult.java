/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is a container for the result of calling an operation. Methods
 * invoked from the view should return ServiceResult objects rather than other
 * classes
 * @author rdinarte
 */
public class ServiceResult<T> {

    // Enums
    // -------------------------------------------------------------------------
    /**
     * It is used to indicate if the operation was successful, failed, or there
     * was no data retrieved, inserted or updated
     */
    public enum OperationResult
    {
        Succeeded,
        Error,
        EmptyResult
    }

    // Attributes
    // -------------------------------------------------------------------------
    private OperationResult status = OperationResult.Succeeded;
    private T value = null;
    private List<String> errors = new LinkedList<String>();

    // Constructor
    // -------------------------------------------------------------------------
    /**
     * This constructor should be used if the operation didn't return or update any
     * information, or if an error occurred
     */
    public ServiceResult()
    {
        status = OperationResult.EmptyResult;
    }

    /**
     * This is the constructor for a successful operation
     */
    public ServiceResult(final T value)
    {
        this.value = value;
    }

    // Attribute accesors
    // -------------------------------------------------------------------------
    /**
     * Get the operation errors
     * @return the value of error
     */
    public String[] getErrors() {
        return errors.toArray(new String[errors.size()]);
    }

    /**
     * Get the result value of the operation
     * @return the value of value
     */
    public T getValue() {
        return value;
    }

    /**
     * Set the result value of the operation
     * @param value new value of value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Get the status of the operation
     * @return the value of status
     */
    public OperationResult getStatus() {
        return status;
    }

    /**
     * Set the status of the operation
     * @param status new value of status
     */
    public void setStatus(final OperationResult status) {
        this.status = status;
    }

    // Instance methods
    // -------------------------------------------------------------------------
    /**
     * Used to indicate that an error occurred and store the list of errors
     * @param error The error that occurred
     */
    public void AddError(final String error)
    {
        errors.add(error);
        status = OperationResult.Error;
    }

}
