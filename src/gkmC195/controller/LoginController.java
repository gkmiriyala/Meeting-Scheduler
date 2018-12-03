package gkmC195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import gkmC195.miscellanious.MySQLConnection;
import gkmC195.main.MainApp;
import gkmC195.model.Appointment;
import gkmC195.model.Customer;
import gkmC195.model.ApplicationUser;
import gkmC195.miscellanious.ApplicationLogs;

public class LoginController {
        
    @FXML
    private Text applicationName;
    
    @FXML
    private Label error;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Text textUsername;

    @FXML
    private Text textPassword;

    @FXML
    private Button textSignIn;

    @FXML
    private Button textCancel;
    
    private MainApp mainApp;
    ResourceBundle rb = ResourceBundle.getBundle("login", Locale.getDefault());
    private final ZoneId newzid = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    ApplicationUser user = new ApplicationUser();
    ObservableList<Appointment> reminderList;
    private final static Logger LOGGER = Logger.getLogger(ApplicationLogs.class.getName());

    public LoginController() {
    }

    public void displayLogin(MainApp mA) {
	this.mainApp = mA;
        reminderList = FXCollections.observableArrayList();
        applicationName.setText(rb.getString("title"));
        textUsername.setText(rb.getString("username"));
        textPassword.setText(rb.getString("password"));
        textSignIn.setText(rb.getString("signin"));
        textCancel.setText(rb.getString("cancel"));
    }
    
    @FXML
    void SIGN_IN_HANDLER(ActionEvent ev) {
        String usnm = username.getText();
        String pw = password.getText();        
        if(usnm.length()==0 || pw.length()==0)
            error.setText(rb.getString("empty"));
        else{
            ApplicationUser applicationUserValidation = confirmValidApplicationUser(usnm,pw); 
            if (applicationUserValidation == null) {
                error.setText(rb.getString("incorrect"));
                return;
            }
            getAppointmentListFromDB();
            displayFifteenMinReminder();
            mainApp.displayMenu(applicationUserValidation);
            mainApp.displayAppt(applicationUserValidation);
            LOGGER.log(Level.INFO, "{0} logged in", applicationUserValidation.getApplicationUsername());       
        }
    }

    ApplicationUser confirmValidApplicationUser(String usnm,String pw) {
        try{           
            PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement("SELECT * FROM user WHERE userName=? AND password=?");
            pst.setString(1, usnm); 
            pst.setString(2, pw);
            ResultSet rs = pst.executeQuery();                        
            if(rs.next()){
                user.setApplicationUsername(rs.getString("userName"));
                user.setApplicationUserPassword(rs.getString("password"));
                user.setApplicationUserID(rs.getInt("userId"));
            }
            else {
                return null;    
            }               
        } 
        catch(SQLException e){
            e.printStackTrace();
        }       
        return user;
    }
    
    @FXML
    void CANCEL_HANDLER(ActionEvent event) {
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
    
    private void displayFifteenMinReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15Min = now.plusMinutes(15);
        FilteredList<Appointment> reminderListFiltered = new FilteredList<>(reminderList);
        reminderListFiltered.setPredicate(row -> {
            LocalDateTime rowDate = LocalDateTime.parse(row.getApptStartTime(), timeDTF);
            return rowDate.isAfter(now.minusMinutes(1)) && rowDate.isBefore(nowPlus15Min);
        });
        if (!reminderListFiltered.isEmpty()) {
            String type = reminderListFiltered.get(0).getApptClassification();
            String customer =  reminderListFiltered.get(0).getApptCustomer().getCustName();
            String start = reminderListFiltered.get(0).getApptStartTime();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Reminder");
            alert.setHeaderText("Appointment Reminder within the next 15 minutes.");
            alert.setContentText("Your upcoming " + type + " appointment with " + customer +
                " is currently set for " + start + ".");
            alert.showAndWait();
        } 
    }
    
    private void getAppointmentListFromDB() {
        try{           
        PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement(
        "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, "
                + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                + "FROM appointment, customer "
                + "WHERE appointment.customerId = customer.customerId AND appointment.createdBy = ? "
                + "ORDER BY `start`");
            pst.setString(1, user.getApplicationUsername());
            ResultSet results = pst.executeQuery();
            while (results.next()) { 
                String apptId = results.getString("appointment.appointmentId");
                Timestamp tsApptStartTime = results.getTimestamp("appointment.start");
                ZonedDateTime newzdtApptStartTime = tsApptStartTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        	ZonedDateTime newLocalApptStartTime = newzdtApptStartTime.withZoneSameInstant(newzid);
                Timestamp tsApptEndTime = results.getTimestamp("appointment.end");
                ZonedDateTime newzdtApptEndTime = tsApptEndTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        	ZonedDateTime newLocalApptEndTime = newzdtApptEndTime.withZoneSameInstant(newzid);
                String apptName = results.getString("appointment.title");
                String apptClassification = results.getString("appointment.description");
                Customer apptCustomer = new Customer(results.getString("appointment.customerId"), results.getString("customer.customerName"));
                String applicationUser = results.getString("appointment.createdBy");         
                reminderList.add(new Appointment(apptId, apptName, apptClassification, newLocalApptStartTime.format(timeDTF), newLocalApptEndTime.format(timeDTF), apptCustomer, applicationUser));   
            }     
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
