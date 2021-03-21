package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentViewController implements Initializable, DataChangeListener{
	
	private DepartmentService dService;
	
	public void setDepartmentService(DepartmentService dService) {
		this.dService = dService;
	}
	
	@FXML
	private TableView<Department> tvDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tcId;
	
	@FXML
	private TableColumn<Department, String> tcName;
	
	@FXML
	private TableColumn<Department, Department> tcEdit;
	
	@FXML
	private TableColumn<Department, Department> tcRemove;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department department = new Department();
		createDialogForm(department, "/gui/DepartmentForm.fxml", parentStage);
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
		if(dService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = dService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tvDepartment.setItems(obsList);
		initEditButton();
		initRemoveButtons();
	}
	
	private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(department);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage diaologStage = new Stage();
			diaologStage.setTitle("Enter Department Data");
			diaologStage.setScene(new Scene(pane));
			diaologStage.setResizable(false);
			diaologStage.initOwner(parentStage);
			diaologStage.initModality(Modality.WINDOW_MODAL);
			diaologStage.showAndWait();
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	private void initEditButton() {
		tcEdit.setCellValueFactory(param -> 
			new ReadOnlyObjectWrapper<>(param.getValue()));
		tcEdit.setCellFactory(param -> 
			new TableCell<Department, Department>(){
				private final Button button = new Button("eidt");
				
				@Override
				protected void updateItem(Department obj, boolean empty) {
					super.updateItem(obj, empty);
					
					if(obj == null) {
						setGraphic(null);
						return;
					}
					
					setGraphic(button); 
						button.setOnAction(
							event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
					}
			});
	}
	
	private void initRemoveButtons() {
		tcRemove.setCellValueFactory(param -> 
			new ReadOnlyObjectWrapper<>(param.getValue()));
		tcRemove.setCellFactory(param -> 
			new TableCell<Department, Department>() {
				private final Button button = new Button("remove");
				
				@Override
				protected void updateItem(Department obj, boolean empty) {
					super.updateItem(obj, empty);
					
					if(obj == null) {
						setGraphic(null);
						return;
					}
					
					setGraphic(button);
					button.setOnAction(event -> removeEntity(obj));
				}
			}		
		);
	}
	
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Do you really want to delete?");
		if(result.get() == ButtonType.OK) {
			if(dService == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				dService.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}
