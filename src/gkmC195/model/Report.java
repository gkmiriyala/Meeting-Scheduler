/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class Report {    
    private String apptMonth;
    private String apptClassification;    
    private String apptCountPerClassification;

    public Report(String apptMonth, String apptClassification, String apptCountPerClassification) {
        this.apptMonth = apptMonth;
        this.apptClassification = apptClassification;
        this.apptCountPerClassification = apptCountPerClassification;
    }
    
    public void setApptMonth(String apptMonth) {
        this.apptMonth = apptMonth;
    }

    public void setApptClassification(String apptClassification) {
        this.apptClassification = apptClassification;
    }

    public void setApptCountPerClassification(String apptCountPerClassification) {
        this.apptCountPerClassification = apptCountPerClassification;
    }  
    
    public String getApptMonth() {
        return apptMonth;
    }
    
    public String getApptClassification() {
        return apptClassification;
    }

    public String getApptCountPerClassification() {
        return apptCountPerClassification;
    } 
}
