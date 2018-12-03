/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.main;

import gkmC195.miscellanious.MySQLConnection;
import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import gkmC195.model.Appointment;
import gkmC195.model.ApplicationUser;
import gkmC195.miscellanious.ApplicationLogs;
import gkmC195.controller.LoginController;
import gkmC195.controller.CustomerController;
import gkmC195.controller.AppointmentController;
import gkmC195.controller.MenuController;
import gkmC195.controller.EditApptController;
import gkmC195.controller.ReportsController;

/**
 * @author Ganesh
 * username: test
 * Password: test
 */

public class MainApp extends Application {
    
    private Stage primaryStage;
    private BorderPane menu;
    Locale locale = Locale.getDefault();
    private static Connection connection;

    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("GKMC195 Scheduling");
        displayLogin();
    }
    
    public static void main(String[] args) {
        MySQLConnection.initialize();
        connection = MySQLConnection.getDBConnection();
        ApplicationLogs.initialize();
        launch(args);
        MySQLConnection.closeDBConnection();
    }

    public void displayLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/Login.fxml"));
            AnchorPane login = (AnchorPane) loader.load();
            LoginController loginController = loader.getController();
            loginController.displayLogin(this);            
            Scene loginScene = new Scene(login);
            primaryStage.setScene(loginScene);
            primaryStage.show();

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void displayMenu(ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/Menu.fxml"));
            menu = (BorderPane) loader.load();
            Scene menuScene = new Scene(menu);
            primaryStage.setScene(menuScene);
            MenuController menuController = loader.getController();
            menuController.displayMenu(this, currUser);
            primaryStage.show();
        } 
        catch (IOException e) {
            e.getCause().printStackTrace();
        }
    }
    
    public void displayAppt(ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/Appointment.fxml"));
            AnchorPane appt = (AnchorPane) loader.load();
            menu.setCenter(appt);
            AppointmentController appointmentController = loader.getController();
            appointmentController.setAppointmentScreen(this, currUser);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean displayNewAppt(ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/EditAppt.fxml"));
            AnchorPane newAppt = (AnchorPane) loader.load();
            Stage newApptStage = new Stage();
            newApptStage.setTitle("New Appointment");
            newApptStage.initModality(Modality.WINDOW_MODAL);
            newApptStage.initOwner(primaryStage);
            Scene newApptScene = new Scene(newAppt);
            newApptStage.setScene(newApptScene);
            EditApptController editApptController = loader.getController();
            editApptController.displayApptStage(newApptStage, currUser);
            newApptStage.showAndWait();
            return editApptController.checkOk();
        } 
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean displayEditAppt(Appointment appt, ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/EditAppt.fxml"));
            AnchorPane editAppt = (AnchorPane) loader.load();
            Stage editApptStage = new Stage();
            editApptStage.setTitle("Edit Appointment");
            editApptStage.initModality(Modality.WINDOW_MODAL);
            editApptStage.initOwner(primaryStage);
            Scene editApptScene = new Scene(editAppt);
            editApptStage.setScene(editApptScene);
            EditApptController editApptController = loader.getController();
            editApptController.displayApptStage(editApptStage, currUser);
            editApptController.displayAppt(appt);
            editApptStage.showAndWait();
            return editApptController.checkOk();
        } 
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
  
    public void displayCustomer(ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/Customer.fxml"));
            AnchorPane customer = (AnchorPane) loader.load();
            menu.setCenter(customer);
            CustomerController customerController = loader.getController();
            customerController.setCustomerScreen(this, currUser);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void displayReports(ApplicationUser currUser) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/gkmC195/view/Reports.fxml"));
            TabPane reportsTab = (TabPane) loader.load();
            menu.setCenter(reportsTab);
            ReportsController reportsController = loader.getController();
            reportsController.displayReports(this, currUser);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
