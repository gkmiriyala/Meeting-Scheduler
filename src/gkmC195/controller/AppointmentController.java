/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gkmC195.controller;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import gkmC195.miscellanious.MySQLConnection;
import gkmC195.main.MainApp;
import gkmC195.model.Appointment;
import gkmC195.model.Customer;
import gkmC195.model.ApplicationUser;

/**
 * @author Ganesh
 */

public class AppointmentController {
     
    @FXML
    private TableView<Appointment> apptTableView;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> startApptColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> endApptColumn;
    
    @FXML
    private TableColumn<Appointment, String> titleApptColumn;

    @FXML
    private TableColumn<Appointment, String> typeApptColumn;

    @FXML
    private TableColumn<Appointment, Customer> customerApptColumn;

    @FXML
    private TableColumn<Appointment, String> consultantApptColumn;

    @FXML
    private RadioButton weekRadioButton;

    @FXML
    private RadioButton monthRadioButton;
    
    @FXML
    private ToggleGroup apptToggleGroup;
    
    private MainApp mainApp;
    private ApplicationUser currentUser;
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final ZoneId newzid = ZoneId.systemDefault();
    ObservableList<Appointment> apptList;
       
    public void setAppointmentScreen(MainApp mainApp, ApplicationUser currentUser) {
	this.mainApp = mainApp;
        this.currentUser = currentUser;      
        apptToggleGroup = new ToggleGroup();
        this.weekRadioButton.setToggleGroup(apptToggleGroup);
        this.monthRadioButton.setToggleGroup(apptToggleGroup);
        titleApptColumn.setCellValueFactory(new PropertyValueFactory<>("apptName"));
        typeApptColumn.setCellValueFactory(new PropertyValueFactory<>("apptClassification"));
        startApptColumn.setCellValueFactory(new PropertyValueFactory<>("apptStartTime"));
        endApptColumn.setCellValueFactory(new PropertyValueFactory<>("apptEndTime"));
        customerApptColumn.setCellValueFactory(new PropertyValueFactory<>("apptCustomer"));
        consultantApptColumn.setCellValueFactory(new PropertyValueFactory<>("applicationUser"));       
        apptList = FXCollections.observableArrayList();
        populateAppointmentList();
        apptTableView.getItems().setAll(apptList);        
    }
    
    @FXML
    void handleApptMonth(ActionEvent event) {   
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Month = now.plusMonths(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(apptList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getApptStartTime(), timeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });
        apptTableView.setItems(filteredData);

    }
    
    @FXML
    void handleApptWeek(ActionEvent event) { 
        LocalDate now = LocalDate.now();
        LocalDate nowPlus7 = now.plusDays(7);
        FilteredList<Appointment> filteredData = new FilteredList<>(apptList);
        
        //Filters to show appointments from current date to a week out
        filteredData.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getApptStartTime(), timeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus7);
        });
        apptTableView.setItems(filteredData);
    }
       
    @FXML
    void handleDeleteAppt(ActionEvent event) {
        Appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();   
        if (selectedAppointment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + selectedAppointment.getApptName() + " scheduled for " + selectedAppointment.getApptStartTime() + "?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                deleteAppointment(selectedAppointment);
                mainApp.displayAppt(currentUser);
                }
            );
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment selected for Deletion");
            alert.setContentText("Please select an Appointment in the Table to delete");
            alert.showAndWait();
        }
    }

    @FXML
    void handleEditAppt(ActionEvent event) {
        Appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            boolean okClicked = mainApp.displayEditAppt(selectedAppointment, currentUser);
            mainApp.displayAppt(currentUser);     
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment selected");
            alert.setContentText("Please select an Appointment in the Table");
            alert.showAndWait();
        }
    }

    @FXML
    void handleNewAppt(ActionEvent event) throws IOException{
        boolean okClicked = mainApp.displayNewAppt(currentUser);
        mainApp.displayAppt(currentUser);
    }
    
    private void populateAppointmentList() {
        try{                     
        PreparedStatement statement = MySQLConnection.getDBConnection().prepareStatement(
        "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, "
                + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                + "FROM appointment, customer "
                + "WHERE appointment.customerId = customer.customerId "
                + "ORDER BY `start`");
            ResultSet rs = statement.executeQuery();  
            while (rs.next()) {      
                String tAppointmentId = rs.getString("appointment.appointmentId");
                Timestamp tsStart = rs.getTimestamp("appointment.start");
                ZonedDateTime newzdtStart = tsStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
        	ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(newzid);
                Timestamp tsEnd = rs.getTimestamp("appointment.end");
                ZonedDateTime newzdtEnd = tsEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
        	ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(newzid);
                String tTitle = rs.getString("appointment.title");           
                String tType = rs.getString("appointment.description");            
                Customer tCustomer = new Customer(rs.getString("appointment.customerId"), rs.getString("customer.customerName"));              
                String tUser = rs.getString("appointment.createdBy");                  
                apptList.add(new Appointment(tAppointmentId, tTitle, tType, newLocalStart.format(timeDTF), newLocalEnd.format(timeDTF), tCustomer, tUser));               
            }           
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void deleteAppointment(Appointment appointment) {
        try{           
            PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement("DELETE appointment.* FROM appointment WHERE appointment.appointmentId = ?");
            pst.setString(1, appointment.getApptId()); 
            pst.executeUpdate();  
                
        } 
        catch(SQLException e){
            e.printStackTrace();
        }       
    }
}
