package gkmC195.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import gkmC195.miscellanious.MySQLConnection;
import gkmC195.main.MainApp;
import gkmC195.model.CustomerCity;
import gkmC195.model.Customer;
import gkmC195.model.ApplicationUser;

public class CustomerController {

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;
    
    @FXML
    private TableColumn<Customer, String> cityColumn;
    
    @FXML
    private TableColumn<Customer, String> countryColumn;
    
    @FXML
    private TextField customerIdField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private ComboBox<CustomerCity> cityComboBox;

    @FXML
    private TextField address2Field;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField countryField;
    
    @FXML
    private ButtonBar newEditDeleteButtonBar;
    
    @FXML
    private Button customerSaveButton;
   
    @FXML
    private Button customerCancelButton;

    @FXML
    private Button newCustomerButton;

    @FXML
    private Button editCustomerButton;
    
    @FXML
    private Button deleteCustomerButton;
    
    private MainApp mainApp;
    private boolean editClicked = false;
    private Stage dialogStage;
    private ApplicationUser currentUser;
    
    public CustomerController() {
    }
    
    @FXML
    void NEW_CUSTOMER_HANDLER(ActionEvent event) {
        editClicked = false;
        enableCustomerFields();
        customerSaveButton.setDisable(false);
        customerCancelButton.setDisable(false);
        customerTable.setDisable(true);
        clearCustomerDetails();
        customerIdField.setText("Auto-Generated");
        newCustomerButton.setDisable(true);
        editCustomerButton.setDisable(true);
        deleteCustomerButton.setDisable(true);       
    }
    
    @FXML
    void EDIT_CUSTOMER_HANDLER(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();       
        if (selectedCustomer != null) {
            editClicked = true;
            enableCustomerFields();
            customerSaveButton.setDisable(false);
            customerCancelButton.setDisable(false);
            customerTable.setDisable(true);
            newCustomerButton.setDisable(true);
            editCustomerButton.setDisable(true);
            deleteCustomerButton.setDisable(true);
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer selected");
            alert.setContentText("Please select a Customer in the Table");
            alert.showAndWait();
        }       
    }

    @FXML
    void DELETE_CUSTOMER_HANDLER(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + selectedCustomer.getCustName() + "?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                deleteCustomer(selectedCustomer);
                mainApp.displayCustomer(currentUser);
                }
            );
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer selected for Deletion");
            alert.setContentText("Please select a Customer in the Table to delete");
            alert.showAndWait();
        }      
    }
   
    @FXML
    void SAVE_CUSTOMER_HANDLER(ActionEvent event) {
        if (validateCustomer()){
        customerSaveButton.setDisable(true);
        customerCancelButton.setDisable(true);
            customerTable.setDisable(false);
            if (editClicked == true) {
                updateCustomer();
            } 
            else if (editClicked == false){
                saveCustomer();
            }
            customerTable.getSelectionModel().clearSelection();    
            mainApp.displayCustomer(currentUser);
        } 
    }
    
    @FXML
    void CANCEL_CUSTOMER_HANDLER(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Are you sure you want to Cancel?");
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> {
            customerSaveButton.setDisable(true);
            customerCancelButton.setDisable(true);
            customerTable.setDisable(false);
            clearCustomerDetails();
            newCustomerButton.setDisable(false);
            editCustomerButton.setDisable(false);
            deleteCustomerButton.setDisable(false);
            editClicked = false;
            }
        );
        customerTable.getSelectionModel().clearSelection();
        mainApp.displayCustomer(currentUser);
    }
    
    public void setCustomerScreen(MainApp mainApp, ApplicationUser currentUser) {
	this.mainApp = mainApp;
        this.currentUser = currentUser;     
        customerSaveButton.setDisable(true);
        customerCancelButton.setDisable(true);
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("custName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("custPhone"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("custCity"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("custCtry"));      
        populateCityList();   
        disableCustomerFields();  
        cityComboBox.setConverter(new StringConverter<CustomerCity>() {
            @Override
            public String toString(CustomerCity object) {
            return object.getCustCity();
            }     

            @Override
            public CustomerCity fromString(String string) {
            return cityComboBox.getItems().stream().filter(ap -> 
                ap.getCustCity().equals(string)).findFirst().orElse(null);
            }
        });
        cityComboBox.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null)
                showCountry(newval.toString());
        });
        customerTable.getItems().setAll(populateCustomerList());          
        customerTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue)->showCustomerDetails(newValue));
    }
         
    @FXML
    private void showCustomerDetails(Customer selectedCustomer) {    
        customerIdField.setText(selectedCustomer.getCustId());
        nameField.setText(selectedCustomer.getCustName());
        addressField.setText(selectedCustomer.getCustAddr());
        address2Field.setText(selectedCustomer.getCustAddr2());
        cityComboBox.setValue(selectedCustomer.getCustCity());
        countryField.setText(selectedCustomer.getCustCtry());
        postalCodeField.setText(selectedCustomer.getCustPstCd());
        phoneField.setText(selectedCustomer.getCustPhone());
    }
    
    private void disableCustomerFields() {        
        nameField.setDisable(true);
        addressField.setDisable(true);
        address2Field.setDisable(true);
        cityComboBox.setDisable(true);
        countryField.setDisable(true);
        postalCodeField.setDisable(true);
        phoneField.setDisable(true);
    }
    
    private void enableCustomerFields() {       
        nameField.setDisable(false);
        addressField.setDisable(false);
        address2Field.setDisable(false);
        cityComboBox.setDisable(false);
        countryField.setDisable(false);
        postalCodeField.setDisable(false);
        phoneField.setDisable(false);
    }
    
    @FXML 
    private void clearCustomerDetails() {    
        customerIdField.clear();
        nameField.clear();
        addressField.clear();
        address2Field.clear();
        countryField.clear();
        postalCodeField.clear();
        phoneField.clear();
    }
    
    protected List<Customer> populateCustomerList() {      
        String custID;
        String custName;
        String custAddr;
        String custAddr2;
        CustomerCity custCity;
        String custCtry;
        String custPstCd;
        String custPhone;       
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try(          
            PreparedStatement statement = MySQLConnection.getDBConnection().prepareStatement(
            "SELECT customer.customerId, customer.customerName, address.address, address.address2, address.postalCode, city.cityId, city.city, country.country, address.phone " +
            "FROM customer, address, city, country " +
            "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId " +
            "ORDER BY customer.customerName");
                ResultSet rs = statement.executeQuery();){
           
            
            while (rs.next()) {
                custID = rs.getString("customer.customerId");
                custName = rs.getString("customer.customerName");
                custAddr = rs.getString("address.address");
                custAddr2 = rs.getString("address.address2");
                custCity = new CustomerCity(rs.getInt("city.cityId"), rs.getString("city.city"));
                custCtry = rs.getString("country.country");
                custPstCd = rs.getString("address.postalCode");
                custPhone = rs.getString("address.phone");
                customerList.add(new Customer(custID, custName, custPhone, custAddr, custAddr2, custCity, custPstCd, custCtry));
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
    
    protected void populateCityList() { 
    ObservableList<CustomerCity> cities = FXCollections.observableArrayList();  
    try(PreparedStatement statement = MySQLConnection.getDBConnection().prepareStatement("SELECT cityId, city FROM city LIMIT 100;");
        ResultSet rs = statement.executeQuery();){  
        while (rs.next()) {
            cities.add(new CustomerCity(rs.getInt("city.cityId"),rs.getString("city.city")));
        }
    } 
    catch (SQLException sqe) {
        sqe.printStackTrace();
    } 
    catch (Exception e) {
        e.printStackTrace();
    }
    cityComboBox.setItems(cities);
    }
    
    @FXML
    private void showCountry(String citySelection) {
        if (citySelection.equals("Hyderabad") || citySelection.equals("Mumbai")) {
            countryField.setText("India");
        } 
        else if (citySelection.equals("Seattle") || citySelection.equals("Austin") || citySelection.equals("New York")) {
            countryField.setText("United States");
        }
        else if (citySelection.equals("Vancouver")) {
            countryField.setText("Canada");
        }
    }
    
    private void saveCustomer() {
            try {
                PreparedStatement ps = MySQLConnection.getDBConnection().prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)",Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, addressField.getText());
                ps.setString(2, address2Field.getText());
                ps.setInt(3, cityComboBox.getValue().getCustCityId());
                ps.setString(4, postalCodeField.getText());
                ps.setString(5, phoneField.getText());
                ps.setString(6, currentUser.getApplicationUsername());
                ps.setString(7, currentUser.getApplicationUsername());
                boolean res = ps.execute();
                int newAddressId = -1;
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    newAddressId = rs.getInt(1);
                }    
                PreparedStatement psc = MySQLConnection.getDBConnection().prepareStatement("INSERT INTO customer "
                + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");  
                psc.setString(1, nameField.getText());
                psc.setInt(2, newAddressId);
                psc.setInt(3, 1);
                psc.setString(4, currentUser.getApplicationUsername());
                psc.setString(5, currentUser.getApplicationUsername());
                psc.executeUpdate();           
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
            }
    }
    
    private void deleteCustomer(Customer customer) {    
        try{           
            PreparedStatement pst = MySQLConnection.getDBConnection().prepareStatement("DELETE customer.*, address.* from customer, address WHERE customer.customerId = ? AND customer.addressId = address.addressId");
            pst.setString(1, customer.getCustId()); 
            pst.executeUpdate();   
        } 
        catch(SQLException e){
            e.printStackTrace();
        }       
    }

    private void updateCustomer() {
        try {
            PreparedStatement ps = MySQLConnection.getDBConnection().prepareStatement("UPDATE address, customer, city, country "
                    + "SET address = ?, address2 = ?, address.cityId = ?, postalCode = ?, phone = ?, address.lastUpdate = CURRENT_TIMESTAMP, address.lastUpdateBy = ? "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");
            ps.setString(1, addressField.getText());
            ps.setString(2, address2Field.getText());
            ps.setInt(3, cityComboBox.getValue().getCustCityId());
            ps.setString(4, postalCodeField.getText());
            ps.setString(5, phoneField.getText());
            ps.setString(6, currentUser.getApplicationUsername());
            ps.setString(7, customerIdField.getText());          
            int result = ps.executeUpdate();        
            PreparedStatement psc = MySQLConnection.getDBConnection().prepareStatement("UPDATE customer, address, city "
            + "SET customerName = ?, customer.lastUpdate = CURRENT_TIMESTAMP, customer.lastUpdateBy = ? "
            + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId");          
            psc.setString(1, nameField.getText());
            psc.setString(2, currentUser.getApplicationUsername());
            psc.setString(3, customerIdField.getText());
            int results = psc.executeUpdate();              
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean validateCustomer() {
        String name = nameField.getText();
        String address = addressField.getText();
        CustomerCity city = cityComboBox.getValue();
        String country = countryField.getText();
        String zip = postalCodeField.getText();
        String phone = phoneField.getText();   
        String errorMessage = "";
        if (name == null || name.length() == 0) {
            errorMessage += "Please enter the Customer's name.\n"; 
        }
        if (address == null || address.length() == 0) {
            errorMessage += "Please enter an address.\n";  
        } 
        if (city == null) {
            errorMessage += "Please Select a City.\n"; 
        } 
        if (country == null || country.length() == 0) {
            errorMessage += "No valid Country. Country set by City.\n"; 
        }         
        if (zip == null || zip.length() == 0) {
            errorMessage += "Please enter the Postal Code.\n"; 
        } 
        else if (zip.length() > 10 || zip.length() < 5){
            errorMessage += "Please enter a valid Postal Code.\n";
        }
        if (phone == null || phone.length() == 0) {
            errorMessage += "Please enter a Phone Number (including Area Code)."; 
        } 
        else if (phone.length() < 10 || phone.length() > 15 ){
            errorMessage += "Please enter a valid phone number (including Area Code).\n";
        }        
        if (errorMessage.length() == 0) {
            return true;
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid Customer fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
