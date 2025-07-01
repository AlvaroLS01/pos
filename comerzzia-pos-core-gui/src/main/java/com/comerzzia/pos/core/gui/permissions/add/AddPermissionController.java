package com.comerzzia.pos.core.gui.permissions.add;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.Profile;
import com.comerzzia.core.facade.model.User;
import com.comerzzia.core.facade.service.profile.ProfileServiceFacade;
import com.comerzzia.core.facade.service.user.UserSearchParams;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class AddPermissionController extends SceneController implements Initializable, ButtonsGroupController {

    protected static final Logger log = Logger.getLogger(AddPermissionController.class.getName());

    public static final String MODE_USER = "U";
    public static final String MODE_PROFILE = "P";

    public static final String PARAM_INPUT_MODE = "mode";

    protected String mode;

    protected List<User> users;
    protected List<Profile> profiles;
    protected ObservableList<AddPermissionRow> addPermissionRows;

    protected AddPermissionForm ftAddPermission;

    @FXML
    protected TextField tfFilter1, tfFilter2;

    @FXML
    protected Label lbFilter1, lbFilter2;

    @FXML
    protected TableView<AddPermissionRow> tbHolder;

    @FXML
    protected TableColumn tcField1, tcField2;

    @FXML
    protected Button btAccept, btCancel, btSearch;
    
    @FXML
    protected Label lbError, lbHeader;
    
    @Autowired
    protected ProfileServiceFacade profileService;
    
    @Autowired
    protected UserServiceFacade userService;
    
    @Autowired
    protected Session session;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcField1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbHolder", "tcField1", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcField2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbHolder", "tcField2", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
           
        tcField1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AddPermissionRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AddPermissionRow, String> cdf) {
                return new SimpleStringProperty(cdf.getValue().getHolder());
            }
        });
        
        tcField2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AddPermissionRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AddPermissionRow, String> cdf) {
                return new SimpleStringProperty(cdf.getValue().getHolderDes());
            }
        });

        ftAddPermission = SpringContext.getBean(AddPermissionForm.class);
        
        ftAddPermission.setFormField("user", tfFilter1);
        ftAddPermission.setFormField("name", tfFilter2);
    }

    @Override
    public void initializeComponents() {
        log.debug("initializeComponents() - Inicialización de componentes");
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	initializeComponentsData();
    }

    @Override
    public void initializeFocus() {
        tfFilter1.requestFocus();
    }

    public void refreshData() {
        tbHolder.setItems(addPermissionRows);
        
        if(addPermissionRows.isEmpty()){
            tbHolder.setPlaceholder(new Label(""));
        }
    }

    @Override
    public void executeAction(ActionButtonComponent actionButton) {
        log.debug("executeAction() - Realizando la acción : " + actionButton.getClave() + " de tipo : " + actionButton.getTipo());
    }

    public void initializeComponentsData() {
        log.debug("initializeComponentsData()");
        
        mode = (String) sceneData.get(AddPermissionController.PARAM_INPUT_MODE);
        addPermissionRows = FXCollections.observableList(new ArrayList<AddPermissionRow>());
        
        tfFilter1.clear();
        tfFilter2.clear();
        lbError.setText("");
        
        ftAddPermission.clearErrorStyle();

		if (isModeUser()) {
			loadDefaultUsers();
			lbHeader.setText(I18N.getText("Seleccionar Usuario"));

			lbFilter1.setText(I18N.getText("Usuario"));
			tcField1.setText(I18N.getText("Usuario"));
			lbFilter2.setText(I18N.getText("Nombre"));
			tcField2.setText(I18N.getText("Nombre"));

			tfFilter2.setVisible(true);
			lbFilter2.setVisible(true);
			tcField2.setVisible(true);
		}
		else if (isModeProfile()) {
			loadDefaultProfiles();
			lbHeader.setText(I18N.getText("Seleccionar Grupo"));

			lbFilter1.setText(I18N.getText("Grupo"));
			tcField1.setText(I18N.getText("Grupo"));

			tfFilter2.setVisible(false);
			lbFilter2.setVisible(false);
			tcField2.setVisible(false);
		}
		
		refreshData();
    }

	protected void loadDefaultUsers() {
		addPermissionRows.clear();
		
		UserSearchParams searchParams = new UserSearchParams();
		searchParams.setActive(true);
		users = userService.findStoreUsers(session.getApplicationSession().getCodAlmacen(), searchParams);
		for (User user : users) {
			addPermissionRows.add(new AddPermissionRow(user));
		}
	}
    
    protected void loadDefaultProfiles() {
        addPermissionRows.clear();
        
        profiles = profileService.findAll();
        for (Profile profile : profiles) {
            addPermissionRows.add(new AddPermissionRow(profile));
        }
    }
    
    @FXML
    public void actionAccept(ActionEvent event) {
        log.debug("actionAccept()");  

        int index = tbHolder.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            actionTableEventEnter(null); // Just one table on screen
        }
        else {
            if (isModeProfile()) {
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar un grupo"));
            }
            else {
                DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar un usuario"));
            }
        }
    }

    public boolean isModeUser() {
        return mode.equals(MODE_USER);
    }

    public boolean isModeProfile() {
        return mode.equals(MODE_PROFILE);
    }

    @FXML
    public void actionEnterFilter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            actionFilter();
        }
    }

    @FXML
    public void actionFilter() {
        String filter1 = tfFilter1.getText().toLowerCase();
        String filter2 = tfFilter2.getText().toLowerCase();
        
        ftAddPermission.setUser(tfFilter1.getText());
        ftAddPermission.setName(tfFilter2.getText());
        
        if(validateAddPermissionForm()){
        	if (isModeProfile()) {
        		if (StringUtils.isNotBlank(filter1)) {
        			filterProfiles(filter1);
        		}
        		else{
       				loadDefaultProfiles();
        		}
        	}
        	else if (isModeUser()) {
        		if (StringUtils.isNotBlank(filter1) || StringUtils.isNotBlank(filter2)) {
        			filterUsers(filter1, filter2);
        		}
        		else{
       				loadDefaultUsers();
        		}
        	}
        }
        
        refreshData();
    }
    
    protected void filterProfiles(String profilesFilter) {
        addPermissionRows.clear();

		for (Profile profile : profiles) {
			if (profile.getProfileDes().toLowerCase().indexOf(profilesFilter) != -1) {
				addPermissionRows.add(new AddPermissionRow(profile));
			}
		}
    }
    
    protected void filterUsers(String nameFilter, String userFilter) {
        addPermissionRows.clear();
        
		if (StringUtils.isNotBlank(nameFilter)) {
			for (User user : users) {
				if (user.getUserCode().toLowerCase().indexOf(nameFilter) != -1) {
					addPermissionRows.add(new AddPermissionRow(user));
				}
			}
		}
		if (StringUtils.isNotBlank(userFilter)) {
			for (User user : users) {
				if (user.getUserDes().toLowerCase().indexOf(userFilter, -1) != -1) {
					addPermissionRows.add(new AddPermissionRow(user));
				}
			}
		}
    }

    @Override
	public void actionTableEventEnter(String tableId) {
		log.debug("actionEventEnterTable() - Acción ejecutada");
		if (CollectionUtils.isNotEmpty(tbHolder.getItems())) {
			closeSuccess(tbHolder.getSelectionModel().getSelectedItem());
		}
    }
    
	protected boolean validateAddPermissionForm() {
		log.debug("validateAddPermissionForm()");

		boolean valid;

		// Clear form errors
		ftAddPermission.clearErrorStyle();
		// Clear possible previous error
		lbError.setText("");

		Set<ConstraintViolation<AddPermissionForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(ftAddPermission);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<AddPermissionForm> next = constraintViolations.iterator().next();
			ftAddPermission.setErrorStyle(next.getPropertyPath(), true);
			ftAddPermission.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valid = false;
		}
		else {
			valid = true;
		}

		return valid;
	}

}
