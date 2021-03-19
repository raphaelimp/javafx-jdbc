package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentViewController implements Initializable {
	
	private DepartmentService dServoce;
	
	public void setDepartmentService(DepartmentService dService) {
		this.dServoce = dService;
	}
	
	@FXML
	private TableView<Department> tvDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tcId;
	
	@FXML
	private TableColumn<Department, String> tcName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("Hello Button!");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tvDepartment.prefHeightProperty().bind(stage.heightProperty() );
	}
	
	public void updateTableView() {
		if(dServoce == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = dServoce.findAll();
		obsList = FXCollections.observableArrayList(list);
		tvDepartment.setItems(obsList);
	}
	
}
