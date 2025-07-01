package com.comerzzia.pos.core.gui.view;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ModalSceneView extends SceneView {
    
    private static Logger log = Logger.getLogger(ModalSceneView.class);
    /**Las ModalSceneView tienen su propia stage distinta que la del Application*/
    private Stage stage;
    private Scene scene;
    /**Este es el Nodo por defecto de la escena de esta View*/
    private Region defaultNode;
        
    public ModalSceneView(){
        defaultNode = new Region();
        
        scene = new CssScene(defaultNode);
        
        stage = new Stage(){
        	@Override
        	public void close() {
        		getController().onClose();
        	    super.close();
        	}
        };
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
//				if (!canClose()) {
//					event.consume();
//				}
			}
		});                       
                
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED); //Quitamos los botones minimizar y maximizar
        stage.setIconified(false); // Estilo redondeado y margenes de la pantalla
        stage.setScene(scene);
        addEnterKeyListener();
    }
    
    protected void addEnterKeyListener() {
		stage.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				if(arg0.getCode() == KeyCode.ENTER){
					if(getScene().getFocusOwner() instanceof Button) {
						((Button)getScene().getFocusOwner()).fire();
					}
				}
			}
    		
    	});
	}
    
    public void initializeOwnerStage (Stage stage){
        this.stage.initOwner(stage);
    }
    
    /**Las ModalSceneView tienen su propia stage distinta que la del Application*/
    @Override
    public Stage getStage() {
        return stage;
    }
    
    public void show() {
        show(null, false);
    }
    
    public void showCentered(){
        show(null, true);
    }
    
    public void show(String titulo, boolean center) {
        log.debug("show() - Abriendo ventana modal : "+ getClass().getName());
        
        scene.setRoot(getViewNode());
        
        if(center){
            this.stage.centerOnScreen();
        }else{

//            double ancho = stageController.getMainStage().getWidth();
//            double alto = stageController.getMainStage().getHeight();
//            if (this.stage.getHeight() != alto || this.stage.getWidth() != ancho) {
//	            log.debug("show() - Estableciando ancho: " + ancho + " y alto: " + alto);
//	            this.stage.setX(stageController.getMainStage().getX());
//	            this.stage.setY(stageController.getMainStage().getY() + 0);
//	            this.stage.setWidth(ancho);
//	            this.stage.setHeight(alto);
//            }
        }
        
        if (titulo != null) {
            this.stage.setTitle(I18N.getText(titulo));
        }
        
        AppConfigData appConfig = AppConfig.getCurrentConfiguration();
        
        if(appConfig.getBackgroundModalsEffect()){
        	// Establecemos el estilo para que aparezca borroso el application
        	this.stage.getOwner().getScene().getRoot().setEffect(new GaussianBlur(3));
        }
         
        log.debug("show() - this.stage.showAndWait()");        
        this.stage.showAndWait();
        
        this.stage.getScene().setRoot(defaultNode); //Quitamos el getViewNode() del scene-graph
        
        // Eliminamos el efecto de borroso
        if(appConfig.getBackgroundModalsEffect()){
           this.stage.getOwner().getScene().getRoot().setEffect(null);
        }
    }
    
    public void close() {
        stage.close();
    }
    
}
