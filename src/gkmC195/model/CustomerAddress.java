
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.model;

/**
 * @author Ganesh
 */

public class CustomerAddress {
    
    private String custPhone;
    private Integer custAddrID;
    private String custAddr;
    private String custAddr2;
    private int custCityID;
    private String custPstCd;

    public CustomerAddress() {
    }

    public CustomerAddress(Integer custAddrID) {
        this.custAddrID = custAddrID;
    }

    public CustomerAddress(String custPhone, Integer custAddrID, String custAddr, String custAddr2, int custCityID, String custPstCd) {
        this.custPhone = custPhone;       
        this.custAddrID = custAddrID;
        this.custAddr = custAddr;
        this.custAddr2 = custAddr2;
        this.custCityID = custCityID;
        this.custPstCd = custPstCd;
    }
 
    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }
    
    public void setCustAddrId(Integer custAddrID) {
        this.custAddrID = custAddrID;
    }

    public void setCustAddr(String custAddr) {
        this.custAddr = custAddr;
    }

    public void setCustAddr2(String custAddr2) {
        this.custAddr2 = custAddr2;
    }

    public void setCustCityId(int custCityID) {
        this.custCityID = custCityID;
    }

    public void setCustPstCd(String custPstCd) {
        this.custPstCd = custPstCd;
    }

    public String getCustPhone() {
        return custPhone;
    }
    
    public Integer getCustAddrId() {
        return custAddrID;
    }

    public String getCustAddr() {
        return custAddr;
    }

    public String getCustAddr2() {
        return custAddr2;
    }

    public int getCustCityId() {
        return custCityID;
    }

    public String getCustPstCd() {
        return custPstCd;
    }
}