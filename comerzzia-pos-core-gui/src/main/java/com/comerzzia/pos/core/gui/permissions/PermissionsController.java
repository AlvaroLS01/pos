package com.comerzzia.pos.core.gui.permissions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.model.ActionOperationDetail;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionDTO;
import com.comerzzia.core.facade.service.permissions.PermissionServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.permissions.add.AddPermissionController;
import com.comerzzia.pos.core.gui.permissions.add.AddPermissionRow;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Controller
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PermissionsController extends SceneController implements Initializable, ButtonsGroupController {

    private static final Logger log = Logger.getLogger(PermissionsController.class.getName());

    public static final String PARAM_INPUT_ACTION_NAME = "action_name";

    @FXML
    protected Label lbActionName;

    @FXML
    protected HBox hbTableMenu;

    @FXML
    protected TableView<PermissionsRow> tbPermissions;

    @FXML
    protected TableColumn tcHolder, tcHolderType;

    @FXML
    protected Button btAccept, btCancel;
    
    @Autowired
    protected PermissionServiceFacade permissionService;
    
    @Autowired
    protected Session session;

    protected ArrayList<PermissionsRow> permissions;
    protected ButtonsGroupComponent tableActionsButtonsGroup;
    protected ObservableList<PermissionsRow> permissionsRows;

    // Dynamic table columns
    protected HashMap<String, TableColumn> tableColumns; 	// Operation Id - TableColumn
    protected HashMap<Integer, String> tableColumnsIndex; 	// Column index - TableColumn Id

    // Action whose operations will be managed
    protected ActionDetail action;
    
    // User permissions on managed action
    protected EffectiveActionPermissionsDto userActionPermissions;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
	@Override
	public void initializeComponents() {
		try {
			tbPermissions.getSelectionModel().setCellSelectionEnabled(true);
			addEnterTableEvent(tbPermissions);
			addNavegationTableEvent(tbPermissions);

			List<ButtonConfigurationBean> actionsPermissions = loadTableActions();

			tableActionsButtonsGroup = new ButtonsGroupComponent(1, 4, this, actionsPermissions, hbTableMenu.getPrefWidth(), hbTableMenu.getPrefHeight(),
			        ActionButtonSimpleComponent.class.getName());
			hbTableMenu.getChildren().clear();
			hbTableMenu.getChildren().add(tableActionsButtonsGroup);

			tbPermissions.getColumns().addListener(new ListChangeListener(){

				public boolean suspended;

				// Register event to avoid reorder column problem. It will reset window if it is performed
				@Override
				public void onChanged(ListChangeListener.Change change) {
					change.next();
					if (change.wasReplaced() && !suspended) {
						// this.suspended = true;
						try {
							onSceneOpen();
						}
						catch (InitializeGuiException ex) {
							log.error("Tabla: onChanged - Error al reinicializar para bloquear orden de columnas ");
						}
					}
				}
			});
		}
		catch (LoadWindowException ex) {
			log.error("initializeComponents() - Error inicializando pantalla de gestiond e tickets");
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error cargando pantalla. Para mas información consulte el log."));
		}
	}

    protected List<ButtonConfigurationBean> loadTableActions() {
        List<ButtonConfigurationBean> actions = new ArrayList<>();
        actions.add(new ButtonConfigurationBean("icons/anadir-perfil.png", null, null, "ACTION_TABLE_ADD_PROFILE", "VENTANA"));
        actions.add(new ButtonConfigurationBean("icons/anadir-usuario.png", null, null, "ACTION_TABLE_ADD_USER", "VENTANA"));
        return actions;
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
        tbPermissions.getColumns().clear();
        tbPermissions.getColumns().add(tcHolderType);
        tbPermissions.getColumns().add(tcHolder);
        log.debug("onSceneOpen() - Inicialización de formulario");
        permissionsRows = FXCollections.observableList(new ArrayList<PermissionsRow>());
        
        // Init permissions columns map
        tableColumns = new HashMap<>();
        tableColumnsIndex = new HashMap<>();

        initializeComponentsData();

        tcHolder.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPermissions", "tcHolder", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcHolderType.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPermissions", "tcHolderType", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
             
        tcHolder.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PermissionsRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PermissionsRow, String> cdf) {
                return new SimpleStringProperty(cdf.getValue().getHolder());
            }
        });
            
        tcHolderType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PermissionsRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PermissionsRow, String> cdf) {
                String res = "";
                if (cdf.getValue().getHolderPermissions().isProfilePermission()) {
                    res = "P";
                    tcHolderType.getStyleClass().add("permisos-linea-perfil");
                }
                else if (cdf.getValue().getHolderPermissions().isUserPermission()) {
                    res = "U";
                    tcHolderType.getStyleClass().add("permisos-linea-usuario");
                }
                return new SimpleStringProperty(res);
            }
        });
    }

    @Override
    public void initializeFocus() {
        tbPermissions.requestFocus();
    }

    public void refreshData() {
        permissionsRows.clear();
        tbPermissions.setItems(permissionsRows);
        permissionsRows.addAll(permissions);
    }

	@Override
	public void executeAction(ActionButtonComponent actionButton) {
		log.debug("executeAction() - Realizando la acción : " + actionButton.getClave() + " de tipo : " + actionButton.getTipo());
		switch (actionButton.getClave()) {
			case "ACTION_TABLE_ADD_PROFILE":
				actionAddHolder(AddPermissionController.MODE_PROFILE);
				break;
			case "ACTION_TABLE_ADD_USER":
				actionAddHolder(AddPermissionController.MODE_USER);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + actionButton.getClave());
				break;
		}
	}

    @Override
    public void actionTableEventEnter(String tableId) {
        log.debug("actionEventEnterTable() - Acción ejecutada");
        if (tbPermissions.getItems() != null) {
            PermissionsRow permission = tbPermissions.getSelectionModel().getSelectedItem();
            ObservableList<TablePosition> selectedCells = tbPermissions.getSelectionModel().getSelectedCells();

            if (permission != null && CollectionUtils.isNotEmpty(selectedCells) && selectedCells.get(0).getColumn() > 1) { // First two columns do not count
                log.debug("actionEventEnterTable() Existen celdas selecionadas");
                
                final int column = selectedCells.get(0).getColumn();
                final int row = selectedCells.get(0).getRow();
                
				log.debug("actionEventEnterTable() - Fila:" + row + "  Columna:" + column);
				
                String permissionName = tableColumnsIndex.get(column);
                PermissionDTO cellPermission = permission.getHolderPermissions().getPermissions().get(permissionName);
                Long operationId = -1L;

                // Check user has permission to modify operation              
                if (cellPermission != null && userActionPermissions.isCheckManage(cellPermission.getOperation().getOperationDes())) {
                    log.debug("actionEventEnterTable()- La operación existe y el usuario tiene permisos de administración");
                    
                    // Change permission
                    if (cellPermission.isManage()) {
                        cellPermission.setAccessLevelUndefined();
                        permissions.get(selectedCells.get(0).getRow()).getHolderPermissions().getPermissions().get(permissionName).setAccessLevelUndefined();
                        permission.getHolderPermissionsGui().get(permissionName).setValue("permisos.noestablecido");
                    }
                    else if (cellPermission.isForbidden()) {
                        cellPermission.setAccessLevelGranted();
                        permissions.get(selectedCells.get(0).getRow()).getHolderPermissions().getPermissions().get(permissionName).setAccessLevelGranted();
                        permission.getHolderPermissionsGui().get(permissionName).setValue("permisos.concedido");
                    }
                    else if (cellPermission.isGranted()) {
                        cellPermission.setAccessLevelManage();
                        permissions.get(selectedCells.get(0).getRow()).getHolderPermissions().getPermissions().get(permissionName).setAccessLevelManage();
                        permission.getHolderPermissionsGui().get(permissionName).setValue("permisos.administrar");
                    }
                    else { // It is null
                        cellPermission.setAccessLevelForbidden();
                        permissions.get(selectedCells.get(0).getRow()).getHolderPermissions().getPermissions().get(permissionName).setAccessLevelForbidden();
                        permission.getHolderPermissionsGui().get(permissionName).setValue("permisos.denegado");
                    }
                }
                else {
                    log.debug("actionEventEnterTable()- La operación no existe existe o el usuario no tiene permisos de administración");
                }

                // Set operation Id
                if (cellPermission != null) { 
                    operationId = cellPermission.getOperation().getOperationId();
                }

                // For invokeLater event
                final long finalOperationId = operationId; 

                // Refresh table
                refreshData();

                // Select table element which was previously selected
                log.debug("actionEventEnterTable() - Programamos la reselección de la tabla para fila: " + row + " y columna: " + tableColumns.get(String.valueOf(finalOperationId)));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        log.debug("actionEventEnterTable() - Reselección en la tabla. ");
                        tbPermissions.getSelectionModel().clearAndSelect(row, tableColumns.get(String.valueOf(finalOperationId)));
                        tbPermissions.scrollTo(row);
                        tbPermissions.requestFocus();
                    }
                });
            }
        }
    }

    @FXML
    public void actionTableClick() {
        actionTableEventEnter(null); // null because there is only one table
    }

    public void initializeComponentsData() {
        log.debug("initializeComponentsData()");
        
		// Set action on which we manage permissions
		action = (ActionDetail) sceneData.get(PermissionsController.PARAM_INPUT_ACTION_NAME);
		lbActionName.setText(action.getTitle());

		permissions = new ArrayList<>();

		// Query action effective permissions
		List<EffectiveActionPermissionsDto> actionPermissions = permissionService.obtainEffectivePermissions(action);

		// Query user action permissions
		userActionPermissions = session.getPOSUserSession().getEffectivePermissions(action, false);

		// Columns are registered starting from the second one (holder)
		int columnIndex = 2;
		for (final ActionOperationDetail operation : action.getOperations()) {
			if (!tableColumns.containsKey(operation.getOperationId().toString())) {

				TableColumn tc = new TableColumn(operation.getOperationDes());
				tc.setSortable(false);
				tc.setEditable(false);
				tc.setMinWidth(120);
				tc.setCellFactory(
				        CellFactoryBuilder.createCellRendererCeldaPermiso("tbPermissions", "a-" + operation.getActionId() + "o" + operation.getOperationId(), CellFactoryBuilder.LEFT_ALIGN_STYLE));

				// Factory for every column
				tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PermissionsRow, String>, ObservableValue<String>>(){

					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<PermissionsRow, String> cdf) {
						return cdf.getValue().getHolderPermissionsGui().get(operation.getOperationDes());
					}
				});

				// Add column to table and maps
				tbPermissions.getColumns().add(tc);
				tableColumns.put(operation.getOperationId().toString(), tc);
				tableColumnsIndex.put(columnIndex, operation.getOperationDes());

				columnIndex++;
			}
		}

		// Add effective permissions to permissions list
		for (EffectiveActionPermissionsDto permission : actionPermissions) {
			PermissionsRow permissionsRow = new PermissionsRow(action, permission);
			permissions.add(permissionsRow);
		}

		refreshData();
    }

    @FXML
    public void actionAccept(ActionEvent event) {
		log.debug("actionAccept()");

		session.getPOSUserSession().clearPermissions();
		List<EffectiveActionPermissionsDto> permissionsToModify = permissions.stream()
		        .map(p -> p.getHolderPermissions())
		        .collect(Collectors.toList());
		// TODO Update only new and modified permissions
		permissionService.updateActionPermissions(action, permissionsToModify);
		closeSuccess();
    }

    @FXML
    public void actionCancel() {
        log.debug("actionCancel()");
        
        closeCancel();
    }

    protected void actionAddHolder(String holderType) {
        log.debug("actionAddHolder()");
        
        sceneData.put(AddPermissionController.PARAM_INPUT_MODE, holderType);
        openScene(AddPermissionController.class, new SceneCallback<AddPermissionRow>() {
			
			@Override
			public void onSuccess(AddPermissionRow callbackData) {
				PermissionsRow newPermission = new PermissionsRow(action, callbackData);
				log.debug("actionAddHolder() - Se ha recibido un permiso de la pantalla de permisos");
				
				if (!containsHolder(permissions, newPermission)) {
					log.debug("actionAddHolder() - Añadimos el titular a los permisos");
					permissions.add(newPermission);
				}
				else {
					log.debug("actionAddHolder() - ACTION_TABLE_ADD_PROFILE - El titular que se deseaba añadir ya esta conteido en la tabla de permisos ");
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El titular que se deseaba añadir ya esta conteido en la tabla de permisos"));
				}
				
				refreshData();
			}
		});

    }

    /**
     * Checks that the holder we are adding to the table is not already in it 
     */
    protected boolean containsHolder(ArrayList<PermissionsRow> permissions, PermissionsRow newPermission) {
		boolean found = false;
		
		for (PermissionsRow permission : permissions) {
			if (newPermission.getHolderPermissions().isProfilePermission() && permission.getHolderPermissions().isProfilePermission()
			        && permission.getHolderPermissions().getProfileId().compareTo(newPermission.getHolderPermissions().getProfileId()) == 0) {
				found = true;
				break;
			}
			else if (newPermission.getHolderPermissions().isUserPermission() && permission.getHolderPermissions().isUserPermission()
			        && permission.getHolderPermissions().getUserId().compareTo(newPermission.getHolderPermissions().getUserId()) == 0) {
				found = true;
				break;
			}
		}
		
		return found;
    }

}
