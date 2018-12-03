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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import gkmC195.miscellanious.MySQLConnection;
import gkmC195.main.MainApp;
import gkmC195.model.Appointment;
import gkmC195.model.Report;
import gkmC195.model.Customer;
import gkmC195.model.ApplicationUser;

/**
 * @author Ganesh
 */

public class ReportsController {
    
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab schedTab;

    @FXML
    private TableView<Appointment> schedTableView;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> startSchedColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> endSchedColumn;
    
    @FXML
    private TableColumn<Appointment, String> titleSchedColumn;

    @FXML
    private TableColumn<Appointment, String> typeSchedColumn;

    @FXML
    private TableColumn<Appointment, Customer> customerSchedColumn;

    @FXML
    private Tab apptTab;

    @FXML
    private TableView<Report> apptTableView;

    @FXML
    private TableColumn<Report, String> monthColumn;

    @FXML
    private TableColumn<Report, String> typeColumn;

    @FXML
    private TableColumn<Report, String> typeAmount;

    @FXML
    private Tab custTab;
    
    @FXML
    private BarChart barChart;
    
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    
    private MainApp mainApp;
    private ObservableList<Report> apptList;
    private ObservableList<Appointment> schedule;
    private ObservableList<PieChart.Data> pieChartData;
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final ZoneId newzid = ZoneId.systemDefault();
    private ApplicationUser currentUser;
    
    public ReportsController() {  
    }
    
    public void displayReports(MainApp mainApp, ApplicationUser currentUser) {
        this.mainApp = mainApp;
        this.currentUser = currentUser;   
        populateApptClassificationList();
        populateCustByCityChart();
        populateAppointments();         
        startSchedColumn.setCellValueFactory(new PropertyValueFactory<>("apptStartTime"));
        endSchedColumn.setCellValueFactory(new PropertyValueFactory<>("apptEndTime"));
        titleSchedColumn.setCellValueFactory(new PropertyValueFactory<>("apptName"));
        typeSchedColumn.setCellValueFactory(new PropertyValueFactory<>("apptClassification"));
        customerSchedColumn.setCellValueFactory(new PropertyValueFactory<>("apptCustomer"));        
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("apptMonth"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("apptClassification"));
        typeAmount.setCellValueFactory(new PropertyValueFactory<>("apptCountPerClassification"));     
    }
    
    
    
    private void populateApptClassificationList() {
        apptList = FXCollections.observableArrayList();
        try{       
            PreparedStatement statement = MySQLConnection.getDBConnection().prepareStatement(
                "SELECT MONTHNAME(`start`) AS \"apptMonth\", description AS \"apptClassification\", COUNT(*) as \"apptCountPerClassification\" "
                + "FROM appointment "
                + "GROUP BY MONTHNAME(`start`), description");
            ResultSet rs = statement.executeQuery();   
            while (rs.next()) {   
                String month = rs.getString("apptMonth"); 
                String type = rs.getString("apptClassification");
                String amount = rs.getString("apptCountPerClassification");              
                apptList.add(new Report(month, type, amount));
            }          
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }  
        apptTableView.getItems().setAll(apptList);
    }
    
    private void populateCustByCityChart() {
        ObservableList<XYChart.Data<String, Integer>> data = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
            try { 
                PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement(
                    "SELECT city.city, COUNT(city) "
                    + "FROM customer, address, city "
                    + "WHERE customer.addressId = address.addressId "
                    + "AND address.cityId = city.cityId "
                    + "GROUP BY city"); 
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                        String city = rs.getString("city");
                        Integer count = rs.getInt("COUNT(city)");
                        data.add(new Data<>(city, count));
                }
            } 
            catch (SQLException sqe) {
                sqe.printStackTrace();
            } 
            catch (Exception e) {
                e.printStackTrace();
            }             
        series.getData().addAll(data);
        barChart.getData().add(series);
    }
    
    private void populateAppointments() {
        schedule = FXCollections.observableArrayList();     
        try{            
        PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement(
        "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, "
                + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                + "FROM appointment, customer "
                + "WHERE appointment.customerId = customer.customerId AND appointment.`start` >= CURRENT_DATE AND appointment.createdBy = ?"
                + "ORDER BY `start`");
            pst.setString(1, currentUser.getApplicationUsername());
            ResultSet rs = pst.executeQuery();         
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
                schedule.add(new Appointment(tAppointmentId, tTitle, tType, newLocalStart.format(timeDTF), newLocalEnd.format(timeDTF), tCustomer, tUser));
            }       
        } 
        catch (SQLException sqe) {
            sqe.printStackTrace();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        schedTableView.getItems().setAll(schedule);
    }  
}
