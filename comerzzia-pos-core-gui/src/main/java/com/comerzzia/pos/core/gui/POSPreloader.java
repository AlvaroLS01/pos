


package com.comerzzia.pos.core.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.config.Variables;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class POSPreloader extends Preloader {
    
    Stage stage;
    private static final Logger log = Logger.getLogger(POSPreloader.class.getName());
    
    protected Scene createPreloaderScene() {        
        AnchorPane p = new AnchorPane();
        ProgressIndicator cargando = new ProgressIndicator();
        p.getChildren().add(cargando);
        AnchorPane.setTopAnchor(cargando, 22.0);
        AnchorPane.setLeftAnchor(cargando, 0.0);

        // Controlamos la excepción por si no existiese la imagen de logo
        try{
            ImageView logo = new ImageView(createImage("logos/logo_comerzzia.png"));
            p.getChildren().add(logo);
            AnchorPane.setRightAnchor(logo, 0.0);
        }
        catch (Exception e){
            log.error("createPreloaderScene() - Error cargando imagen de logo.");
        }

        Scene scene = new Scene(p, 350, 75);
        addBaseCSS(scene);
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        setBaseTheme();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(createPreloaderScene());      
        stage.centerOnScreen();
        stage.getIcons().add(createImage("logos/logo_comerzzia_icon.png"));
        stage.setTitle("comerzzia POS");
        stage.show();
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            fadeOut();
        }
    }
    
    protected void fadeOut() {
        FadeTransition ft = new FadeTransition(
                Duration.millis(500), 
                stage.getScene().getRoot());
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
               stage.hide();
            }            
        });
        ft.play();
    } 
    
    protected Image createImage(String path){
		try (InputStream is = getClass().getResourceAsStream(getImagePath() + path)) {
			return new Image(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Establece el tema base de JavaFX. En Java7 no cambiamos nada pero
     * si estamos usando Java8, estableceremos el tema "Caspian" en lugar de "Modena" que es el por defecto.
     */
    protected void setBaseTheme() {
    	Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
    }

    protected void addBaseCSS(Scene scene) {
    	String stylesCss = getStylesPath();
        URL resource = getClass().getResource(stylesCss);
        if (resource != null) {
        	scene.getStylesheets().add(stylesCss);
        }
    }
    
    protected String getImagePath() {
    	return "/skins/" + "standard" + Variables.IMAGES_BASE_PATH;
    }
    
    protected String getStylesPath() {
    	return "/skins/"+ "standard" + "/com/comerzzia/pos/gui/styles/styles.css";
    }

}
