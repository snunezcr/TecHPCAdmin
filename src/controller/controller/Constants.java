/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

/**
 * Contains constants which are used ONLY by the view
 * @author rdinarte
 */
public class Constants {

    // Common
    // -------------------------------------------------------------------------
    public static final String DisabledControl = "disabled='disabled'";
    public static final String BooleanTrueText = "on";
    public static final String ExperimentHash = "ExperimentHash";
    public static final String ExperimentId = "ExpId";
    public static final String ExecutionError = "ExecutionError";

    public static final String IntegerTypeName = "int";
    public static final String CharTypeName = "char";
    public static final String FloatTypeName = "float";
    public static final String StringTypeName = "string";
    
    public static final String AdminRole = "Administrator";

    // Page names
    // -------------------------------------------------------------------------
    /**
     * The name of the page to display experiment details
     */
    public static final String ExperimentDetailsPage = "experiment-details.jsp";
    
    public static final String DisplayExperimentPage =
        "normal/" + ExperimentDetailsPage + "?" + ExperimentId + "=0";

    // Error messages
    // -------------------------------------------------------------------------
    /**
     * Error message reported to the user when an unexpected error occurs while
     * trying to get his applications
     */
    public static final String GetApplicationsErrorMessage = "No se pudieron obtener sus "
            + "aplicaciones.<br />Por favor contacte al administrador del sistema.";

    public static final String ZeroApplicationsErrorMessage = "Debe instalar o subir aplicaciones"
            + "antes de crear un experimento.";

    public static final String GetRolesErrorMessage = "No se pudieron obtener los roles de usuario."
            + "<br />Por favor contacte al administrador del sistema.";

    public static final String ZeroTypesErrorMessage = "No hay tipos de parámetros para el sistema."
            + "<br />Por favor contacte al administrador del sistema.";

    public static final String NewExperimentIncompleteInfoErrorMessage = "Debe completar el nombre "
            + "y descripción del experimento.";

    public static final String NewUserIncompleteInfoErrorMessage = "Debe completar todos los datos "
            + "para poder crear el usuario.";

    public static final String BadPasswordConfirmation = "La contraseña y su confirmación no "
            + "coinciden.";

    public static final String UploadProgramaIncompleteInfoErrorMessage = "Debe completar todos los"
            + " datos antes de subir el programa.";

    public static final String IncompleteParallelInfoErrorMessage = "Si escoge ejecución paralela "
            + "debe completar el directorio compartido de trabajo y el middleware.";

    public static final String IncompleteParamErrorMessage = "Debe completar todos los datos del "
            + "parámetro.";

    /**
     * Error message reported to the user when an unexpected error occurs while
     * trying to get the parameter types
     */
    public static final String GetTypesErrorMessage = "No se pudieron obtener los "
            + "tipos de parámetros.<br />Por favor contacte al administrador del sistema.";

    /**
     * Error message reported to the user when an experiment is tried to be started
     * but it is already running and has not finished its execution.
     */
    public static final String AlreadyRunningErrorMessage = "El experimento ya"
            + "está corriendo, si quiere volver a correrlo primero deténgalo.";

    /**
     * Error message reported to the user when an experiment is tried to be stopped
     * but it is not currently executing.
     */
    public static final String NotRunningErrorMessage = "El experimento ya"
            + "terminó su ejecución o no está corriendo.";

    /**
     * Error message reported to the user when an experiment is tried to be stopped
     * but it is not currently executing.
     */
    public static final String ExperimentStartFailed = "No se puedo iniciar la"
            + "ejecución del experimento.";

    /**
     * Error message reported to the user when an experiment is tried to be stopped
     * but it is not currently executing.
     */
    public static final String ExperimentStopFailed = "No se pudo detener la"
            + "ejecución del experimento.";

    /**
     * Error message reported to the user when an experiment is tried to be stopped
     * but it is not currently executing.
     */
    public static final String ExperimentDoesNotExist = "El experimento solicitado"
            + "no existe.";
    /**
     * Error message reported to the user when an experiment has corrupted information
     */
    public static final String ExperimentCorruptInfo = "Experimentos tienen información"
            + "corrupta (problemas en parámetros o información histórica).";

}
