package com.comerzzia.pos.core.gui;

import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.controllers.StageController;
import com.comerzzia.pos.core.gui.initialize.PreinitializeController;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MainStageManager extends StageController implements org.springframework.context.ApplicationListener<StageReadyEvent>{
    private final String applicationTitle;
            
    @Autowired
    protected SkinManager skinManager;
    
    protected Timer timer;
    
    public MainStageManager(@Value("${spring.application.ui.title}") String applicationTitle) {
    	this.applicationTitle = applicationTitle;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
    	stage = event.getStage();
    	AppConfigData appConfig = AppConfig.getCurrentConfiguration();
        		
    	stage.setTitle(applicationTitle);
		stage.getIcons().add(skinManager.getImage("logos/logo_comerzzia_icon.png"));
		if(!appConfig.getDeveloperMode()) {
			stage.initStyle(StageStyle.UNDECORATED); // Removed minimize and maximized window
		}
		setMainStageSize(stage);		
        
		showScene(PreinitializeController.class);
		addEventsListeners();
		addInactivityTimer();
		stage.show();		

    }
    
    protected void setMainStageSize(Stage stage) {
    	AppConfigData appConfig = AppConfig.getCurrentConfiguration();
    	
		if (appConfig.getInterfaceInfo().isFullscreen()) { // Modo FULL Screen
			// Establecemos aplicación a pantalla completa
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			// Establecemos el tamaño y posición de la pantalla
			Double boundsWidth = bounds.getWidth();
			Double boundsHeight = bounds.getHeight();
			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(boundsWidth);
			stage.setHeight(boundsHeight);
			stage.setMinWidth(boundsWidth);
			stage.setMinHeight(boundsHeight);
			if(!appConfig.getInterfaceInfo().isShowTaskbar()) {
				stage.setMaximized(true);
			}

		}
		else {
			Integer width = appConfig.getInterfaceInfo().getWidth();
			Integer height = appConfig.getInterfaceInfo().getHeight();
			
			if (appConfig.getDeveloperMode()) {
				height = height + 52;
				stage.setResizable(true);
			}else {
				stage.setMaxWidth(width);
				stage.setMaxHeight(height);
			}

			// Establecemos el tamaño y posición de la pantalla
			stage.setMinWidth(width);
			stage.setMinHeight(height);
			
			stage.setWidth(width);
			stage.setHeight(height);
			stage.centerOnScreen();

		}
	}
    
	protected boolean isModalDialogShowing() {
		StageController focusedStage = getFocusedStageController();

		return focusedStage == null || focusedStage.getStage().getModality() == Modality.APPLICATION_MODAL || focusedStage.getStage().getModality() == Modality.WINDOW_MODAL;
	}

    protected void addInactivityTimer() {
		Integer time = AppConfig.getCurrentConfiguration().getInactivityTimer();
		if (time != null && time > 0) {
			timer = new Timer(time*1000, new java.awt.event.ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(isModalDialogShowing()) {
						return;
					}
					
					Platform.runLater(new Runnable(){
						public void run() {
							try {
								timer.stop();
								if(isModalDialogShowing()) {
									return;
								}
								currentScene.onInactivity();
							}
							finally {
								timer.start();
							}
						}
					});
				}
			});
			stage.addEventFilter(Event.ANY, new EventHandler<Event>(){
				public void handle(Event arg0) {
					if (timer.isRunning()) {
						timer.restart();
					}
				}
			});
			
			timer.start();
		}
	}
    
	public SkinManager getSkinManager() {
		return skinManager;
	}
	
	public SceneController getCurrentScene() {
		return currentScene;
	}
	
	@Override
	public StageController getFocusedStageController() {
		return getChildFocusedStageController();
	}
}
