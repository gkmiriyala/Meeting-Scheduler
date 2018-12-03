/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import gkmC195.miscellanious.MySQLConnection;
import gkmC195.main.MainApp;
import gkmC195.model.Appointment;
import gkmC195.model.Customer;
import gkmC195.model.ApplicationUser;

/**
 * @author Ganesh
 */

public class EditApptController {

    @FXML
    private Label apptLabel;

    @FXML
    private TextField apptNameField;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private ComboBox<String> endTimeComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> apptClassificationComboBox;

    @FXML
    private Button apptSaveButton;

    @FXML
    private Button apptCancelButton;

    @FXML
    private TableView<Customer> customerSelectTableView;

    @FXML
    private TableColumn<Customer, String> custNameColumn;

    @FXML
    private TextField customerSearchField;

    private Stage apptStage;
    private MainApp mainApp;
    private boolean okClicked = false;
    private final ZoneId zid = ZoneId.systemDefault();
    private Appointment selectedAppt;
    private ApplicationUser currUser;
    
    private ObservableList<Customer> masterData = FXCollections.observableArrayList();
    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    ObservableList<Appointment> apptTimeList;

     public void displayAppt(Appointment appt) {      
        okClicked = true;
        selectedAppt = appt;
        String startTime = appt.getApptStartTime();
        LocalDateTime startTimeLDT = LocalDateTime.parse(startTime, dateDTF);
        String endTime = appt.getApptEndTime();
        LocalDateTime endTimeLDT = LocalDateTime.parse(endTime, dateDTF);        
        apptLabel.setText("Edit Appointment");
        apptNameField.setText(appt.getApptName());
        apptClassificationComboBox.setValue(appt.getApptClassification());
        customerSelectTableView.getSelectionModel().select(appt.getApptCustomer());
        datePicker.setValue(LocalDate.parse(appt.getApptStartTime(), dateDTF));
        startTimeComboBox.getSelectionModel().select(startTimeLDT.toLocalTime().format(timeDTF));
        endTimeComboBox.getSelectionModel().select(endTimeLDT.toLocalTime().format(timeDTF));
    }
 
    public void displayApptStage(Stage apptStage, ApplicationUser currUser) {
        this.apptStage = apptStage;
        this.currUser = currUser;       
        populateAppointmentClassficationList();
        custNameColumn.setCellValueFactory(new PropertyValueFactory<>("custName"));
        masterData = populateCustomerList();
        FilteredList<Customer> filteredData = new FilteredList<>(masterData, p -> true);
        
        // Set the filter Predicate whenever the filter changes.
        customerSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (customer.getCustName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(customerSelectTableView.comparatorProperty());
        customerSelectTableView.setItems(sortedData);
	LocalTime time = LocalTime.of(8, 0);
	do {
            startTimes.add(time.format(timeDTF));
            endTimes.add(time.format(timeDTF));
            time = time.plusMinutes(15);
	} 
        while(!time.equals(LocalTime.of(17, 15)));
            startTimes.remove(startTimes.size() - 1);
            endTimes.remove(0);      
        datePicker.setValue(LocalDate.now());
        startTimeComboBox.setItems(startTimes);
	endTimeComboBox.setItems(endTimes);
	startTimeComboBox.getSelectionModel().select(LocalTime.of(8, 0).format(timeDTF));
	endTimeComboBox.getSelectionModel().select(LocalTime.of(8, 15).format(timeDTF));
    }
    
    private void saveAppt() {
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);
        ZonedDateTime startUTC = startDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            	
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); //this value can be inserted into database
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime()); //this value can be inserted into database              
        try {
            PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement("INSERT INTO appointment "
            + "(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");           
            pst.setString(1, customerSelectTableView.getSelectionModel().getSelectedItem().getCustId());
            pst.setString(2, apptNameField.getText());
            pst.setString(3, apptClassificationComboBox.getValue());
            pst.setString(4, "");
            pst.setString(5, "");
            pst.setString(6, "");
            pst.setTimestamp(7, startsqlts);
            pst.setTimestamp(8, endsqlts);
            pst.setString(9, currUser.getApplicationUsername());
            pst.setString(10, currUser.getApplicationUsername());
            pst.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateAppt() {  
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);
        ZonedDateTime startUTC = startDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime());      
        try {
                PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement("UPDATE appointment "
                        + "SET customerId = ?, title = ?, description = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                        + "WHERE appointmentId = ?");
            
                pst.setString(1, customerSelectTableView.getSelectionModel().getSelectedItem().getCustId());
                pst.setString(2, apptNameField.getText());
                pst.setString(3, apptClassificationComboBox.getValue());
                pst.setTimestamp(4, startsqlts);
                pst.setTimestamp(5, endsqlts);
                pst.setString(6, currUser.getApplicationUsername());
                pst.setString(7, selectedAppt.getApptId());
                pst.executeUpdate();
            } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void populateAppointmentClassficationList() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll( "General", "Consultation", "Orientation", "New Account", "Follow Up", "Close Account");
        apptClassificationComboBox.setItems(typeList);
    }
    
    protected ObservableList<Customer> populateCustomerList() {     
        String tCustomerId;
        String tCustomerName;     
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try(           
            PreparedStatement statement = MySQLConnection.getDBConnection().prepareStatement(
            "SELECT customer.customerId, customer.customerName " +
            "FROM customer, address, city, country " +
            "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");
            ResultSet rs = statement.executeQuery();){
            while (rs.next()) {
                tCustomerId = rs.getString("customer.customerId");
                tCustomerName = rs.getString("customer.customerName");
                customerList.add(new Customer(tCustomerId, tCustomerName));
            }        
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        return customerList;
    }

    private boolean validateAppointment() {
        String title = apptNameField.getText();
        String type = apptClassificationComboBox.getValue();
        Customer customer = customerSelectTableView.getSelectionModel().getSelectedItem();
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), timeDTF);      
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);
        ZonedDateTime startUTC = startDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            	        
        String errorMessage = "";
        if (title == null || title.length() == 0) {
            errorMessage += "Please enter an Appointment title.\n"; 
        }
        if (type == null || type.length() == 0) {
            errorMessage += "Please select an Appointment type.\n";  
        } 
        if (customer == null) {
            errorMessage += "Please Select a Customer.\n"; 
        } 
        if (startUTC == null) {
            errorMessage += "Please select a Start time"; 
        }         
        if (endUTC == null) {
            errorMessage += "Please select an End time.\n"; 
            } 
            else if (endUTC.equals(startUTC) || endUTC.isBefore(startUTC)){
                errorMessage += "End time must be after Start time.\n";
            } 
            else try {
                if (checkApptTimeConflict(startUTC, endUTC)){
                    errorMessage += "Appointment times conflict with Consultant's existing appointments. Please select a new time.\n";
                }
            } 
            catch (SQLException ex) {
                Logger.getLogger(EditApptController.class.getName()).log(Level.SEVERE, null, ex);
            }
        if (errorMessage.length() == 0) {
            return true;
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(apptStage);
            alert.setHeaderText("Please correct invalid Appointment fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
    
    private boolean checkApptTimeConflict(ZonedDateTime newStart, ZonedDateTime newEnd) throws SQLException {
        String apptID;
        String consultant;
        if (checkOk()) {
            apptID = selectedAppt.getApptId();
            consultant = selectedAppt.getApplicationUser();
        } 
        else {
            apptID = "0";
            consultant = currUser.getApplicationUsername();
        }  
        try{     
            PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement(
            "SELECT * FROM appointment "
            + "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
            + "AND (createdBy = ? AND appointmentID != ?)");
            pst.setTimestamp(1, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(2, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setTimestamp(3, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(4, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setString(5, consultant);
            pst.setString(6, apptID);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return true;
            }
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void SAVE_HANDLER(ActionEvent event) {
        if (validateAppointment()){
            if (checkOk()) {
                updateAppt();            
            } 
            else {
                saveAppt();
            }
            apptStage.close();
        }    
    }

    @FXML
    private void CANCEL_HANDLER(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to Cancel?");
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> apptStage.close()); 
    }

    public boolean checkOk() {
        return okClicked;
    }    
}
