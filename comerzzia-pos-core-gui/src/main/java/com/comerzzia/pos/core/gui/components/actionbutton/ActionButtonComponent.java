package com.comerzzia.pos.core.gui.components.actionbutton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;

import com.comerzzia.core.facade.model.ActionOperationDetail;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


public abstract class ActionButtonComponent extends AnchorPane {
    
    private static final Logger log = Logger.getLogger(ActionButtonComponent.class.getName());
    
    // controlador de evento de teclado del botón
    protected EventHandler<KeyEvent> keyEventHandler; 
    
    // Campos de configuración del botón
    protected ButtonConfigurationBean configuracion;
       
    // Contenedor del menú - Objeto sobre el que realizaremos las acciones del menú
    protected ButtonsGroupController contenedorMenu; 
    
    // Componentes internos de botón
    protected AnchorPane panelBoton;
    protected AnchorPane panelInterno;
    protected Button btAccion;
      
    public ActionButtonComponent() {   
        panelBoton = new AnchorPane();
        panelInterno = new AnchorPane(); 
        btAccion = new Button();
        btAccion.setFocusTraversable(false);
        btAccion.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane.setVgrow(this, Priority.ALWAYS);
        GridPane.setHgrow(this, Priority.ALWAYS);
        
        AnchorPane.setTopAnchor(panelInterno, 0.0);
        AnchorPane.setBottomAnchor(panelInterno, 0.0);
        AnchorPane.setLeftAnchor(panelInterno, 0.0);
        AnchorPane.setRightAnchor(panelInterno, 0.0);        
        AnchorPane.setTopAnchor(panelBoton, 0.0);
        AnchorPane.setBottomAnchor(panelBoton, 0.0);
        AnchorPane.setLeftAnchor(panelBoton, 0.0);
        AnchorPane.setRightAnchor(panelBoton, 0.0);        
        AnchorPane.setTopAnchor(btAccion, 0.0);
        AnchorPane.setBottomAnchor(btAccion, 0.0);
        AnchorPane.setLeftAnchor(btAccion, 0.0);
        AnchorPane.setRightAnchor(btAccion, 0.0);        
        btAccion.setPickOnBounds(true);   
                
        panelBoton.getChildren().add(btAccion);               
        this.getChildren().add(panelBoton);
        
        // Creamos acción click del botón
        btAccion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               log.debug("accionClick() - Acción : "+configuracion.getKey());
               realizarAccion();               
            }
        });
    }
    
    /**
     *  Función que inicializa el componente. Crea y registra el evento de teclado y establece el alto y ancho del componente
     * @param ancho que tendrá la botonera generada
     * @param alto que tendrá la botonera generada
     */
    public void inicializaComponentes(Double ancho, Double alto){        
        if (ancho !=null){
            this.setMaxWidth(ancho);
            btAccion.setMaxWidth(ancho);
            panelBoton.setMaxWidth(ancho);
            panelInterno.setMaxWidth(ancho);
        }else{
        	//Por defecto todos los botones tienen el mismo prefWidth y crecen
        	setPrefWidth(0d);
        	GridPane.setHgrow(this, Priority.ALWAYS);
        }
        if (alto !=null){
            this.setMaxHeight(alto);
            btAccion.setMaxHeight(alto);
            panelBoton.setMaxHeight(alto);             
            panelInterno.setMaxHeight(alto);             
        }
               
        keyEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent event) {
               handleKeyEvent(event);
            }
        };   
        // Insertamos el evento de botón al panel
        getContenedorMenu().addKeyEventHandler(keyEventHandler, KeyEvent.KEY_RELEASED);   
        
        inicializeCustomComponents(ancho,alto);
    }
    
	private void handleKeyEvent(final KeyEvent event) {
		KeyCodeCombination keyCodeCombination = configuracion.getKeyCodeCombination();
		if (keyCodeCombination != null) {
			if (keyCodeCombination.match(event)) {
				log.debug("Handler de botón: - " + configuracion.getText() + " - Acción : " + configuracion.getKey());
				event.consume();
				realizarAccion();
			}
		}
	}
    
    protected abstract void inicializeCustomComponents(Double ancho, Double alto);
    
    /**
     * Realiza las operaciones necesarias para eliminar el componente. Elimina , si existen, los eventos de teclado que ha registrado el componente.
     */
    public void deleteComponents(){
        if (keyEventHandler!=null){
	    	log.trace("eliminaComponentes() - Eliminando handler de Teclado para la acción: "+configuracion.getKey());
	        getContenedorMenu().removeKeyEventHandler(keyEventHandler, KeyEvent.KEY_RELEASED);
        }    
    }
    
    /**
     * Ejecuta la acción asociada al componente
     */
     public void realizarAccion(){
		try {
			log.debug("realizarAccion() - Acción : " + configuracion.getKey());
			if (!btAccion.isDisabled()) { // Evitaremos la acción del botón deshabilitandolo
				switch (configuracion.getType()) {
				case ButtonConfigurationBean.TYPE_ACTION:
					realizarTipoAccion();
					break;
				case ButtonConfigurationBean.TYPE_OPERATION: //botón operación estará desactivado si el usuario no tiene permiso
					realizarTipoOperacion();
					break;
				case ButtonConfigurationBean.TYPE_ITEM:
					realizarTipoItem();
					break;
				case ButtonConfigurationBean.TYPE_METHOD:
					realizarTipoMetodo();
					break;
				case ButtonConfigurationBean.TYPE_PAYMENT:
					realizarTipoPago();
					break;
				case ButtonConfigurationBean.TYPE_LABEL:
					realizarTipoLabel();
					break;
				default:
					getContenedorMenu().executeAction(this);
					break;
				}
			} else {
				log.debug("realizarAcción()- botón deshabilitado");
			}
    	 }catch(Exception e){
    		 log.error("realizarAccion() - Error al realizar acción: " + e.getMessage(), e);
    		 DialogWindowBuilder.getBuilder(contenedorMenu.getStage()).simpleThrowableDialog(I18N.getText("Lo sentimos, se ha producido un error al ejecutar la operación."),e);
    	 }
    }

     private void realizarTipoAccion() throws Exception{
    	 contenedorMenu.openActionScene(new Long(configuracion.getKey())); //TODO getstagecontroller
	}
     
    private void realizarTipoOperacion() throws Exception{
    	if(!(contenedorMenu instanceof ActionSceneController)) {
    		throw new Exception("realizarTipoOperacion() - No action detected");
    	}
    	
    	ActionSceneController buttonsContainer = (ActionSceneController)contenedorMenu;
    	String execution = configuracion.getKey();
    	    	
    	for(ActionOperationDetail operation: buttonsContainer.getAction().getOperations()) {
    		if(operation.getExecution().equals(execution)) {
    			try{
    				contenedorMenu.checkOperationPermissions(execution); 
    	
    				if(configuracion.getParams()!=null) {
    					buttonsContainer.auditOperation(new ComerzziaAuditEventBuilder().addOperation(execution).addField("parameters", configuracion.getParams()));
    					
    					try {
    						invokeMethod(toCamelCase(execution), contenedorMenu, configuracion.getParams());
    					}catch (NoSuchMethodException e) {
    						//Por compatibilidad con versión anterior, buscamos métodos comenzando por "op"
    						invokeMethod(toOpCamelCase(execution), contenedorMenu, configuracion.getParams());
    					}
    				}else{
    					buttonsContainer.auditOperation(new ComerzziaAuditEventBuilder().addOperation(execution));
    					
    					try {
    						invokeMethod(toCamelCase(execution), contenedorMenu);
    					} catch (NoSuchMethodException e) {
    						//Por compatibilidad con versión anterior, buscamos métodos comenzando por "op"
    						invokeMethod(toOpCamelCase(execution), contenedorMenu);
    					}
    				}
    				return;
    				
    			}catch(PermissionDeniedException e){
					DialogWindowBuilder.getBuilder(contenedorMenu.getStage()).simpleWarningDialog(I18N.getText("No tiene permisos para realizar esta operación"));
				}
    		}
    	}
    	log.error("Operation '" + execution + "' not found in action '" + buttonsContainer.getAction().getAction() + "'");
	}

	private void realizarTipoItem() throws Exception{
		invokeMethod("addItem", contenedorMenu, configuracion.getKey());
	}
	
	private void realizarTipoLabel() throws Exception{
		invokeMethod("labelArticle", contenedorMenu, configuracion.getKey());
	}

	private void realizarTipoMetodo() throws Exception{
        if(configuracion.getParams()!=null){
        	invokeMethod(configuracion.getKey(), contenedorMenu, configuracion.getParams());
        }
        else{
            invokeMethod(configuracion.getKey(), contenedorMenu);
        }
	}
	
	private void realizarTipoPago() throws Exception{
		BigDecimal importe = new BigDecimal(configuracion.getKey());
		invokeMethod("addDefaultPayment", contenedorMenu, importe);
	}
    
    /**Llama al método pasado por parámetro en el objeto, el método puede estar en su clase o sus clases padre.
     * @throws NoSuchMethodException */
    private void invokeMethod(String methodName, Object obj, Object... parameters) throws NoSuchMethodException, InvocationTargetException{
    	Class<?> clazz = obj.getClass(); 
    	do{
    		try{
    			if(parameters.length == 0){
    				Method method = clazz.getDeclaredMethod(methodName);
    				method.setAccessible(true);
    				method.invoke(obj);
    			}else{
    				Class<?> paramClass = parameters[0].getClass();
    				Method method = getDeclaredMethod(clazz, methodName, paramClass);
    				method.setAccessible(true);
    				method.invoke(obj, parameters);
    			}
    			//Método ejecutado, salimos del do-while
    			break;
    		}catch(IllegalAccessException | IllegalArgumentException | 
    				NoSuchMethodException | SecurityException e){
    			//No existe el método, probamos en la clase padre
    			clazz = clazz.getSuperclass();
    		}
    	}while(clazz != null);
    	//Si clazz es null es porque el do-while ha llegado hasta el final y no ha encontrado el método
    	if(clazz == null){
            log.error("invokeMethod() Error invocando al método "+methodName+" desde botonera en la case " + obj.getClass().getName());
    	    throw new NoSuchMethodException(methodName);
    	}
    }

	protected Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?> paramClass)
			throws NoSuchMethodException {
		try {
			return clazz.getDeclaredMethod(methodName, paramClass);
		} catch (NoSuchMethodException e) {
			for (Class<?> c : paramClass.getInterfaces()) {
				// Si la clase no implementa el método con la clase como parámetro se iteran sus interfaces
				try {
					return clazz.getDeclaredMethod(methodName, c);
				} catch (NoSuchMethodException e1) {
					// Ignorar
				}
			}
		}
		
		throw new NoSuchMethodException(methodName);
	}
    
    private String toOpCamelCase(String spaceSeparatedWords){
    	String camelCased = "op";
    	String[] words = spaceSeparatedWords.split(" ");
    	for(int i = 0; i < words.length; i++){
    		camelCased += words[i].substring(0, 1).toUpperCase() + words[i].substring(1, words[i].length()).toLowerCase();
    	}
    	return camelCased;
    }

    private String toCamelCase(String spaceSeparatedWords){
    	String camelCased = "";
    	String[] words = spaceSeparatedWords.split(" ");
    	for(int i = 0; i < words.length; i++){
    		if (i == 0) {
    			camelCased += words[i].toLowerCase();
    		} else {
    			camelCased += words[i].substring(0, 1).toUpperCase() + words[i].substring(1, words[i].length()).toLowerCase();
    		}
    	}
    	return camelCased;
    }
    
	/**
     * @param contenedorMenu Contenedor de menú 
     */
    public void setContenedorMenu(ButtonsGroupController contenedorMenu) {
        this.contenedorMenu = contenedorMenu;
    }

    /**
     * @return the contenedorMenu
     */
    public ButtonsGroupController getContenedorMenu() {
        return contenedorMenu;
    }

    public ButtonConfigurationBean getConfiguracionBoton() {
    	return configuracion;
    }

	public void setButtonConfiguration(ButtonConfigurationBean configuracion) {
        this.configuracion = configuracion;
    }

    /**
     * Inicializa los componentes específicos del tipo de botón de la botonera
     * @param ancho
     * @param alto 
     */
   

    /**
     * @return nombre de la acción configurada
     */
    public String getClave() {
        return configuracion.getKey();
    }
    /**
     * @return  tipo de la acción configurada
     */
    public String getTipo() {
        return configuracion.getType();
    }

    /**
     * @param key parametro cuyo valor deseamos consultar
     * @return valor del parámetro consultado
     */
    public Object getParametro(String key) {
        log.debug("getContenedorMenu() Obteniendo el parametro + "+key+ " del boton");
        return configuracion.getParam(key);
    }
    
    /**
     * Establece si un botón esta deshabilitado o no
     * @param disable 
     */
    public void setActionDisabled(boolean disable){
        btAccion.setDisable(disable);
    }
    
    public Button getBtAccion(){
        return btAccion;
    }
    
    public void setFocusable(boolean focusable){
    	setFocusTraversable(false);
    	panelBoton.setFocusTraversable(false);
    	panelInterno.setFocusTraversable(false);
    	btAccion.setFocusTraversable(focusable);
    }
    
    public void destroy() {
    	deleteComponents();
    	keyEventHandler = null;
    	configuracion = null;
    	contenedorMenu = null;
    	panelBoton = null;
    	panelInterno = null;
    	btAccion = null;
    }
    
}
