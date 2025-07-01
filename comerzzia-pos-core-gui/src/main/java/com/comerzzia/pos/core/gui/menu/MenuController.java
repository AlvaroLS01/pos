package com.comerzzia.pos.core.gui.menu;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.MenuActionDetail;
import com.comerzzia.core.facade.model.MenuDetail;
import com.comerzzia.core.facade.service.menu.MenuServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.controllers.StageController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.permissions.PermissionsController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.permissions.user.UserPermitsController;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

@Component
@CzzScene
public class MenuController extends SceneController implements ButtonsGroupController{
	
	public static final String PARAM_SELECTED_ACTION = "MenuController.SelectedAction";

	private Logger log = Logger.getLogger(MenuController.class);
	
	@FXML
    protected HBox hBoxQuickAccess;
    
    // botonera de cabecera
 	protected ButtonsGroupComponent buttonpane;
	
	@FXML
	protected WebView wbMenu;
	
	protected ButtonsGroupModel buttonsPaneBean;
	
	@Autowired
	protected MenuServiceFacade menuService;
	
	protected Map<Long, MenuActionDetail> actionsMenu;
	protected MenuDetail menu;
	
	protected StageController mainStageController;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Nothing to initialize
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			loadButtonsPane();
			loadMenu();
			
			mainStageController = getStageController();
			if(stageController.getOwnerController()!=null) {
				mainStageController = stageController.getOwnerController();
			}
		}
		catch (Exception e) {
			log.error("initializeComponents() - Error: " + e.getMessage(), e);
			throw new InitializeGuiException(I18N.getText("Ha habido un error al cargar el menú de la aplicación."), e);
		}
	}

	protected void loadButtonsPane() {
		try {

			buttonsPaneBean = getSceneView().loadButtonsGroup();
			buttonpane = new ButtonsGroupComponent(buttonsPaneBean, hBoxQuickAccess.getPrefWidth(),
					hBoxQuickAccess.getPrefHeight(), this, ActionButtonSimpleComponent.class);
			hBoxQuickAccess.getChildren().add(buttonpane);
		} catch (InitializeGuiException | LoadWindowException e) {
			log.error("initializeComponents() - Error al crear botonera en la cabecera: " + e.getMessage(), e);
		}
	}

	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if("Menu".equals(method)) {
			String operation = params.get("operation");
			executeOperation(operation, params);
		}else if("CloseMenu".equals(method)) {
			closeCancel();
		}else if("SelectMenuItem".equals(method)) {
			String paramLineId = params.get("action_id");
			Long actionId = Long.valueOf(paramLineId);
			
			MenuActionDetail menuItem = actionsMenu.get(actionId);
			selectedMenuAction(menuItem);
		}
	}
	
	protected void selectedMenuAction(MenuActionDetail menuItem) {
		log.debug("selectedMenuAction()");
		if (menuItem == null) {
			log.warn("addHandlers() - SelectMenuItem: Menu item not found.");
			return;
		}
		closeSuccess();
		mainStageController.showActionScene(menuItem.getAction().getActionId());
	}
	
	protected void executeOperation(String operation, Map<String, String> params) {
		log.debug("executeOperation - Operation " + operation);
		if(operation.equals("gotoPermissions")) {
			gotoPermissions();
		}
		else if(operation.equals("gotoLockTPV")) {
			mainStageController.getCurrentScene().gotoLockTPV();
			closeSuccess();
		}
		else if(operation.equals("gotoAboutApplication")) {
			closeSuccess();
			mainStageController.getCurrentScene().gotoAboutApplication();
		}
		else if(operation.equals("gotoLogin")) {
			closeSuccess();
			mainStageController.getCurrentScene().gotoLogin();
		}
		else if(operation.equals("gotoChangePassword")) {
			closeSuccess();
			mainStageController.getCurrentScene().gotoChangePassword();
		}
	}

	protected void loadMenu() throws NotFoundException {
		menu = menuService.findMenuByUid(AppConfig.getCurrentConfiguration().getMenu(), AppConfig.getCurrentConfiguration().getApplication());
		
		actionsMenu = new HashMap<>();
		for(List<MenuActionDetail> menuItemList : menu.getMenuTree().values()) {
			for(MenuActionDetail menuItem : menuItemList) {
				actionsMenu.put(menuItem.getAction().getActionId(), menuItem);
			}
		}
		
		loadWebView();
	}

	protected void loadWebView() {
		Map<String, Object> params = new HashMap<>();
		params.put("menuItems", menu.getMenuTree());
		params.put("listButtonLine", buttonsPaneBean.getButtonsRow());
		
		loadWebView("menu/menu", params, wbMenu);
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		if(BooleanUtils.isTrue(AppConfig.getCurrentConfiguration().getDeveloperMode())) {
			log.debug("onSceneOpen - Developer mode active. Reloading menu.");
			loadMenu();
		}
	}

	@Override
	public void initializeFocus() {
		wbMenu.requestFocus();
	}
	
	public void gotoPermissions() {
        if (mainStageController.getCurrentAction()!=null){     
        	closeSuccess();
            log.debug("gotoPermisos()- accediendo a permisos de acción : " + mainStageController.getCurrentAction().getAction().getActionId());
            // Ponemos en el mapa la ventana para la que vamos  a gestionar los permisos
            mainStageController.getCurrentScene().getSceneData().put(PermissionsController.PARAM_INPUT_ACTION_NAME, mainStageController.getCurrentAction().getAction());
            mainStageController.getCurrentScene().openScene(UserPermitsController.class);
        }
        else{
            log.debug("gotoPermisos()- no hay ninguna acción seleccionada");
        }
    }
	
	@Override
	public void executeAction(ActionButtonComponent botonAccionado) throws PermissionDeniedException {
		// No standard action buttons
	}
	    
}
