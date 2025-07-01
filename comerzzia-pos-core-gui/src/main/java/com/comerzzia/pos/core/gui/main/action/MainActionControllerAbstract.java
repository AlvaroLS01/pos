package com.comerzzia.pos.core.gui.main.action;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class MainActionControllerAbstract extends ActionSceneController implements Initializable {
	
	@Autowired
	protected CzzImageManager imageManager;
	
	@FXML
	protected ImageView ivMainLogo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void onSceneOpen() {     
    }
    @Override
    public void initializeFocus() {
    }

    @Override
    public void initializeComponents() {
    	showKeyboard = false;
    	Image imageMainLogo = imageManager.getLogo(CzzImageManager.IMAGES_MAIN_LOGO_NAME);
    	ivMainLogo.setImage(imageMainLogo);
    }
    
}
