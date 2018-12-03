/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class Appointment {
    
    private String apptID;
    private String apptName;
    private String apptClassification;
    private String apptStartTime;
    private String apptEndTime;
    private String applicationUser;
    private Customer apptCustomer;
    
    public Appointment() {
    }

    public Appointment(String apptID) {
        this.apptID = apptID;
    }
    
    public Appointment(String apptID, String apptName, String apptClassification, String apptStartTime, String apptEndTime, Customer apptCustomer, String applicationUser) {
        this.apptID = apptID;    
        this.apptName = apptName;
        this.apptClassification = apptClassification;
        this.apptStartTime = apptStartTime;
        this.apptEndTime = apptEndTime;
        this.apptCustomer = apptCustomer;
        this.applicationUser = applicationUser;        
    }
    
    public Appointment(String apptStartTime, String apptEndTime, String applicationUser) {
        this.apptStartTime = apptStartTime;
        this.apptEndTime = apptEndTime;
        this.applicationUser = applicationUser;
    }

    public void setApptID(String apptID) {
        this.apptID = apptID;
    }

    public void setApptName(String apptName) {
        this.apptName = apptName;
    }
    
    public void setApptClassification(String apptClassification) {
        this.apptClassification = apptClassification;
    }

    public void setApptStartTime(String apptStartTime) {
        this.apptStartTime = apptStartTime;
    }
    
    public void setApptEndTime(String apptEndTime) {
        this.apptEndTime = apptEndTime;
    }

    public void setCustomer(Customer customer) {
        this.apptCustomer = customer;
    }
    
    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }
    
    public String getApptId() {
        return apptID;
    }

    public String getApptName() {
        return apptName;
    }

    public String getApptClassification() {
        return apptClassification;
    }

    public String getApptStartTime() {
        return apptStartTime;
    }

    public String getApptEndTime() {
        return apptEndTime;
    }

    public Customer getApptCustomer() {
        return apptCustomer;
    }
    
    public String getApplicationUser() {
        return applicationUser;
    }
}
