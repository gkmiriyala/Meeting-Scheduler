/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.miscellanious;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Ganesh
 */

public class MySQLConnection {
    
    private static Connection mysqlDatabase;
    
    public MySQLConnection(){}
    
 //Server name:  52.206.157.109 
 //Database name:  U051sI
 //Username:  U051sI
 //Password:  53688409050
    
    public static void initialize(){
        try{
            Class.forName("com.mysql.jdbc.Driver");  
            mysqlDatabase = DriverManager.getConnection("jdbc:mysql://52.206.157.109:3306/U051sI", "U051sI", "53688409050");;
        }
        catch (ClassNotFoundException ce){
            ce.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static Connection getDBConnection(){
        return mysqlDatabase;
    }

    public static void closeDBConnection(){
        try{
            mysqlDatabase.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }  
}
