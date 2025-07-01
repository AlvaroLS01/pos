package com.comerzzia.pos.core.gui.login.userselection;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.model.User;
import com.comerzzia.core.facade.service.user.UserSearchParams;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.user.ActionButtonUserComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.user.UserButtonConfigurationBean;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

@Controller
@Scope("prototype")
public class UserSelectionController extends SceneController implements ButtonsGroupController {
    
    private static final Logger log = Logger.getLogger(UserSelectionController.class.getName());
    
    @Autowired
    protected UserServiceFacade userService;
    @Autowired
    protected ModelMapper modelMapper;
    
    public static final String PARAM_SCREEN_MODE_CASHIER = "SCREEN_MODE_CASHIER";
    public static final String PARAM_SCREEN_MODE_LOCK = "SCREEN_MODE_LOCK";
    
    // Button group structure
    public static final int COLUMNS_NUMBER = 4;
    public static final int ROWS_NUMBER = 7;
    public static final int ROWS_NUMBER_FLOATING = 6;
    public int rowsNumber;
    
    public static final String PARAM_INPUT_CUSTOMER = "PARAM_INPUT_CUSTOMER";
    public static final String PARAM_INPUT_RATE_CODE = "PARAM_INPUT_RATE_CODE";
    public static final String PARAM_IS_STOCK = "PARAM_IS_STOCK";
    public static final String PARAM_MODAL = "modal";
    
    @FXML
    protected Button btItemSearch;
    
    @FXML
    protected HBox hbCloseButton;
    @FXML
    protected AnchorPane buttonsGroupPanel, navigationButtonsGroupPanel;
    
    @FXML
    protected Label lbDate, lbTime, lbPOSData, lbCloseButton;
    @FXML
    protected ImageView ivCustomerLogo;
    
    // Users button group
    protected ButtonsGroupComponent buttonsGroup, navigationButtonsGroup;
    
    protected List<User> users;
    
    protected int index;
    
    protected boolean modeChangeCashier;
    
    protected boolean modeTPVLock;
    
    protected ActionSceneController currentAction;
    
    @Autowired
    private Session session;
    
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;
    @Autowired
    protected CzzImageManager czzImageManager;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	UserSearchParams searchParams = new UserSearchParams();
    	searchParams.setActive(true);
        users = userService.findStoreUsers(session.getApplicationSession().getCodAlmacen(), searchParams);
    }
    
    @Override
    public void initializeComponents() {
        index = 0;
        createNavigationButtonsGroup();
        
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
		ivCustomerLogo.setImage(czzImageManager.getLogo(CzzImageManager.IMAGES_CUSTOMER_LOGO_NAME));
    }
    
    @Override
    public void onSceneOpen() throws InitializeGuiException {
        
        if(this.sceneData.containsKey(PARAM_SCREEN_MODE_LOCK)){
            modeChangeCashier = true;
            hbCloseButton.setVisible(false);
            lbCloseButton.setVisible(false);
            rowsNumber = ROWS_NUMBER_FLOATING;
            modeTPVLock = true;
        }
        else if(this.sceneData.containsKey(PARAM_SCREEN_MODE_CASHIER)){
            modeChangeCashier = true;
            hbCloseButton.setVisible(true);
            lbCloseButton.setVisible(true);
            lbCloseButton.setText(I18N.getText("CANCELAR"));
            rowsNumber = ROWS_NUMBER_FLOATING;
            modeTPVLock = false;
        }
        else{
            modeTPVLock = false;
            modeChangeCashier = false;
            hbCloseButton.setVisible(true);
            lbCloseButton.setVisible(true);
            lbCloseButton.setText(I18N.getText("SALIR"));
            rowsNumber = ROWS_NUMBER;
        }
        
        currentAction = (ActionSceneController) sceneData.get(LoginController.PARAM_CURRENT_ACTION);
        createUsersButtonsGroup();
        loadStoreData();
        
    }
    
    @Override
    public void onSceneShow() {
    	super.onSceneShow();
    	lbCloseButton.setText(I18N.getText("SALIR"));
    	btItemSearch.setText(I18N.getText("Consultar stock"));
    }
    
    /**
     * Creates the buttons group of available users to log in
     */
    protected void createUsersButtonsGroup() {
        log.trace("createUsersButtonsGroup()");
        
        final List<ButtonConfigurationBean> actions = new LinkedList<>();

		for (int i = index * COLUMNS_NUMBER, j = 0; i < users.size() && j < rowsNumber * COLUMNS_NUMBER; i++, j++) {
			actions.add(new UserButtonConfigurationBean(null, users.get(i).getUserDes(), "", "ACTION_USER", "FXML", users.get(i)));
		}
        Platform.runLater(new Runnable() {
        	@Override
        	public void run() {
        		try{
        			buttonsGroup = new ButtonsGroupComponent(rowsNumber, COLUMNS_NUMBER, UserSelectionController.this, actions, buttonsGroupPanel.getWidth(), buttonsGroupPanel.getPrefHeight(), ActionButtonUserComponent.class.getName(), 5);
        			buttonsGroup.getStyleClass().add("wrap-login-user");
        			buttonsGroupPanel.getChildren().clear();
        			buttonsGroupPanel.getChildren().add(buttonsGroup);
        		}
        		catch (LoadWindowException ex) {
        			log.error("Error al cargar la botonera de usuarios");
        		}
        	}
        });
    }
    
    /**
     * Creates the buttons group for navigation between users
     */
    protected void createNavigationButtonsGroup(){
        log.trace("createNavigationButtonsGroup()");
        try {
            List<ButtonConfigurationBean> tableActionsList = loadTableActions();
            navigationButtonsGroup = new ButtonsGroupComponent(2, 1, this, tableActionsList, navigationButtonsGroupPanel.getPrefWidth(), navigationButtonsGroupPanel.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            navigationButtonsGroupPanel.getChildren().add(navigationButtonsGroup);}
        catch (LoadWindowException ex) {
            log.error("Error al crear la botonera de desplazamiento entre usuarios");
        }
    }
    
    protected List<ButtonConfigurationBean> loadTableActions() {
    	List<ButtonConfigurationBean> actionsList = new ArrayList<>();
		actionsList.add(new ButtonConfigurationBean("icons/navigate_up.png", null, null, "ACTION_PREVIOUS_USER", "REALIZAR_ACCION"));
		actionsList.add(new ButtonConfigurationBean("icons/navigate_down.png", null, null, "ACTION_NEXT_USER", "REALIZAR_ACCION"));
		return actionsList;
	}
    
    protected void loadStoreData() {
    	String posData = session.getApplicationSession().getCodAlmacen() + " · " + session.getApplicationSession().getStorePosBusinessData().getWarehouse().getWhDes() + " · " + session.getApplicationSession().getTillCode();
        lbPOSData.setText(posData);
    }

	@Override
    public void initializeFocus() {
//        botonCancelar.requestFocus();
    }
    
    @Override
    public void executeAction(ActionButtonComponent actionButton) {
        
        log.trace("executeAction() - Realizando la acción : " + actionButton.getClave() + " de tipo : " + actionButton.getTipo());
        
        switch(actionButton.getClave()){
            case "ACTION_NEXT_USER":
            	createUsersButtonsGroup();
                if(index*COLUMNS_NUMBER+COLUMNS_NUMBER*rowsNumber<users.size()){
                    index++;
                    createUsersButtonsGroup();
                }
                break;
            case "ACTION_PREVIOUS_USER":
                if(index>0){
                    index--;
                    createUsersButtonsGroup();
                }
                break;
            case "ACTION_USER":
                ActionButtonUserComponent button = (ActionButtonUserComponent) actionButton;
                User selectedUser = button.getUsuario();
                
                sceneData.put(LoginController.PARAM_SELECTED_USER, selectedUser.getUserCode());
                if(modeChangeCashier){
                	sceneData.put(LoginController.PARAM_INPUT_MODE_CASHIER_SELECTION, "S");
                    openScene(LoginController.class, new SceneCallback<Map<String, Object>>() {
						
						@Override
						public void onSuccess(Map<String, Object> callbackData) {
							processCloseWindowData(callbackData);
						}
					});
                }
                else{
                	openScene(LoginController.class);
                }
                break;
        }
    }
    
    protected void processCloseWindowData(Map<String, Object> callbackData){
        log.trace("processCloseWindowData()");
        if(callbackData.containsKey(LoginController.PARAM_OUTPUT_USER_CHANGE)){
        	modeTPVLock = false;
        	closeCancel();
        }
    }
    
	public void openItemSearch() {
		log.debug("openItemSearch()");

		sceneData.put(PARAM_INPUT_CUSTOMER, modelMapper.map(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer(), BasketCustomer.class));
		sceneData.put(PARAM_INPUT_RATE_CODE, session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getRateCode());
		sceneData.put(PARAM_IS_STOCK, Boolean.TRUE);
		sceneData.put(PARAM_MODAL, Boolean.TRUE);
		
		try {
	        openItemSearchWindow();
        }
        catch (ClassNotFoundException e) {
	      log.error(e.getMessage(), e);
        }
		initializeFocus();
	}

	@SuppressWarnings("unchecked")
	protected void openItemSearchWindow() throws ClassNotFoundException {
		openScene((Class<SceneController>) Class.forName("com.comerzzia.pos.gui.sales.retail.items.search.ItemSearchController"));
    }
	
    public void actionCancel() {
        log.trace("actionCancel()");
        if(!modeTPVLock){
            closeCancel();
        }
    }
    
    @Override
	public boolean canClose() {
		return !modeTPVLock;
	}
    
    protected void updateClock() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");						
		lbDate.setText(sdfDate.format(new Date()));
		
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		lbTime.setText(sdfTime.format(new Date()));
	}

}
