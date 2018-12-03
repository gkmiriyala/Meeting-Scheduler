/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.miscellanious;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Ganesh
 */

public class ApplicationLogs {
    
 private final static Logger APPLICATION_LOGGER = Logger.getLogger(ApplicationLogs.class.getName());
 private static FileHandler fileHandler = null;
 
 public static void initialize(){
    try {
    fileHandler = new FileHandler("SchedulingApp-Userlog.%u.%g.txt", 1024 * 1024, 10, true);
    } 
    catch (SecurityException | IOException e) {
        e.printStackTrace();
    }
    Logger applicationLogger = Logger.getLogger("");
    fileHandler.setFormatter(new SimpleFormatter());
    applicationLogger.addHandler(fileHandler);
    applicationLogger.setLevel(Level.INFO);
 }
}
