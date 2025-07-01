package com.comerzzia.pos.core.gui.controllers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSURLHandler.URLMethodHandler;
import com.comerzzia.pos.core.gui.about.AboutController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.KeyCodeCombinationParser;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.htmlparser.ScreenTemplateParser;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.login.userselection.UserSelectionController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.user.passwordchange.PasswordChangeController;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.POSUserSession;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.events.CzzCloseEvent;
import com.comerzzia.pos.util.events.CzzTabEvent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class SceneController implements Initializable, URLMethodHandler{
	
	@Autowired
	protected ScreenTemplateParser screenTemplateParser;
	@Autowired
	protected CoreContextHolder contextHolder;
	@Autowired
	protected POSUserSession userSession;
    @Autowired
    protected ApplicationSession applicationSession;
	
	@Autowired
    protected Keyboard keyboard;
	
	protected StageController stageController;
	protected SceneView sceneView; 
	protected Scene scene;
	protected Map<String, Object> sceneData = new HashMap<>();
	protected Boolean initialized = false;
	protected boolean showKeyboard;
	protected SceneCallback<?> callback;
	
	@FXML
	protected Pane mainPane;
	
	@FXML
	protected HBox hbClose;
	
    public SceneView getSceneView() {
    	return sceneView;
    }

	/**
	 * Method called just when FXML is loaded. It is called only once in the life
	 * cycle of the screen. It must carry all the initialization operations that
	 * must be executed once before the creation of the screen.
	 * 
	 * @param url
	 * @param rb
	 * @throws InitializeGuiException
	 */
	public void initialize() throws InitializeGuiException {
		showKeyboard = true;
		initializeComponents();
		addEventHandlers();
		getScene().setRoot(sceneView.getViewNode());
		
		setInitialized(true);
	}

	/**
	 * Method called after loading FXML. Called only once in the life cycle of the
	 * life cycle of the screen. It shall carry all one-time initialization
	 * operations that require the initialization operations that need to be
	 * executed only once and require the screen to be screen is created.
	 * 
	 * @throws InitializeGuiException
	 */
	public abstract void initializeComponents() throws InitializeGuiException;
	
	/**
	 * Method called every time the scene is <b>opened</b>. 
	 * This is not called when the focus is regained after closing another scene.
	 * (See {@link #onSceneShow() onSceneShow()})
	 * 
	 * @throws InitializeGuiException
	 */
	public abstract void onSceneOpen() throws InitializeGuiException;
	
	/**
	 * Method called every time the screen is displayed, even if it is not the first time it is done. 
	 * Can also be called on request to reset the  focus. 
	 * It must perform the operations necessary to initialize the focus of
	 * the screen each time it is needed. It is executed inside a Platform.runLater(), after executing all 
	 * heavy operations. 
	 * (See {@link #onSceneShow() onSceneShow()})
	 */
	protected abstract void initializeFocus();
	
	/**
	 * Method called every time the screen is displayed, even if it is not the first time it is done. 
	 * It is executed inside a Platform.runLater, after {@link #initializeFocus() initializeFocus()}.
	 */
	protected void onSceneShowed() {};
	
	/**
	 * Method called every time the scene get the focus. 
	 * It's called when the scene is opening and when the scene is retrieving the focus after another scene is closed.
	 * When the scene is opening this is called after {@link #succededLongOperations() succededBackgroundOpenOperation()}.
	 * 
	 * Overriding this method should never be done without calling super.
	 * 
	 */
	public void onSceneShow(){
		checkUIPermission();
		keyboard.onController(SceneController.this);
		initializeFocus();
		onSceneShowed();
	}
	
	/**
	 * Method called while opening the screen in a second thread. It must contain heavy data loading logic
	 * and in no case can interact with javaFx elements. 
	 * 
	 * To interact with javaFx after this method has finished its execution use {@link #succededLongOperations() succededBackgroundOpenOperation()} 
	 * and {@link #failedBackgroundOpenOperation() failedBackgroundOpenOperation()}
	 * 
	 * This method is called everytime the scene is open
	 * 
	 * @throws Exception (Commonly InitializeGuiException)
	 */
	protected void executeLongOperations() throws Exception {}
	
	/**
	 * Method called in the javaFX thread after the correct execution of {@link #executeLongOperations() executeBackgroundOpenOperations()}.
	 * This method can interact 
	 */
	protected void succededLongOperations() {}
	
	/**
	 * Method called in the javaFX thread if {@link #executeLongOperations() executeBackgroundOpenOperations()} throws any exception.
	 * By default, this method will show a dialog window showing the error and the closes the scene.
	 */
	protected void failedLongOperations(Throwable e) {
		if(e instanceof InitializeGuiException) {
			InitializeGuiException ex = (InitializeGuiException)e;
			if(ex.isShowError()) {
				DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getMessage());
			}
		}else {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
		}
		close();
	}
	
	public void checkUIPermission() {
	}
	
	protected void addEventHandlers() {
		addCloseEvent();
		addEscapePressedEvent();
		addLocaleChangeEvent();
	}
	

	protected void addCloseEvent() {
		getScene().addEventHandler(CzzCloseEvent.CLOSE_EVENT, new EventHandler<CzzCloseEvent>() {
			@Override
			public void handle(CzzCloseEvent event) {
				log.debug("addEventHandlers() - CzzCloseEvent.CLOSE_EVENT: Closing scene");
				closeCancel();
				event.consume();
			}
		});
	}
	
	protected void addEscapePressedEvent() {
		getScene().addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					if(!scene.getRoot().isDisabled()) {
						closeCancel();
					}
					event.consume();
				}
			}
		});
	}
	
	protected void addLocaleChangeEvent() {
		applicationSession.currentLocaleProperty().addListener((observable, oldValue, newValue) -> onLocaleChange());
	}
	
	/** 
	 * Fire an event to close the current screen. This method should not be directly used as it does not call the scene callback nor check if the scene can be close
	 */
	public void close() {
		try {
			if (getWebView() != null) getWebView().getEngine().loadContent("");

			this.getStageController().closeCurrentScene();
		} catch (InitializeGuiException e) {
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
		} finally {
			sceneData.clear();
		}
	}
	
	public void loadWebView(String template, Map<String, Object> params, WebView webView) {
		long start = System.currentTimeMillis();
		
		try {
			if (params == null) {
				params = new HashMap<String, Object>();
			}
			checkPermissions(params);

			String html = screenTemplateParser.parser(template, params);
			
			log.trace("Template " + template + " parsed in " + (System.currentTimeMillis()-start) + " milliseconds");
		
			webView.getEngine().loadContent(html);
			
			log.trace("Template " + template + " loaded in " + (System.currentTimeMillis()-start) + " milliseconds");
		}
		catch (Exception e) {
			log.error("loadWebViewPrincipal() - Error: " + e.getMessage(), e);
		}
	}
	
	@Deprecated
	/**
	 * @deprecated Not longer necessary. The numeric keypad initializes itself. 
	 * Can be used to force the initialization of the numeric keypad before the scene is loaded.
	 */
	public void initNumericKeypad(NumericKeypad numpad) {
		numpad.init();
		setShowKeyboard(false);
	}
		
	public boolean isShowKeyboard() {
		return showKeyboard;
	}

	public void setShowKeyboard(boolean showKeyboard) {
		this.showKeyboard = showKeyboard;
	}
	
	public void addKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
        log.trace("addKeyEventHandler()");
        getScene().addEventFilter(keyEventType, keyEventHandler);
    }

    public void removeKeyEventHandler(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
        getScene().removeEventFilter(keyEventType, keyEventHandler);
    }
    
    public void checkOperationPermissions(String operacion) throws PermissionDeniedException {
    	log.trace("compruebaPermisos() - Comprobando permisos efectivos para la operación: " + operacion);
    	if(getStageController().getCurrentAction()!=null) {
    		getStageController().getCurrentAction().checkOperationPermissions(operacion);
    	}
    }
    
    public void openActionScene(Long actionId) {
    	stageController.showActionScene(actionId);
    }
    
    public void openActionScene(Long actionId, Class<? extends ActionSceneController> clazz) {
    	stageController.showActionScene(actionId, clazz);
    }
    
    public void openActionScene(Long actionId, Class<? extends ActionSceneController> clazz, SceneCallback<?> callback) {
    	stageController.showActionScene(actionId, clazz, callback);
    }
    public void openActionScene(Long actionId, Class<? extends ActionSceneController> clazz, Map<String, Object> sceneData) {
    	stageController.showActionScene(actionId, clazz, callback, sceneData);
    }
    
    public void openScene(Class<? extends SceneController> clazz, SceneCallback<?> callback) {
    	stageController.showScene(clazz, callback);
    }
    
    public void openScene(Class<? extends SceneController> clazz) {
    	stageController.showScene(clazz);
    }
    
    /**
     * Open a modal scene with a closing callback. 
     * When the modal scene opens the execution thread is paused until is closed again.
     * When the modal scene is closed the callback is called first, then the execution thread is resumed.
     * 
     * @param clazz
     * @param callback
     */
    public void openModalScene(Class<? extends SceneController> clazz, SceneCallback<?> callback) {
    	openModalScene(clazz, callback, null);
    }
    public void openModalScene(Class<? extends SceneController> clazz) {
    	openModalScene(clazz, null, null);
    }

	public void openModalScene(Class<? extends SceneController> clazz, SceneCallback<?> callback, Map<String, Object> sceneData) {
		stageController.openModalStage(sceneData).showModalScene(clazz, callback);
    }
    
    protected void initializeTabPaneEvents(TabPane pane) {
		pane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				newValue.getContent().fireEvent(new CzzTabEvent(CzzTabEvent.TAB_EVENT));
			}
		});
		if(pane.getTabs() != null && !pane.getTabs().isEmpty()) {
			pane.getTabs().get(0).getContent().fireEvent(new CzzTabEvent(CzzTabEvent.TAB_EVENT));
		}
	}
    
	protected void checkPermissions(Map<String, Object> params) {
		if(getStageController().getCurrentAction()!=null) {
			ActionDetail action = getStageController().getCurrentAction().getAction();
			EffectiveActionPermissionsDto permissions = userSession.getEffectiveActionPermissions(action);
			permissions.getPermissions().entrySet().stream().forEach(value -> {
				if(permissions.isCheckOperationDes(value.getKey())) {
					params.put("permission_"+value.getValue().getOperation().getExecution().toLowerCase().replace(" ", "_"), true);
				}
			});
		}
	}
	
	public Class<? extends SceneView> getSceneViewClass(){
		return getSceneViewClass(getClass());
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends SceneView> getSceneViewClass(Class<?> clazz) {
		String clazzName = clazz.getName();
		int viewIndex = clazzName.lastIndexOf("Controller");
		Class<? extends SceneView> clazzView = null;
		String viewName = clazzName.substring(0, viewIndex)+"View";
		try {
			clazzView = (Class<? extends SceneView>) Class.forName(viewName);
		}catch(ClassNotFoundException e) {
			log.debug("getSceneViewClass() - Class not found "+viewName+" loading from superClass");
			clazzView = getSceneViewClass(clazz.getSuperclass());
		}
		return clazzView;
	}
	
	/**
     * Method called just before the controller is closed
     * */
	public void onClose(){
    }
    
    /**
     * Method called every time a scene is being closed. Must return true/false 
     * depending on whether the scene can be closed on the state it is in. 
     * If the scene can't be closed should display a dialog notifying the reason.
     */
    public boolean canClose(){ 
    	return true;
    }

    /**
     * Method called every time a scene is being closed as a succeeded operation. Must return true/false 
     * depending on whether the scene can be closed on the state it is in. 
     * If the scene can't be closed should display a dialog notifying the reason.
     * 
     * This method is called after {@link #canClose()}
     */
    public boolean canCloseSuccess() {
    	return true;
    }
    
    /**
     * Method called every time a scene is being closed as a canceled operation. Must return true/false 
     * depending on whether the scene can be closed on the state it is in. 
     * If the scene can't be closed should display a dialog notifying the reason.
     * 
     * This method is called after {@link #canClose()}
     */
    public boolean canCloseCancel() {
    	return true;
    }
    
    /**
     * Method called during the execution of long operations on background task. The scene's webview will be update to show
     * a loading message if this method is override to return the scene's webview.
     * 
     * @return
     */
    public WebView getWebView() {
		return null;
	}
    
    /**
     * Crea evento ENTER en tabla
     *
     * @param table
     */
    public void addEnterTableEvent(TableView<?> table) {
        log.trace("addEnterTableEvent()");
        KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        
        final KeyCodeCombination keyCodeAccion = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo()!=null?AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeAction():null, new KeyCodeCombination(KeyCode.ENTER));
        final KeyCodeCombination keyCodeAccionGeneral = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo()!=null?AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralAction():null, new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN));
        
        final String tableId = table.getId();

        EventHandler<KeyEvent> evh = event -> {
        	if (keyCodeAccion.match(event)) {
                actionTableEventEnter(tableId);
            }
        };

        EventHandler<KeyEvent> evhGeneral = event -> {
        	if (keyCodeAccionGeneral.match(event)) {
            	actionTableEventEnter(tableId);
            }
        };
        table.addEventFilter(KeyEvent.KEY_RELEASED, evh); // registramos el evento a la tabla
        // Añadimos el evento a la lista de eventos
        addKeyEventHandler(evhGeneral, KeyEvent.KEY_RELEASED);
        
    }

    /**
     * Evento ENTER en tabla.
     *
     * @param idTabla
     */
    public void actionTableEventEnter(String tableId) {
        log.trace("actionTableEventEnter() - versión controller a implementar por clase heredada");
    }

    /**
     * Crea evento NEGAR en tabla
     *
     * @param table
     */
    public void addNegateTableEvent(TableView<?> table) {
        log.trace("addNegateTableEvent()");
        
        KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        final KeyCodeCombination keyCodeNegate = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeNegate(), parser.build(KeyCode.MINUS, null));
        final KeyCodeCombination keyCodeGeneralNegate = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralNegate(), new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));
        final KeyCodeCombination keyCodeMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination keyCodeSubtract = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);
        
        final String tableId = table.getId();

        EventHandler<KeyEvent> evh = event -> {
        	if (keyCodeNegate.getCode() == KeyCode.MINUS || keyCodeNegate.getCode() == KeyCode.SUBTRACT) {
                if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
                    actionTableEventNegate(tableId);
                }
        	} else {
        		 if (keyCodeNegate.match(event)) {
        			 actionTableEventNegate(tableId); 
        		 }
        	}
        };
		EventHandler<KeyEvent> evhGeneral = event -> {
			if (keyCodeGeneralNegate.equals(keyCodeMinus) || keyCodeGeneralNegate.equals(keyCodeSubtract)) {
				if (keyCodeMinus.match(event) || keyCodeSubtract.match(event)) {
					actionTableEventNegate(tableId);
				}
			} else {
				if (keyCodeNegate.match(event)) {
					actionTableEventNegate(tableId);
				}
			}
		};

        table.addEventFilter(KeyEvent.KEY_RELEASED, evh); // registramos el evento a la tabla
        // Añadimos el evento a la lista de eventos
        addKeyEventHandler(evhGeneral, KeyEvent.KEY_RELEASED);
    }

    /**
     * Evento NEGAR en tabla
     */
    public void actionTableEventNegate(String tableId) {
        log.trace("actionTableEventNegate() - versión controller a implementar por clase heredada");
    }

    /**
     * Crea evento Eliminar en tabla
     *
     * @param table
     */
    public void addDeleteTableEvent(TableView<?> table) {
        log.trace("crearEventoEliminarTabla()");
        final String tableId = table.getId();

        KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        final KeyCodeCombination keyCodeDelete = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeDelete(), parser.build(KeyCode.DELETE, null));
        final KeyCodeCombination keyCodeEliminarGeneral = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeDelete(), new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN));
        
        EventHandler<KeyEvent> evh = event -> {
        	if (keyCodeDelete.match(event)) {
                actionTableEventDelete(tableId);
            }
        };
        EventHandler<KeyEvent> evhGeneral = event -> {
        	if (keyCodeEliminarGeneral.match(event)) {
            	actionTableEventDelete(tableId);
            }
        };

        table.addEventFilter(KeyEvent.KEY_RELEASED, evh); // registramos el evento a la tabla
        // Añadimos el evento a la lista de eventos
        addKeyEventHandler(evhGeneral, KeyEvent.KEY_RELEASED);
    }

    /**
     * Evento ELIMINAR en tabla
     *
     * @param idTabla
     */
    public void actionTableEventDelete(String tableId) {
        log.trace("actionTableEventDelete() - versión controller a implementar por clase heredada");
    }

    public void addNavegationTableEvent(final TableView<?> table) {
    	
    	if(AppConfig.getCurrentConfiguration().getKeyCodesInfo() != null) {
    		KeyCodeCombinationParser parser = new KeyCodeCombinationParser();
        	final KeyCodeCombination keyCodeUp = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralPrevious(), new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN));
        	final KeyCodeCombination keyCodeDown = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralNext(), new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN));
        	final KeyCodeCombination keyCodeFirst = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralFirst(), new KeyCodeCombination(KeyCode.HOME, KeyCombination.CONTROL_DOWN));
        	final KeyCodeCombination keyCodeLast = parser.parse(AppConfig.getCurrentConfiguration().getKeyCodesInfo().getTableKeyCodeGeneralLast(), new KeyCodeCombination(KeyCode.END, KeyCombination.CONTROL_DOWN));
        	
            EventHandler<KeyEvent> evhGeneral = event -> {
            	if (keyCodeUp.match(event)) {
                	actionTableEventPrevious(table);
                } else if(keyCodeDown.match(event)) {
                	actionTableEventNext(table);
                } else if(keyCodeFirst.match(event)) {
                	actionTableEventFirst(table);
                } else if(keyCodeLast.match(event)) {
                	actionTableEventLast(table);
                }
            };

            addKeyEventHandler(evhGeneral, KeyEvent.KEY_RELEASED);
    	}
    	
    }
    
    /**
     * Acción mover al primer registro de la tabla
     *
     * @param table
     */
    public void actionTableEventFirst(TableView<?> table) {
        log.trace("actionTableEventFirst() - Acción ejecutada");
        if (table.getItems() != null && table.getItems() != null) {
            table.getSelectionModel().select(0);
            table.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

    /**
     * Acción mover a anterior registro de la tabla
     *
     * @param table
     */
    public void actionTableEventPrevious(TableView<?> table) {
        log.trace("actionTableEventPrevious() - Acción ejecutada");
        if (table.getItems() != null && table.getItems() != null) {
            int index = table.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                table.getSelectionModel().select(index - 1);
                table.scrollTo(index - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a siguiente registro de la tabla
     *
     * @param table
     */
    public void actionTableEventNext(TableView<?> table) {
        log.trace("actionTableEventNext() - Acción ejecutada");
        if (table.getItems() != null && table.getItems() != null) {
            int index = table.getSelectionModel().getSelectedIndex();
            if (index < table.getItems().size()) {
                table.getSelectionModel().select(index + 1);
                table.scrollTo(index + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a último registro de la tabla
     *
     * @param table
     */
    public void actionTableEventLast(TableView<?> table) {
        log.trace("actionTableEventLast() - Acción ejecutada");
        if (table.getItems() != null && table.getItems() != null) {
            table.getSelectionModel().select(table.getItems().size() - 1);
            table.scrollTo(table.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    protected void addSelectAllOnFocus(final TextField field) {
		field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (field.isFocused() && !field.getText().isEmpty()) {
							field.selectAll();
						}
					}
				});
			}
		});
    }
	
	public void gotoLogin() {
		HashMap <String, Object> stageData = new HashMap<>();
		stageData.put(LoginController.PARAM_INPUT_MODE_USER_CHANGE, "true");
		stageData.put(LoginController.PARAM_CURRENT_ACTION, getStageController().getCurrentAction());
		if (BooleanUtils.isTrue(AppConfig.getCurrentConfiguration().getButtonsLogin())) {
			stageData.put(UserSelectionController.PARAM_SCREEN_MODE_CASHIER, "S");
			openModalScene(UserSelectionController.class, null, stageData);
		} else {
			stageData.put(LoginController.PARAM_INPUT_MODE_CASHIER_SELECTION, "S");
			openModalScene(LoginController.class, null, stageData);
		}
		checkUIPermission();
	}

	public void gotoLockTPV() {
		HashMap <String, Object> stageData = new HashMap<>();
		stageData.put(LoginController.PARAM_CURRENT_ACTION, getStageController().getCurrentAction());
		if (BooleanUtils.isTrue(AppConfig.getCurrentConfiguration().getButtonsLogin())) {
			stageData.put(UserSelectionController.PARAM_SCREEN_MODE_LOCK, "S");
			openModalScene(UserSelectionController.class, null, stageData);
		} else {
			stageData.put(LoginController.PARAM_INPUT_MODE_LOCK, "S");
			openModalScene(LoginController.class, null, stageData);
		}
		checkUIPermission();
	}

	public void gotoAboutApplication() {
		openScene(AboutController.class);
	}

	public void gotoChangePassword() {
		openScene(PasswordChangeController.class);
	}
    
	public Scene getScene() {
		return scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	public void setSceneView(SceneView sceneView) {
		this.sceneView = sceneView;
	}
	public boolean isInitialized() {
		return initialized;
	}
	public void setInitialized(Boolean initialized) {
		this.initialized = initialized;
	}
	public Stage getStage() {
		return stageController.getStage();
	}
	public Map<String, Object> getSceneData() {
		return sceneData;
	}
	
	public void setSceneData(Map<String, Object> sceneData) {
		this.sceneData = sceneData;
	}
	public StageController getStageController() {
		return stageController;
	}
	public void setStageController(StageController stageController) {
		this.stageController = stageController;
	}
	
	public void setCallback(SceneCallback<?> callback) {
		this.callback = callback;
	}
	
	public interface SceneCallback<T> {
		public void onSuccess(T callbackData);
		
		@SuppressWarnings("unchecked")
		default void onSuccessObject(Object object) { 
			onSuccess((T) object); 
		};
		default void onCancel() {/* Do nothing */};
		default void onFailure() {/* Do nothing */};
	}
	
	public void onInactivity() {
		gotoLockTPV();
	}

	/**
	 * Closes current scene calling {@link com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback#onSuccess onSuccess} 
	 * callback with callbackData after checking if the current scene can be closed
	 * 
	 * @param callbackData
	 */
	public void closeSuccess(Object callbackData) {
		if(!canClose() || !canCloseSuccess()) {
			return;
		}
		
		close();
		if(callback!=null) {
			Platform.runLater(() -> {
				callback.onSuccessObject(callbackData);
			});
		}
	}
	
	/**
	 * Closes current scene calling {@link com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback#onSuccess onSuccess}
	 *  callback with an empty callbackData map after checking if the current scene can be closed
	 * 
	 * @param callbackData
	 */
	public void closeSuccess() {
		closeSuccess(null);
	}
	
	protected Type getTypeArgumentFromSuperclass(Class<?> currentClazz, Class<?> assignableFrom, int argIndex) {
		Type genericSuperclass = currentClazz.getGenericSuperclass();
		Type typeArgument = null;
		if (genericSuperclass instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
			if (assignableFrom.isAssignableFrom(((Class<?>) typeArguments[argIndex]))) {
				typeArgument = typeArguments[argIndex];
			}
		}
		
		if(typeArgument == null && currentClazz.getSuperclass() != null) {
			typeArgument = getTypeArgumentFromSuperclass(currentClazz.getSuperclass(), assignableFrom, argIndex);
		}
		
		return typeArgument;
	}
	
	/**
	 * Closes scene calling {@link com.comerzzia.pos.core.gui.controllers.SceneController.SceneCallback#onCancel onCancel}
	 *  callback after checking if the current scene can be closed
	 * 
	 */
	public void closeCancel() {
		if(!canClose() || !canCloseCancel()) {
			return;
		}
		
		close();
		if(callback != null) {
			Platform.runLater(() -> {
				callback.onCancel();
			});
		}
	}
	
	public void auditOperation(ComerzziaAuditEventBuilder event) {
		auditOperationCustomFields(event);

		applicationSession.auditOperation(event);		
	}
	
	protected void auditOperationCustomFields(ComerzziaAuditEventBuilder event) {		
	}
	
	public void onURLMethodCalled(String method, Map<String, String> params) {}

	/**
	 * Method called when the locale is changed. This method should be overridden if
	 * the scene needs to do something when the locale is changed.
	 */
	public void onLocaleChange() {
	}
	
	public void changeLocale(Locale newLocale) {
		applicationSession.setLocale(newLocale);
	}
	
	@PreDestroy
	/**
	 * Whenever the scenecontroller bean is destroy the sceneview bean must be destroyed. Otherwhise, the bean can not be initialized again.
	 */
	public void onDestroy() {
		contextHolder.destroyBean(getSceneViewClass());
	}
	
}
