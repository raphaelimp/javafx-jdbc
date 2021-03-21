package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{
	
	private SellerService sService;
	
	public void setSellerService(SellerService sService) {
		this.sService = sService;
	}
	
	@FXML
	private TableView<Seller> tvSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tcId;
	
	@FXML
	private TableColumn<Seller, String> tcName;
	
	@FXML
	private TableColumn<Seller, String> tcEmail;
	
	@FXML
	private TableColumn<Seller, Date> tcBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tcBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tcEdit;
	
	@FXML
	private TableColumn<Seller, Seller> tcRemove;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller Seller = new Seller();
		createDialogForm(Seller, "/gui/SellerForm.fxml", parentStage);
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tcBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tcBirthDate, "dd/MM/yyyy");
		tcBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableCOlumnDouble(tcBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tvSeller.prefHeightProperty().bind(stage.heightProperty() );
	}
	
	public void updateTableView() {
		if(sService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = sService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tvSeller.setItems(obsList);
		initEditButton();
		initRemoveButtons();
	}
	
	private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			SellerFormController controller = loader.getController();
			controller.setSeller(seller);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage diaologStage = new Stage();
			diaologStage.setTitle("Enter Seller Data");
			diaologStage.setScene(new Scene(pane));
			diaologStage.setResizable(false);
			diaologStage.initOwner(parentStage);
			diaologStage.initModality(Modality.WINDOW_MODAL);
			diaologStage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
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
			new TableCell<Seller, Seller>(){
				private final Button button = new Button("eidt");
				
				@Override
				protected void updateItem(Seller obj, boolean empty) {
					super.updateItem(obj, empty);
					
					if(obj == null) {
						setGraphic(null);
						return;
					}
					
					setGraphic(button); 
						button.setOnAction(
							event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
					}
			});
	}
	
	private void initRemoveButtons() {
		tcRemove.setCellValueFactory(param -> 
			new ReadOnlyObjectWrapper<>(param.getValue()));
		tcRemove.setCellFactory(param -> 
			new TableCell<Seller, Seller>() {
				private final Button button = new Button("remove");
				
				@Override
				protected void updateItem(Seller obj, boolean empty) {
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
	
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Do you really want to delete?");
		if(result.get() == ButtonType.OK) {
			if(sService == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				sService.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}
