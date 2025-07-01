package com.comerzzia.pos.core.gui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.menu.MenuController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.events.CzzChildComponentClosedEvent;
import com.comerzzia.pos.util.events.CzzCloseEvent;
import com.comerzzia.pos.util.events.CzzHeaderEvent;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.log4j.Log4j;

/**
 * Abstract class that manages the lifecycle of a JavaFX Stage and its scenes (SceneController)
 * within the Comerzzia POS application. It handles scene transitions, opening and closing actions,
 * and global event handling such as keyboard and custom application events.
 *
 * <p>Subclasses should implement their own logic specific to their stage type.</p>
 * 
 */
@Log4j
public abstract class StageController {

	@Autowired
	protected ActionSceneControllerBuilder actionSceneBuilder;

	@Autowired
	protected SceneControllerBuilder sceneControllerBuilder;

	@Autowired
	protected CoreContextHolder contextHolder;

	@Autowired
	protected AppConfig appConfig;
	
	@Autowired
	protected StageControllerBuilder stageControllerBuilder;

	protected SceneController currentScene;
	protected ActionSceneController currentAction;
	protected List<SceneController> sceneStack = new ArrayList<SceneController>();
	protected Stage stage;
	protected StageController ownerController;
	protected List<StageController> childControllers = new ArrayList<StageController>();
	protected Map<String, Object> stageData;
	protected boolean isLoading = false;

	
	/**
	 * Displays an action scene. The controller is loaded using the execution class defined in the database.
	 * 
	 * @param actionId - The ID of the action to be displayed.
	 * 
	 * @throws IllegalArgumentException if another action is being loaded
	 * 
	 */
	public void showActionScene(Long actionId) {
		showActionScene(actionId, null);
	}
	
	/**
	 * Displays an action scene.
	 * 
	 * @param actionId - The ID of the action to be displayed.
	 * @param clazz - The class of the controller.
	 * 
	 * @throws IllegalArgumentException if another action is being loaded
	 */
	public void showActionScene(Long actionId, Class<? extends ActionSceneController> clazz) {
		showActionScene(actionId, clazz, null);
	}
	
	
	/**
	 * Displays an action scene with a callback.
	 * 
	 * @param actionId - The ID of the action to be displayed.
	 * @param clazz - <b>Optional</b>. The class of the controller. If null, the action execution class is used.
	 * @param callback - The callback to be executed when the action is completed.
	 * 
	 * @throws IllegalArgumentException if another action is being loaded
	 */
	public void showActionScene(Long actionId, Class<? extends ActionSceneController> clazz, SceneCallback<?> callback) {
		showActionScene(actionId, clazz, callback, currentScene != null ? currentScene.getSceneData() : stageData);
	}

	/**
	 * Displays an action scene with a callback.
	 * 
	 * @param actionId - The ID of the action to be displayed.
	 * @param clazz - <b>Optional</b>. The class of the controller. If null, the action execution class is used.
	 * @param callback - <b>Optional</b>. The callback to be executed when the action is completed. When present a new instance of the action scene controller is always created.
	 * @param data - <b>Optional</b>. The data to be passed to the action scene controller. 
	 * 
	 * @throws IllegalArgumentException if another action is being loaded
	 */
	public void showActionScene(Long actionId, Class<? extends ActionSceneController> clazz, SceneCallback<?> callback, Map<String, Object> data) {
		Assert.isTrue(!isLoading, "Another action is being loaded");
		try {
			isLoading = true;
			ActionSceneController actionSceneController =  clazz != null ? (ActionSceneController) getSceneController(clazz) : getActionSceneController(actionId);
			if (callback != null || actionSceneController == null) {
				actionSceneController = actionSceneBuilder.buildActionSceneController(actionId, clazz, this, data);
				actionSceneController.checkExecutionPermission();
			} else {
				actionSceneController.onSceneShow();
			}
			
			updateCurrentScene(actionSceneController);
			currentScene.setCallback(callback);
			isLoading = false;
		} catch (InitializeGuiException e) {
			isLoading = false;
			log.error("showActionScene() - Error: ", e);
			onFailureSceneController(callback);
			if (e.isShowError()) {
				DialogWindowBuilder.getBuilder(stage).simpleThrowableDialog(e);
			}
		} catch (PermissionDeniedException e) {
			isLoading = false;
			log.error("showActionScene() - Sin permisos: ", e);
			onFailureSceneController(callback);
			DialogWindowBuilder.getBuilder(stage).simpleErrorDialog(e.getLocalizedMessage());
		} catch (Exception e) {
			isLoading = false;
			log.error("showActionScene() - Error: ", e);
			onFailureSceneController(callback);
			DialogWindowBuilder.getBuilder(stage).simpleThrowableDialog(e);
		}
	}
	
	/**
	 * Display a generic scene.
	 * 
	 * @param sceneControllerName - The bean name of the scene controller to be loaded.
	 * @param callback - The callback to be executed when the scene is loaded.
	 * 
	 * @deprecated Use {@link #showScene(Class, SceneCallback)} instead.
	 * 
	 * @throws IllegalArgumentException if another scene is being loaded
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void showScene(String sceneControllerName, SceneCallback<?> callback) {
		showScene((Class<? extends SceneController>)contextHolder.getApplicationContext().getType(sceneControllerName), callback);
	}
	
	/**
	 * Display a generic scene.
	 * 
	 * @param clazz - The class of the scene controller to be loaded.
	 * @param callback - The callback to be executed when the scene is loaded.
	 * 
	 * @throws IllegalArgumentException if another scene is being loaded
	 */
	public void showScene(Class<? extends SceneController> clazz, SceneCallback<?> callback) {
		Assert.isTrue(!isLoading, "Another action is being loaded");
		SceneController sceneController = null;
		try {
			isLoading = true;
			sceneController = getSceneController(clazz);
			if (sceneController == null) {
				sceneController = sceneControllerBuilder.buildSceneController(clazz, this,
						currentScene != null ? currentScene.getSceneData() : stageData);
			}else {
				sceneController.onSceneShow();
			}
			updateCurrentScene(sceneController);
			currentScene.setCallback(callback);
			isLoading = false;
		} catch (InitializeGuiException e) {
			isLoading = false;
			log.error("showScene() - Error: ", e);
			onFailureSceneController(callback);
			if (e.isShowError()) {
				DialogWindowBuilder.getBuilder(stage).simpleThrowableDialog(e);
			}
		} catch (Exception e) {
			isLoading = false;
			log.error("showScene() - Error: ", e);
			onFailureSceneController(callback);
			DialogWindowBuilder.getBuilder(stage).simpleThrowableDialog(e);
		}
		
	}

	/**
	 * Display a generic scene.
	 * 
	 * @param clazz - The class of the scene controller to be loaded.
	 * 
	 * @throws IllegalArgumentException if another scene is being loaded
	 */
	public void showScene(Class<? extends SceneController> clazz) {
		showScene(clazz, null);
	}

	protected void updateCurrentScene(SceneController sceneController) {
		currentScene = sceneController;
		sceneStack.remove(currentScene);
		sceneStack.add(currentScene);
		currentAction = getLastAction();
		setStageScene(currentScene);
	}
	
	protected void setStageScene(SceneController sceneController) {
		Scene nextScene = sceneController.getScene();
		nextScene.setRoot(new Group()); // Force the scene to be masked as dirty so the root is re-rendered from scratch
		nextScene.setRoot(sceneController.getSceneView().getViewNode());
		stage.setScene(nextScene);
	}

	/**
	 * Adds stage-wide event listeners (keyboard, window close, etc.).
	 */
	public void addEventsListeners() {
		stage.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					if (stage.getScene().getFocusOwner() instanceof Button) {
						((Button) stage.getScene().getFocusOwner()).fire();
					}
				} 
			}

		});
		stage.addEventHandler(CzzCloseEvent.CLOSE_SCENE_EVENT, new EventHandler<CzzCloseEvent>() {
			@Override
			public void handle(CzzCloseEvent event) {
				log.debug("addEventHandlers() - CzzCloseEvent.CLOSE_EVENT: Closing scene");
				try {
					closeCurrentScene();
					event.consume();
				} catch (InitializeGuiException e) {
					DialogWindowBuilder.getBuilder(stage).simpleThrowableDialog(e);
				}
			}
		});
		stage.addEventHandler(CzzHeaderEvent.OPEN_MENU_EVENT, new EventHandler<CzzHeaderEvent>() {
			@Override
			public void handle(CzzHeaderEvent event) {
				log.debug("addEventHandlers() - CzzMenuEvent.OPEN_MENU_EVENT: Opening Menu");
				showScene(MenuController.class);
				event.consume();
			}

		});
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(!currentScene.getScene().getRoot().isDisabled()) {
					currentScene.closeCancel();
				}
				event.consume();
			}
		});
	}

	/**
	 * Closes the current scene in the stage.  If the current scene is
	 * the last scene in the stack, the stage will be closed.
	 */
	public void closeCurrentScene() throws InitializeGuiException {
		if (sceneStack.size() > 1) {
			currentScene.onClose();// Si es una escena modal
			sceneStack.remove(currentScene);
			if (AppConfig.getCurrentConfiguration().getDeveloperMode() && AppConfig.getCurrentConfiguration().getDeveloperModeInfo().isForceReload()) {
				contextHolder.destroyBean(currentScene.getClass());
			}
			currentScene = sceneStack.get(sceneStack.size() - 1);
			currentAction = getLastAction();
			setStageScene(currentScene);
			currentScene.onSceneShow();
			
		} else {
			currentScene.onClose();
			if (AppConfig.getCurrentConfiguration().getDeveloperMode() && AppConfig.getCurrentConfiguration().getDeveloperModeInfo().isForceReload()) {
				SpringContext.destroyBean(currentScene.getClass());
			}
			if (ownerController != null) {
				ownerController.setStageData(currentScene.getSceneData());
			}
			closeStage();
		}
	}
	
	protected void closeStage() {
		stage.close();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	public ActionSceneController getCurrentAction() {
		return currentAction;
	}

	public Map<String, Object> getStageData() {
		return stageData;
	}

	public void setStageData(Map<String, Object> stageData) {
		this.stageData = stageData;
	}

	public StageController getOwnerController() {
		return ownerController;
	}

	public void setOwnerController(StageController ownerController) {
		this.ownerController = ownerController;
	}

	protected SceneController getSceneController(Class<? extends SceneController> clazz) {
		return sceneStack.stream().filter(clazz::isInstance).findFirst().orElse(null);
	}
	
	public SceneController getCurrentScene() {
		return currentScene;
	}

	protected ActionSceneController getActionSceneController(Long actionId) {
		return (ActionSceneController) sceneStack.stream()
				.filter(sc -> sc instanceof ActionSceneController
						&& ((ActionSceneController) sc).getAction().getActionId().equals(actionId))
				.reduce((first, second) -> second).orElse(null);
	}
	
	public boolean isAnotherActionOpened() {
		return sceneStack.stream().anyMatch(
				sc -> sc instanceof ActionSceneController
				&& !(sc.getClass().equals(currentAction.getClass()))
				&& !((ActionSceneController) sc).getAction().getActionId().equals(AppConfig.getCurrentConfiguration().getMainAction())); 
	}
	
	protected ActionSceneController getLastAction() {
		return (ActionSceneController) sceneStack.stream().filter( sc -> sc instanceof ActionSceneController)
				.reduce((first,second)->second).orElse(null);
	}
	
	protected ActionSceneController getActionOf(SceneController child) {
		int index = sceneStack.indexOf(child);
		while(index>0) {
			SceneController currentScene = sceneStack.get(index);
			if(currentScene instanceof ActionSceneController) {
				return (ActionSceneController) currentScene;
			}
			index --;
		}
		return null;
	}
	
	protected void onFailureSceneController(SceneCallback<?> callback) {
		if(callback != null) {
			callback.onFailure();
		}
	}
	
	/**
	 * Closes all scenes except the param instance and its child scenes
	 * @param instance
	 * @return
	 */
	public boolean closeAllActionsExcept(ActionSceneController instance) {
		int index = sceneStack.size()-1;
		while(index>0) {
			SceneController currentScene = sceneStack.get(index);
			ActionSceneController currentAction= getActionOf(currentScene);
			//Si ya solo queda la acción inicio dejamos de cerrar
			if(currentScene instanceof ActionSceneController && ((ActionSceneController)currentScene).getAction().getActionId().equals(AppConfig.getCurrentConfiguration().getMainAction())) {
				return true;
			}
			
			//Hemos llegado al login, la pantalla de inicio fue cerrada
			if(currentScene instanceof LoginController) {
				return true;
			}
			
			//Si la acción no es la que debe quedar abierta se intenta cerrar
			if(!currentAction.equals(instance)) {
				if(currentScene.canClose() && currentScene.canCloseCancel()) {
					sceneStack.remove(currentScene);
					this.currentAction = getLastAction();
					currentScene.onClose();
				}else {
					//Si no es posible cerrarla, se devuelve false
					return false;
				}
			}
			index--;
		}
		return true;
		
		
	}
	
	public ModalStageController openModalStage(Map<String, Object> sceneData) { 
		ModalStageController controller = ((ModalStageController)stageControllerBuilder.buildStageController(ModalStageController.class, getStage(), this, sceneData));
		childControllers.add(controller);
		controller.getStage().setOnHiding(e ->{
			childControllers.remove(controller);
		    getStage().getScene().getRoot().fireEvent(new Event(CzzChildComponentClosedEvent.CLOSED_CHILD_EVENT));
		});
		return controller;
	}
	
	public List<StageController> getChildControllers() {
		return childControllers;
	}
	
	public StageController getFocusedStageController() {
		MainStageManager mainStageManager = SpringContext.getBean(MainStageManager.class);
		return mainStageManager.getChildFocusedStageController();
	}
	
	protected StageController getChildFocusedStageController() {
		if (this.getStage().isFocused()) {
			return this;
		}
		StageController focused = null;
		for (StageController child : childControllers) {
			focused = child.getChildFocusedStageController();
			if (focused != null) {
				break;
			}
		}
		
		return focused;
	}
}
