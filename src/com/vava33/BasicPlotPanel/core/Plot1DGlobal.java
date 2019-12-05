package com.vava33.BasicPlotPanel.core;

/**
 * Global parameters. 
 * Programs using the library should extend it with their specifics
 *
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import com.vava33.jutils.VavaLogger;

public final class Plot1DGlobal {

    private static boolean loggingConsole = false; //console
    private static boolean loggingFile = false; //file
    private static boolean loggingTA = true; //textArea -- NO ESCRIT AL FITXER DE CONFIGURACIO JA QUE VOLEM SEMPRE ACTIVAT
    private static String loglevel = "info"; //info, config, etc...
    public static VavaLogger log;
    
    //
    public static String[] lightColors = {"black","blue","red","green","magenta","cyan","pink","yellow"}; //8 colors
    public static String[] DarkColors = {"yellow","white","cyan","green","magenta","blue","red","pink"}; //8 colors

    //==========================================================================
    
    public static void initLogger(String name){
        log = new VavaLogger(name,loggingConsole,loggingFile,loggingTA);
        log.setLogLevel(loglevel);
        
        if (isAnyLogging()) {
            log.enableLogger(true);
        }else {
            log.enableLogger(false);
        }

    }
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name,loggingConsole,loggingFile,loggingTA);
        l.setLogLevel(loglevel);
        if (isAnyLogging()) {
            l.enableLogger(true);
        }else {
            l.enableLogger(false);
        }
        return l;
    }
    
    public static boolean isAnyLogging() {
        if (loggingConsole || loggingFile || loggingTA) return true;
        return false;
    }
    
    public static boolean isLoggingConsole() {
        return loggingConsole;
    }
    public static boolean isLoggingFile() {
        return loggingFile;
    }

    //returns true if logging is enabled and level is <= config
    public static boolean isDebug(){
        if (isAnyLogging()){
            if (loglevel.equalsIgnoreCase("config")||loglevel.equalsIgnoreCase("debug")||loglevel.equalsIgnoreCase("fine")||loglevel.equalsIgnoreCase("finest")){
                return true;
            }
        }
        return false;
    }

}
