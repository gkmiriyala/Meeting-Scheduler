package gkmC195.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import gkmC195.main.MainApp;
import gkmC195.model.ApplicationUser;


public class MenuController {
    
    @FXML
    private MenuItem logout;
    
    private MainApp mainApp;
    private ApplicationUser currUser;
    
    public MenuController() {
        
    }
    
    public void displayMenu(MainApp mainApp, ApplicationUser currUser) {
	this.mainApp = mainApp;
        this.currUser = currUser;
        logout.setText("Logout: " + currUser.getApplicationUsername());
    }    

    @FXML
    void DISPLAY_APPOINTMENTS_HANDLER(ActionEvent event) {
        mainApp.displayAppt(currUser);
    }

    @FXML
    void DISPLAY_CUSTOMERS_HANDLER(ActionEvent event) {
        mainApp.displayCustomer(currUser);
    }
    
    @FXML
    void DISPLAY_REPORTS_HANDLER(ActionEvent event) {
        mainApp.displayReports(currUser);

    }
    
    @FXML
    void LOGOUT_HANDLER(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are you sure you want logout?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> mainApp.displayLogin());
    }
    
    @FXML
    void EXIT_HANDLER(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are you sure you want close the program?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent((ButtonType response) -> {
                Platform.exit();
                System.exit(0);
                }
            );     
    }    
}
