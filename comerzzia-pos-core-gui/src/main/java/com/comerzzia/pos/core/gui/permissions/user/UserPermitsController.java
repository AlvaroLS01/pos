package com.comerzzia.pos.core.gui.permissions.user;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionDTO;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.permissions.PermissionsController;
import com.comerzzia.pos.core.services.session.POSUserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

@Component
@CzzScene
public class UserPermitsController extends SceneController implements Initializable {

    protected static final Logger log = Logger.getLogger(UserPermitsController.class.getName());

    public static final String PARAM_INPUT_ACTION_NAME = "action_name";

    @FXML
    protected Label lbActionName;

    @FXML
    protected Button btManage, btAccept;

    @FXML
    protected TilePane tpActions;
    
    @Autowired
    protected POSUserSession posUserSession;
    
    // Action whose operations will be managed
    protected ActionDetail action;
    
    // User permissions on managed action
    protected EffectiveActionPermissionsDto userActionPermissions;
    protected boolean canManage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {
        log.debug("initializeComponents() - Inicialización de componentes");
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	
    	// Set action whose permissions will be managed
        action = (ActionDetail) sceneData.get(UserPermitsController.PARAM_INPUT_ACTION_NAME);
        lbActionName.setText(action.getTitle());

        // Query user effective action permissions
        userActionPermissions = posUserSession.getEffectivePermissions(action, false);
        
        tpActions.getChildren().clear();
        for (PermissionDTO permission : userActionPermissions.getPermissionList()) {
        	
            // Add action and user permission          
            PermissionAPComponent paComponent = new PermissionAPComponent(permission);
            tpActions.getChildren().add(paComponent);
        }

        // Check user has access to manage permissions window
        if (userActionPermissions.isCheckManage()) {
            log.debug("onSceneOpen() - El usuario tiene permisos de Administración sobre alguna operación ");
            canManage = true;              
        }
        else {
            log.debug("onSceneOpen() - El usuario NO tiene permisos de Administración sobre alguna operación ");
            canManage = false;
        }
        
        btManage.setVisible(canManage);
    }

    @Override
    public void initializeFocus() {
    }

    @FXML
    public void actionManage(ActionEvent event) {
        log.debug("actionManage()");
        if (canManage) {
        	log.debug("actionManage()- accediendo a administración de permisos de acción : " + action.getActionId());
        	
        	sceneData.put(PermissionsController.PARAM_INPUT_ACTION_NAME, action);
        	openScene(PermissionsController.class);
        }
        else {
            log.debug("actionManage() - El usuario no puede administrar");
        }
    }

}
