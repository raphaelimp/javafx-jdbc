package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	private Seller seller;
	
	private SellerService sService;
	
	private DepartmentService dService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> cBDepartment;
	
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button bSave;
	
	@FXML
	private Button bCancel;
	
	private ObservableList<Department> obsList;
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public void setServices(SellerService sService, DepartmentService dService) {
		this.sService = sService;
		this.dService = dService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener dCListener) {
		dataChangeListeners.add(dCListener);
	}

	
	@FXML
	public void onBSaveAction(ActionEvent event) {
		if(seller == null) {
			throw new IllegalStateException("Seller was null");
		}
		if(sService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			seller = getFormData();
			sService.saveOrUpdate(seller);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} 
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e){
			Alerts.showAlert("Error savinf object", null, e.getMessage(), AlertType.ERROR);
		}
		
	};
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener l : dataChangeListeners) {
			l.onDataChanged();
		}
	}

	@FXML
	public void onBCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	};
	
	private Seller getFormData() {
		Seller nSeller = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		nSeller.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		nSeller.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return nSeller;
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.SetTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLenght(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLenght(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}
	
	public void updateFormData() {
		if(seller == null) {
			throw new IllegalStateException("Seller was null.");
		}
		txtId.setText(String.valueOf(seller.getId()));
		txtName.setText(seller.getName());
		txtEmail.setText(seller.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
		if(seller.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(seller.getDepartment() == null) {
			cBDepartment.getSelectionModel().selectFirst();
		} else {
			cBDepartment.setValue(seller.getDepartment());
		} 
	}
	
	public void loadAssociatedObjects() {
		List<Department> list = dService.findAll();
		obsList = FXCollections.observableArrayList(list);
		cBDepartment.setItems(obsList);
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override 
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
	}
	
}
