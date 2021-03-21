package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department department;
	
	private DepartmentService dService;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button bSave;
	
	@FXML
	private Button bCancel;
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService dService) {
		this.dService = dService;
	}
	
	@FXML
	public void onBSaveAction(ActionEvent event) {
		if(department == null) {
			throw new IllegalStateException("Department was null");
		}
		if(dService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			department = getFormData();
			dService.saveOrUpdate(department);
			Utils.currentStage(event).close();
		} catch (DbException e){
			Alerts.showAlert("Error savinf object", null, e.getMessage(), AlertType.ERROR);
		}
		
	};
	
	@FXML
	public void onBCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	};
	
	private Department getFormData() {
		Department nDepartment = new Department();
		nDepartment.setId(Utils.tryParseToInt(txtId.getText()));
		nDepartment.setName(txtName.getText());
		return nDepartment;
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.SetTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLenght(txtName, 30);
	}
	
	public void updateFormData() {
		if(department == null) {
			throw new IllegalStateException("Department was null.");
		}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}
	
	
	
}