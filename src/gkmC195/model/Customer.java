/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class Customer {
    
    private String custID;
    private String custName;
    private String custPhone;
    private String custAddr;
    private String custAddr2;
    private CustomerCity custCity;
    private String custCtry;
    private String custPstCd;
    
    
    public Customer(){
    }
    
    public Customer(String custID, String custName, String custPhone, String custAddr, String custAddr2, CustomerCity custCity, String custPstCd, String custCtry) {
        this.custID = custID;
        this.custName = custName;
        this.custPhone = custPhone;
        this.custAddr = custAddr;
        this.custAddr2 = custAddr2;
        this.custCity = custCity;
        this.custCtry = custCtry;
        this.custPstCd = custPstCd;
    }
    
    public Customer (String custID, String custName){
        this.custID = custID;
        this.custName = custName;
    }
    
    //Setters
    public void setCustID(String custID) {
        this.custID = custID;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }
    
    public void setCity(CustomerCity custCity) {
        this.custCity = custCity;
    }
    
    public void setCountry(String custCtry) {
        this.custCtry = custCtry;
    }
    
    //Getters
    public String getCustId() {
        return custID;
    }

    public String getCustName() {
        return custName;
    }

    public String getCustPhone() {
        return custPhone;
    }   
    
    public String getCustAddr() {
        return custAddr;
    }

    public String getCustAddr2() {
        return custAddr2;
    }

    public int getCityId(CustomerCity object) {
        return object.getCustCityId();
    }
    
    public CustomerCity getCustCity() {
        return custCity;
    }

    public String getCustPstCd() {
        return custPstCd;
    }
    
    public String getCustCtry() {
        return custCtry;
    }

    @Override
    public String toString() {
        return custName;
    } 
}
