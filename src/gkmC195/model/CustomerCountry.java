/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class CustomerCountry {
    
    private Integer custCtryID;
    private String custCtry;

    public CustomerCountry() {
    }

    public CustomerCountry(Integer custCtryID) {
        this.custCtryID = custCtryID;
    }

    public CustomerCountry(Integer custCtryID, String custCtry) {
        this.custCtryID = custCtryID;
        this.custCtry = custCtry;
    }
    
    public void setCustCtryID(Integer custCtryID) {
        this.custCtryID = custCtryID;
    }

    public void setCustCtry(String custCtry) {
        this.custCtry = custCtry;
    }
    
    public Integer getCustCtryID() {
        return custCtryID;
    }

    public String getCustCtry() {
        return custCtry;
    }  

    @Override
    public String toString() {
        return "model.Country[ countryId=" + custCtryID + " ]";
    }
}
