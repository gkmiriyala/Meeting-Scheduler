/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class ApplicationUser {
    private int applicationUserID;
    private String applicationUsername;
    private String applicationUserPassword;

    public ApplicationUser(int applicationUserID, String applicationUsername, String applicationUserPassword) {
        this.applicationUserID = applicationUserID;
        this.applicationUsername = applicationUsername;
        this.applicationUserPassword = applicationUserPassword;
    }

    public ApplicationUser() {
        
    }
    
    public void setApplicationUserID(int applicationUserID) {
        this.applicationUserID = applicationUserID;
    }

    public void setApplicationUsername(String applicationUsername) {
        this.applicationUsername = applicationUsername;
    }

    public void setApplicationUserPassword(String applicationUserPassword) {
        this.applicationUserPassword = applicationUserPassword;
    }  
    
    public int getApplicationUserID() {
        return applicationUserID;
    }
    
    public String getApplicationUsername() {
        return applicationUsername;
    }

    public String getApplicationUserPassword() {
        return applicationUserPassword;
    }    
}
