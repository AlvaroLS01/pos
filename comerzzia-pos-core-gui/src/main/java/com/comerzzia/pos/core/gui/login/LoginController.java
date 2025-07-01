package com.comerzzia.pos.core.gui.login;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.model.Language;
import com.comerzzia.core.facade.model.User;
import com.comerzzia.core.facade.service.action.ActionServiceFacade;
import com.comerzzia.core.facade.service.language.LanguageServiceFacade;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionServiceFacade;
import com.comerzzia.core.facade.service.user.UserInvalidLoginException;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.language.ActionButtonLanguageComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.POSUserSession;
import com.comerzzia.pos.core.services.session.SesionInitException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

@Controller
@Scope("prototype")
public class LoginController extends SceneController implements Initializable, ButtonsGroupController {
    
	private static final Logger log = Logger.getLogger(LoginController.class.getName());
    
	// Parameter key which activates cashier selection mode (Only needs to be present)
    public static final String PARAM_INPUT_MODE_CASHIER_SELECTION = "MODE_CASHIER_SELECTION";
    public static final String PARAM_INPUT_MODE_LOCK = "MODE_LOCK";
    public static final String PARAM_INPUT_MODE_USER_CHANGE = "MODE_USER_CHANGE";
    
    // Parameter key which contains selected user in selection user window
    public static final String PARAM_SELECTED_USER = "PARAM_SELECTED_USER";
    public static final String PARAM_OUTPUT_USER_CHANGE = "PARAM_OUTPUT_USER_CHANGE";
    
    public static final String PARAM_INPUT_CUSTOMER = "PARAM_INPUT_CUSTOMER";
    public static final String PARAM_INPUT_RATE_CODE = "PARAM_INPUT_RATE_CODE";
    public static final String PARAM_IS_STOCK = "PARAM_IS_STOCK";
    public static final String PARAM_MODAL = "modal";
    public static final String PARAM_CURRENT_ACTION = "PARAM_CURRENT_ACTION";

    public static final String OPERATION_REPLACE_CASHIER = "SUPLANTAR";
    
    @FXML
    protected TextField tfUser;
    @FXML
    protected PasswordField tfPassword;
    @FXML
    protected Button btAccept, btCancel, btStockSearch;
    @FXML
    protected Label lbError;
    @FXML
    protected Label lbStore, lbTill;
    @FXML
    protected Label lbDate, lbTime, lbPOSData, lbCloseButton;
    @FXML
    protected Label lbLoggedIn;
    @FXML
	protected Label lbUser, lbPassword;
    @FXML
    protected HBox hbLanguageBox;

    @FXML
    protected ImageView ivCustomerLogo;
    
    protected LoginForm frLogin;    

    protected Boolean modeCashierSelection;
    protected Boolean modeLock;
    protected Boolean modeUserChange;
    
    protected boolean selectedUser;
    protected ActionSceneController currentAction;
    
    @Autowired
    protected Session session;
    @Autowired
    protected PermissionServiceFacade permissionService;
    @Autowired
    protected POSUserSession posUserSession;
    @Autowired
    protected UserServiceFacade userService;
    @Autowired
    protected LanguageServiceFacade languageService;
    @Autowired
    protected CzzImageManager czzImageManager;
    @Autowired
  	protected ActionServiceFacade actionsService;
        
    @Autowired
	protected AppConfig appConfig;
    
    protected boolean loggingInProcess = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize()");

		frLogin = SpringContext.getBean(LoginForm.class);
		frLogin.setFormField("user", tfUser);
		frLogin.setFormField("password", tfPassword);
    }    
    
    @Override
    public void onSceneOpen() {
    	modeLock = null;
    	modeUserChange = null;
    	
        lbError.setText("");
        lbLoggedIn.setText("");
        btCancel.setDisable(false);
        
        tfUser.setText("");
        tfPassword.setText("");
        frLogin.clearErrorStyle();

        // Check if cashier selection mode is active
        if (sceneData != null) {
            if (sceneData.containsKey(PARAM_INPUT_MODE_CASHIER_SELECTION)) {
                sceneData.remove(PARAM_INPUT_MODE_CASHIER_SELECTION);
                modeCashierSelection = true;
                // Register escape event to exit window
                btCancel.setDisable(false);
			}
			else {
				modeCashierSelection = false;
			}
            
            if (sceneData.containsKey(PARAM_SELECTED_USER)) {
                tfUser.setText((String) sceneData.get(PARAM_SELECTED_USER));
                tfUser.setEditable(false);
                btCancel.setDisable(false);
                selectedUser = true;
			}
			else {
				selectedUser = false;
			}
            
            if (sceneData.containsKey(PARAM_INPUT_MODE_LOCK)) {
            	modeLock = true;
            	tfUser.setEditable(true);
            	btCancel.setDisable(true);
            	selectedUser = false;
            } 
            else{
            	modeLock = false;
            }
            
            if (sceneData.containsKey(PARAM_INPUT_MODE_USER_CHANGE)) {
            	modeUserChange = true;
            } 
            else{
            	modeUserChange = false;
            }
            
            currentAction = (ActionSceneController) sceneData.get(PARAM_CURRENT_ACTION);
        }
        
        if (AppConfig.getCurrentConfiguration().getDeveloperMode()){
        	if (sceneData == null || StringUtils.isBlank(((String) sceneData.get(PARAM_SELECTED_USER))) || ((String) sceneData.get(PARAM_SELECTED_USER)).equalsIgnoreCase(AppConfig.getCurrentConfiguration().getDeveloperModeInfo().getUsuario())) {
    		    log.debug("onSceneOpen() - Modo desarrollo establecemos usuario/password por defecto");
    		    tfUser.setText(AppConfig.getCurrentConfiguration().getDeveloperModeInfo().getUsuario());
    		    tfPassword.setText(AppConfig.getCurrentConfiguration().getDeveloperModeInfo().getPassword());
    		    if (AppConfig.getCurrentConfiguration().getDeveloperModeInfo().isAutologin() && !isModeLock() && !isModeCashierSelection()) {
    		    	btAccept.fire();
    		    }
    	    }
		}
        String posData = session.getApplicationSession().getCodAlmacen() + " · " + session.getApplicationSession().getStorePosBusinessData().getWarehouse().getWhDes() + " · " + session.getApplicationSession().getTillCode();
        lbPOSData.setText(posData);

    	refreshUserData();
    }
    

    protected void refreshUserData() {
    	lbError.setText("");
    	if(AppConfig.getCurrentConfiguration().getShowCashOpeningUser()) {
    		CashJournalSession cashJournalSession = session.getCashJournalSession();
    		if(cashJournalSession != null && cashJournalSession.isOpenedCashJournal()
				&& StringUtils.isNotBlank(cashJournalSession.getUserCode())) {
    			lbLoggedIn.setText(I18N.getText("Caja abierta por el usuario {0}", cashJournalSession.getUserCode()));
    		}else {
    			lbLoggedIn.setText("");
    		}
    	}
    	
	}

	@Override
    public void initializeFocus() { 	 
		if (AppConfig.getCurrentConfiguration().getButtonsLogin()) {
			CashJournalSession cashJournalSession = session.getCashJournalSession();
			if (AppConfig.getCurrentConfiguration().getShowCashOpeningUser()
					&& cashJournalSession != null && cashJournalSession.isOpenedCashJournal()
					&& StringUtils.isNotBlank(cashJournalSession.getUserCode())) {
				tfPassword.requestFocus();
			}
			else {
				if (!selectedUser) {
					tfUser.requestFocus();
				}
				else {
					tfPassword.requestFocus();
				}
			}
		}
		else {
			tfUser.requestFocus();
		}
    }
    
    @Override
    public void initializeComponents() {
        log.debug("initializeComponents() - Creando el timeline de refresco para el reloj");
        updateClock();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int seconds = 60 - calendar.get(Calendar.SECOND);
		
		Timeline delayTimeline = new Timeline();
		delayTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(seconds), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Timeline everyMinute = new Timeline();
				everyMinute.setCycleCount(Timeline.INDEFINITE);
				everyMinute.getKeyFrames().add(new KeyFrame(Duration.seconds(60), new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						updateClock();
					}
				}));
				everyMinute.play();
			}
		}));
		delayTimeline.play();
		
		if (AppConfig.getCurrentConfiguration().getLanguageSelection()) {
			loadButtonsLanguageSelection();
		}
		
		ivCustomerLogo.setImage(czzImageManager.getLogo(CzzImageManager.IMAGES_CUSTOMER_LOGO_NAME));
    }
    
    protected void refreshLanguageFlags() {
    	if (!AppConfig.getCurrentConfiguration().getLanguageSelection() || modeCashierSelection || modeLock || modeUserChange) {
			hbLanguageBox.setManaged(false);
			hbLanguageBox.setVisible(false);
		}
		else {
			hbLanguageBox.setManaged(true);
			hbLanguageBox.setVisible(true);
		}
    }
    
    protected void loadButtonsLanguageSelection() {
		ButtonsGroupModel botoneraMediosPagos = null;
		try {
			try {
				botoneraMediosPagos = getSceneView().loadButtonsGroup("_language.xml");
			} 
			catch (InitializeGuiException ex) {
				log.info("inicializarComponentes() - The payment methods button panel has not been loaded.: " + ex.getMessage());
			}
			
			if(botoneraMediosPagos != null) {
				ButtonsGroupComponent botonera = new ButtonsGroupComponent(botoneraMediosPagos, hbLanguageBox.getPrefWidth(), hbLanguageBox.getPrefHeight(), this, ActionButtonLanguageComponent.class);
				hbLanguageBox.getChildren().clear();
				hbLanguageBox.getChildren().add(botonera);
			}
		}catch (Exception e) {
			log.error("loadButtonsGroup() - Error: " + e.getMessage(), e);
		}
	}
    
    public void selectLanguage(HashMap<String, String> params) {
		if(!params.containsKey("language") || !params.containsKey("country")){
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("No se ha especificado una acción correcta para este botón"));
        	log.error("No existe el código de idioma pago para este botón.");
        	return;
		}
		String languageCode = params.get("language");
		String country = params.get("country");
		Locale newLocale = new Locale(languageCode, country);
		if(StringUtils.isNotBlank(newLocale.getLanguage())) {
			AppConfig.getCurrentConfiguration().setLanguage(newLocale.getLanguage());
		}
		
		if(StringUtils.isNotBlank(newLocale.getCountry())) {
			AppConfig.getCurrentConfiguration().setCountry(newLocale.getCountry());
		}
		
		Locale.setDefault(new Locale(AppConfig.getCurrentConfiguration().getLanguage(),AppConfig.getCurrentConfiguration().getCountry()));

		refreshUserData();
		lbUser.setText(I18N.getText("Usuario"));
		lbPassword.setText(I18N.getText("Contraseña"));
		lbCloseButton.setText(I18N.getText("SALIR"));
		btStockSearch.setText(I18N.getText("Consultar stock"));
		btAccept.setText(I18N.getText("Aceptar"));
		btCancel.setText(I18N.getText("Cancelar"));
		FormatUtils.getInstance().init(Locale.getDefault());
		destroyScenesBean();
	}
    
    protected void destroyScenesBean() {
		ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(CzzActionScene.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(CzzScene.class));
		Set<BeanDefinition> beans = scanner.findCandidateComponents("com.comerzzia");
		beans.stream().forEach( bd -> {
			try {
				contextHolder.destroyBean(Class.forName(bd.getBeanClassName()));
			} catch (Exception e) {
				log.debug("loadFocus() - An error was thrown destroying the bean "+bd.getBeanClassName(), e);
			}
		});
	}
    
    @Override
    public void onSceneShow() {
    	loggingInProcess = false;
    	
    	if (!AppConfig.getCurrentConfiguration().getDeveloperMode()) {
    		actionClearForm();
    	}
    	
    	super.onSceneShow();
    	refreshUserData();
    	refreshLanguageFlags();
    }
    
	/**
     * Action which logs in
     * @param event 
     */
    public void actionBtAccept(ActionEvent event) {
    	log.debug("actionBtAccept()");
    	
    	event.consume();
    	
    	if (loggingInProcess) {
    		log.warn("Logging request in process ... ignoring");
    		return;
    	}
        
        if (!actionSubmitForm()) return;
        
        loggingInProcess = true;
        
        try {
        	doLogin(tfUser.getText(), tfPassword.getText());
        }
        catch (SesionInitException ex) {
        	if (ex.getCause() != null) {
        		// unknow error
        		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(ex.getMessageDefault(), ex);
        	} else {
        		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(ex.getMessage());
        	}
            actionClearForm();
            initializeFocus();
            loggingInProcess = false;
        } 
        catch (UserInvalidLoginException ex) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(ex.getMessage());
            initializeFocus();
            loggingInProcess = false;
        }        
    }
    
    protected void doLogin(String userCode, String password) throws SesionInitException, UserInvalidLoginException {
    	// ensure permissions update
    	session.getPOSUserSession().clearPermissions();
    	
    	User user = checkValidUser(userCode);
    	
    	// Check user has permission to the current screen
    	if (!hasPermissionsCurrentScreen(user)) {
    		throw new SesionInitException(I18N.getText("El usuario no tiene permisos para ejecutar la pantalla activa."));
    	}
    	
    	if (!hasPermissionsReplace(user)) {
    		throw new SesionInitException(I18N.getText("El usuario no tiene permisos para suplantar una caja abierta."));
    	}
    	
    	session.initPosUserSession(user.getUserCode(), password);
    	
    	doAfterLogin();
    }
    
    protected void doAfterLogin() throws SesionInitException {
    	try {
	        if (isModeCashierSelection()) {
	        	modeCashierSelection = false;
	        	HashMap<String, Object> callbackdata = new HashMap<String, Object>();
	        	callbackdata.put(PARAM_OUTPUT_USER_CHANGE, "S");
	            closeSuccess(callbackdata);
	        }
	        else if(isModeLock()) {
	        	modeLock = false;
	        	HashMap<String, Object> callbackdata = new HashMap<String, Object>();
	        	callbackdata.put(PARAM_OUTPUT_USER_CHANGE, "S");
	            closeSuccess(callbackdata);
	        }
	        else{
	        	openActionScene(AppConfig.getCurrentConfiguration().getMainAction());
	        }    	
    	} catch (Exception e) {
    		log.error("actionBtAccept() - Error no controlado -" + e.getMessage(), e);
    		throw new SesionInitException(e);
    	}
    }
    
    protected User checkValidUser(String userCode) throws UserInvalidLoginException, SesionInitException {
    	try {
			return userService.findByCode(userCode);
		} catch (NotFoundException e) {
			throw new UserInvalidLoginException();
		}
		catch (Exception e) {
			throw new SesionInitException(e);
		}    	
	}
    
    protected boolean hasPermissionsCurrentScreen(User user) throws SesionInitException {
    	ActionDetail action = currentAction!=null?currentAction.getAction():null;
    	if(action != null){
			try {
				EffectiveActionPermissionsDto permisosEfectivos = posUserSession.getEffectiveActionPermissions(user, action, true);
				return permisosEfectivos.isCheckExecution();
			}
			catch (Exception e) {
				throw new SesionInitException(e);
			}
    	}
    	
    	return true;
	}
    
    protected boolean hasPermissionsReplace(User user) throws SesionInitException {
    	try {
    		CashJournalSession cashJournalSession = session.getCashJournalSession();
    		
    		if(cashJournalSession == null || !cashJournalSession.isOpenedCashJournal()) {
    			return true;
    		}
    		
    		if (!StringUtils.equalsIgnoreCase(user.getUserCode(), cashJournalSession.getUserCode())) {
    			ActionDetail action = actionsService.findActionDetailById(AppConfig.getCurrentConfiguration().getMainAction());
    			EffectiveActionPermissionsDto permisosEfectivos = posUserSession.getEffectiveActionPermissions(user, action, true);
    			return permisosEfectivos.isCheckOperationExecution(OPERATION_REPLACE_CASHIER);
    		}

    		return true;
    	}
		catch (Exception e) {
			throw new SesionInitException(e);
		}
    }

        
	@Override
	public boolean canClose() {
		return !isModeLock();
	}

	/**
     * Action which closes the application (Cancel in login window)
     */
    @Override
    public void closeCancel() {
        log.debug("closeCancel()");
        tfUser.clear();
        tfPassword.clear();
        tfUser.requestFocus();
        super.closeCancel();
    }
    
    @Override
    public boolean canCloseCancel() {
    	return !btCancel.isDisabled() && super.canCloseCancel();
    }
    
    // Auxiliar status methods
    
	public boolean isModeCashierSelection() {
		return (modeCashierSelection != null && modeCashierSelection);
	}

	public boolean isModeLock() {
		return (modeLock != null && modeLock);
	}
	
	// Auxiliar form methods
    
    protected void actionClearForm() {
        log.debug("actionClearForm()");
        if(!selectedUser) {
        	tfUser.setText("");
        }
        tfPassword.setText("");
        frLogin.clearForm();
    }
    
    public boolean actionSubmitForm(){
        frLogin.setUser(tfUser.getText());
        frLogin.setPassword(tfPassword.getText());
        return actionValidateForm();
    }

    protected boolean actionValidateForm() {    
		// Clear possible previous errors
    	lbError.setText("");
		frLogin.clearErrorStyle();

		// Validate form
		Set<ConstraintViolation<LoginForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frLogin);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<LoginForm> next = constraintViolations.iterator().next();
			frLogin.setErrorStyle(next.getPropertyPath(), true);
			frLogin.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			return false;
		}
		return true;
    }
    
	public void openItemSearch() {
		log.debug("openItemSearch()");
		sceneData.put(PARAM_IS_STOCK, Boolean.TRUE);
		sceneData.put(PARAM_MODAL, Boolean.TRUE);

		try {
			openItemSearchWindow();
		}
		catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void openItemSearchWindow() throws ClassNotFoundException {
		openScene((Class<SceneController>) Class.forName("com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchController"), new SceneCallback<Map<String, Object>>(){

			@Override
			public void onSuccess(Map<String, Object> callbackData) {
				initializeFocus();
			}
		});
    }

	protected void updateClock() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		lbDate.setText(sdfDate.format(new Date()));

		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		lbTime.setText(sdfTime.format(new Date()));
	}
	
	protected ObservableList<LanguageSelection> getSupportedLocales() {
		List<Language> languages = languageService.findAll();
		List<LanguageSelection> supportedLocales = languages.stream()
					.map(l -> new LanguageSelection(l.getLanguageCode()))
					.collect(Collectors.toList());
		return FXCollections.observableArrayList(supportedLocales);
	}
	
	@Override
	public void onInactivity() {
		closeCancel();
	}

}
