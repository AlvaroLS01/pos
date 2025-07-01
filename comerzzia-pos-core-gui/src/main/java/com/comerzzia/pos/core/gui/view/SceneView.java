package com.comerzzia.pos.core.gui.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.ComponentController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class SceneView {

	private static Logger log = Logger.getLogger(SceneView.class);
	//private static String skin = AppConfig.getCurrentConfiguration().getSkin();
	
    public final static String DEFAULT_ENDING = "view";
    protected FXMLLoader fxmlLoader;
    protected URL resource;
    protected Scene scene;
    
	private ResourceBundle bundle;
	private Parent viewPane;
	
    public SceneView() {
    	ResourceBundle bundle = I18N.getResourceBundle();
    	resource = getFXMLResource();
    	
		this.bundle = bundle;
    }
    public URL getFXMLResource() {
    	return getFXMLResource(getFXMLName());
	}
	public static URL getFXMLResource(String fxmlName) {
		URL resource;
		String resPath = "/skins/" + AppConfig.getCurrentConfiguration().getSkin() + "/" + fxmlName;
    	log.debug("getFXMLResource() - Usando ruta para FXML: " + resPath);
    	resource = SceneView.class.getResource(resPath);
    	if(resource == null){
    		resPath = "/skins/" + AppConfig.getCurrentConfiguration().getDEFAULT_SKIN() + "/" + fxmlName;
    		log.debug("getFXMLResource() - Usando ruta DEFAULT para FXML: " + resPath);
    		resource = SceneView.class.getResource(resPath);
    		if(resource == null){
        		log.error("getFXMLResource() - No se encuentra el FXML asociado a la View");
        	}
    	}
    	return resource;
	}
	public FXMLLoader loadFXML() throws InitializeGuiException {
		return loadFXML(null);
	}
    public <C extends SceneController> FXMLLoader loadFXML(C controllerInstance) throws InitializeGuiException {
    	try{
    		if(fxmlLoader == null){
		    	fxmlLoader = new FXMLLoader(resource, bundle);
		    	fxmlLoader.setController(controllerInstance);
		    	fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
		    		@Override
		    		public Object call(Class<?> defaultControllerClass) {
		    			Object controller = SpringContext.getBean(defaultControllerClass);
		    			if(controller instanceof ComponentController) {
		    				((ComponentController)controller).setParentScene(getController());
		    			}
		    			return SpringContext.getBean(defaultControllerClass);
		    		}
		    	});
		    	fxmlLoader.load();
		    	
		    	Map<String, Object> userData = new HashMap<>();
		    	userData.put("CONTROLLER", fxmlLoader.getController());
		    	scene.setUserData(userData);
		    	
		    	Parent parent = fxmlLoader.getRoot();
		    	
		    	organizeCSS(getClass(), parent);
		    	
		    	viewPane = parent;
    		}
	        return fxmlLoader;
	    } catch (IOException e) {
	    	log.error("loadFXML() - Error al cargar FXML", e);
	    	throw new InitializeGuiException("Error al cargar FXML", e);
	    }
    }
    
    public void reloadI18NBundle() {
        try {
            // Crear nuevo ResourceBundle
            bundle = I18N.getResourceBundle();

            // Reconfigurar FXMLLoader
            SceneController currentController = getController(); // Reutilizamos el Controller existente
            Callback<Class<?>, Object> currentControllerFactory = fxmlLoader.getControllerFactory();
            fxmlLoader = new FXMLLoader(resource, bundle);
			fxmlLoader.setController(currentController); 
			fxmlLoader.setControllerFactory(currentControllerFactory);

            // Recargamos el FXML, el controller ya estaba creado
            Parent newViewPane = fxmlLoader.load();

            // Volvemos a poner el root actualizado
            organizeCSS(getClass(), newViewPane);
            viewPane = newViewPane;
            scene.setRoot(viewPane);

            // ¡Muy importante! Re-inicializar la parte lógica
            SceneController controller = currentController;
            controller.setScene(scene); // Asegurar que la nueva escena sigue asociada
            controller.setSceneView(this);
        } catch (IOException e) {
        	log.error("loadFXML() - Error al cargar FXML tras un cambio de locale", e);
        } 
    }

    public static void organizeCSS(Class<?> clazz, Parent node) {
    	node.getStylesheets().clear();
    	URL resource;
    	
    	//Añadimos la clase y sus clases padre a una lista
    	List<Class<?>> classes = new LinkedList<Class<?>>();
		while(!clazz.equals(Object.class)){
			classes.add(clazz);
			clazz = clazz.getSuperclass();
		}
    	
		//Invertimos el orden de la lista
		Collections.reverse(classes);
		
		
		//Añadimos las rutas a la lista de stylesheets, primero estándar y luego skin
		for (Class<?> class1 : classes) {
			String skin = getCSSName(class1, AppConfig.getCurrentConfiguration().getDEFAULT_SKIN());
	    	resource = SceneView.class.getResource(skin);
	    	if(resource != null){
	    		node.getStylesheets().add(skin);
	    	}
	    	
	    	if(!StringUtils.equals(AppConfig.getCurrentConfiguration().getDEFAULT_SKIN(), AppConfig.getCurrentConfiguration().getSkin())){
		    	skin = getCSSName(class1, AppConfig.getCurrentConfiguration().getSkin());
		    	resource = SceneView.class.getResource(skin);
		    	if(resource != null){
		    		node.getStylesheets().add(skin);
		    	}
	    	}
		}
    }
    
    /**
     * @return nodo Parent del FXML
     */
    public Parent getViewNode() {
    	if(fxmlLoader == null){
                log.error("getViewNode() - fxmlLoader es null");
    		throw new IllegalStateException("Antes debe llamar a loadFXML()");
    	}
    	return viewPane;
    }
    
    /**
     * @return Controller from FXML
     */
    public SceneController getController() {
    	if(fxmlLoader == null){
                log.error("getViewNode() - fxmlLoader es null");
    		throw new IllegalStateException("Antes debe llamar a loadFXML()");
    	}
		Object controllerObj = fxmlLoader.getController();
		if(controllerObj == null){
			log.error("getController() - No se ha encontrado el controller asociado. Location: " + fxmlLoader.getLocation());
			throw new IllegalStateException("No se ha encontrado el controller asociado. Location: " + fxmlLoader.getLocation());
		}
		if(!(controllerObj instanceof SceneController)){
            log.error("getController() - El controller asociado no extiende de la clase SceneController");
		    throw new IllegalStateException("El controller asociado no extiende de la clase SceneController");
		}
		return (SceneController)controllerObj;
    }
    
    public static String getConventionalName(Class<?> clazz, String ending) {
        String clazzName = clazz.getName();
        String packageName = clazzName.substring(0, clazzName.lastIndexOf("."));
        packageName = packageName.replaceAll("\\.", "/");
        return (stripEnding(packageName + "/" + clazz.getSimpleName().toLowerCase())) + ending;
    }

    private static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }
    
    public ButtonsGroupModel loadButtonsGroup() throws InitializeGuiException{
		return loadButtonsGroup("_panel.xml");
	}
    
    public ButtonsGroupModel loadButtonsGroup(String xmlBotonera) throws InitializeGuiException{
		try{
			InputStream is = null;
			Class<?> clazz = getClass();
			while(!clazz.equals(SceneView.class)){
				//Probamos si existe por idioma y pais
				is = loadButtonsGroupInputStreamForLocale(clazz, xmlBotonera, AppConfig.getCurrentConfiguration().getLanguage(), AppConfig.getCurrentConfiguration().getCountry());
				if(is != null){
					break;
				}
				
				//Probamos si existe sólo por idioma
				is = loadButtonsGroupInputStreamForLocale(clazz, xmlBotonera, AppConfig.getCurrentConfiguration().getLanguage(), null);
				if(is != null){
					break;
				}
				
				//Probamos por defecto
				is = loadButtonsGroupInputStreamForLocale(clazz, xmlBotonera, null, null);
				if(is != null){
					break;
				}

				//Probamos con superclass
				clazz = clazz.getSuperclass();
			}
			
			if(is == null){
				log.trace(String.format("loadBotonera() - No se ha encontrado el fichero de botonera %s para la clase %s ni sus clases padre", xmlBotonera, getClass().getName()));
				throw new InitializeGuiException(String.format("No se ha encontrado el fichero de botonera %s para la clase %s ni sus clases padre", xmlBotonera, getClass().getName()));
			}
			
			ButtonsGroupModel panelBotonera = (ButtonsGroupModel) MarshallUtil.readXML(is, ButtonsGroupModel.class);
			return panelBotonera;
		}catch(Exception e){
			log.error("loadBotonera() Error parseando XML :" +e.getMessage(), e );
			throw new InitializeGuiException("Error parseando XML", e);
		}
	}
    
	protected InputStream loadButtonsGroupInputStreamForLocale(Class<?> clazz, String xmlBotonera, String idioma, String pais) {
		String conventionalName = getConventionalName(clazz, xmlBotonera);
		
		String conventionalNameWithoutExt = conventionalName.substring(0, conventionalName.lastIndexOf("."));
		String ext = conventionalName.substring(conventionalName.lastIndexOf("."), conventionalName.length());
		
		String finalName = conventionalNameWithoutExt;
		
		if (idioma != null) {
			finalName = finalName + "_" + idioma;
		}
		if (pais != null) {
			finalName = finalName + "_" + pais;
		}
		
		finalName = finalName + ext;
		
		String resPath = "/skins/" + AppConfig.getCurrentConfiguration().getSkin() + "/" + finalName;
		InputStream is = clazz.getResourceAsStream(resPath);
		if(is == null){
			resPath = "/skins/" + AppConfig.getCurrentConfiguration().getDEFAULT_SKIN() + "/" + finalName;
			is = clazz.getResourceAsStream(resPath);
		}
		return is;
	}

    /**
     * Devuelve el fxml asociado a esta View. Por defecto debe llamarse igual que
     * la subclase de View asociada.
     * @return
     */
    protected String getFXMLName() {
        return getFXMLName(getClass());
    }
    
    public static String getFXMLName(Class<?> clazz) {
        return getConventionalName(clazz, ".fxml");
    }
    
    public static String getCSSName(Class<?> clazz, String skin){
    	return  "/skins/" + skin + "/" + getConventionalName(clazz, ".css");
    }
	public Scene getScene() {
		return scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;
	}
    
    public Stage getStage() {
    	return (Stage) scene.getWindow();
    }
}