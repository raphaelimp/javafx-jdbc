package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {
	
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
	
	@FXML
	public void onBSaveAction() {
		System.out.println("BSave");
	};
	
	@FXML
	public void onBCancelAction() {
		System.out.println("BCancel");
	};
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.SetTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLenght(txtName, 30);
	}
	
	
	
}
