/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class CustomerCity {
    
    private int custCityID;
    private String custCity;
    private int custCtryID;

    public CustomerCity() {
    }

    public CustomerCity(int custCityID) {
        this.custCityID = custCityID;
    }

    public CustomerCity(int custCityID, String custCity, int custCtryID) {
        this.custCityID = custCityID;
        this.custCity = custCity;
        this.custCtryID = custCtryID;
    }
    
    public CustomerCity(int custCityID, String custCity) {
        this.custCityID = custCityID;
        this.custCity = custCity;
    }

    public void setCustCityId(Integer custCityID) {
        this.custCityID = custCityID;
    }

    public void setCustCity(String custCity) {
        this.custCity = custCity;
    }

    public void setCustCtryID(int custCtryID) {
        this.custCtryID = custCtryID;
    }
    
    public int getCustCityId() {
        return custCityID;
    }

    public String getCustCity() {
        return custCity;
    }

    public int getCustCtryID() {
        return custCtryID;
    }

    @Override
    public String toString() {
        return custCity;
    }
}
