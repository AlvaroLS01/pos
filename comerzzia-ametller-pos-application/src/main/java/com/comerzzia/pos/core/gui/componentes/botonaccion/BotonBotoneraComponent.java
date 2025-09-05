package com.comerzzia.pos.core.gui.componentes.botonaccion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.persistence.core.acciones.operaciones.OperacionBean;
import com.comerzzia.pos.util.i18n.I18N;


public abstract class BotonBotoneraComponent extends AnchorPane {
    
    private static final Logger log = Logger.getLogger(BotonBotoneraComponent.class.getName());
    
    // controlador de evento de teclado del botón
    protected EventHandler<KeyEvent> keyEventHandler; 
    
    // Campos de configuración del botón
    protected ConfiguracionBotonBean configuracion;
       
    // Contenedor del menú - Objeto sobre el que realizaremos las acciones del menú
    protected IContenedorBotonera contenedorMenu; 
    
    // Componentes internos de botón
    protected AnchorPane panelBoton;
    protected AnchorPane panelInterno;
    protected Button btAccion;
      
    public BotonBotoneraComponent() {   
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
               log.debug("accionClick() - Acción : "+configuracion.getClave());
               realizarAccion();               
            }
        });
        
        // Establecemos estilos especificos
         btAccion.getStyleClass().add("boton-botonera");  
         panelInterno.getStyleClass().add("panel-interno-botonera");  
         panelBoton.getStyleClass().add("panel-boton-botonera");  
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
        getContenedorMenu().registraEventoTeclado(keyEventHandler, KeyEvent.KEY_RELEASED);   
        
        inicializaComponentesPersonalizados(ancho,alto);
    }
    
	private void handleKeyEvent(final KeyEvent event) {
		KeyCodeCombination keyCodeCombination = configuracion.getKeyCodeCombination();
		if (keyCodeCombination != null) {
			if (keyCodeCombination.match(event)) {
				log.debug("Handler de botón: - " + configuracion.getTexto() + " - Acción : " + configuracion.getClave());
				event.consume();
				realizarAccion();
			}
		}
	}
    
    protected abstract void inicializaComponentesPersonalizados(Double ancho, Double alto);
    
    /**
     * Realiza las operaciones necesarias para eliminar el componente. Elimina , si existen, los eventos de teclado que ha registrado el componente.
     */
    public void eliminaComponentes(){
        if (keyEventHandler!=null){
	    	log.trace("eliminaComponentes() - Eliminando handler de Teclado para la acción: "+configuracion.getClave());
	        getContenedorMenu().eliminaEventoTeclado(keyEventHandler, KeyEvent.KEY_RELEASED);
        }    
    }
    
    /**
     * Ejecuta la acción asociada al componente
     */
     public void realizarAccion(){
		try {
			log.debug("realizarAccion() - Acción : " + configuracion.getClave());
			if (!btAccion.isDisabled()) { // Evitaremos la acción del botón deshabilitandolo
				switch (configuracion.getTipo()) {
				case ConfiguracionBotonBean.TIPO_ACCION:
					realizarTipoAccion();
					break;
				case ConfiguracionBotonBean.TIPO_OPERACION: //botón operación estará desactivado si el usuario no tiene permiso
					realizarTipoOperacion();
					break;
				case ConfiguracionBotonBean.TIPO_ITEM:
					realizarTipoItem();
					break;
				case ConfiguracionBotonBean.TIPO_METODO:
					realizarTipoMetodo();
					break;
				case ConfiguracionBotonBean.TIPO_PAGO:
					realizarTipoPago();
					break;
				case ConfiguracionBotonBean.TIPO_LABEL:
					realizarTipoLabel();
					break;
				default:
					getContenedorMenu().realizarAccion(this);
					break;
				}
			} else {
				log.debug("realizarAcción()- botón deshabilitado");
			}
    	 }catch(Exception e){
    		 log.error("realizarAccion() - Error al realizar acción: " + e.getMessage(), e);
    		 VentanaDialogoComponent.crearVentanaError(contenedorMenu.getStage(), "Lo sentimos, se ha producido un error al ejecutar la operación.", e);
    	 }
    }

     private void realizarTipoAccion() throws Exception{
		POSApplication.getInstance().getMainView().showActionView(new Long(configuracion.getClave()));
	}
     
    private void realizarTipoOperacion() throws Exception{
    	List<OperacionBean> operaciones = POSApplication.getInstance().getMainView().getCurrentAccion().getOperaciones();
		for (OperacionBean operacion : operaciones) {
			String ejecucion = configuracion.getClave();
			if(operacion.getEjecucion().equals(ejecucion)){
				try{
					contenedorMenu.compruebaPermisos(ejecucion);
					
					if(configuracion.getParametros()!=null){
			        	try {
			        		invokeMethod(toCamelCase(ejecucion), contenedorMenu, configuracion.getParametros());
						} catch (NoSuchMethodException e) {
							//Por compatibilidad con versión anterior, buscamos métodos comenzando por "op"
							invokeMethod(toOpCamelCase(ejecucion), contenedorMenu, configuracion.getParametros());
						}
			        }
			        else{
			            try {
			            	invokeMethod(toCamelCase(ejecucion), contenedorMenu);
						} catch (NoSuchMethodException e) {
							//Por compatibilidad con versión anterior, buscamos métodos comenzando por "op"
							invokeMethod(toOpCamelCase(ejecucion), contenedorMenu);
						}
			        }
					
					break;
				}catch(SinPermisosException e){
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para realizar esta operación"), contenedorMenu.getStage());
				}
			}
		}
	}

	private void realizarTipoItem() throws Exception{
		invokeMethod("addItem", contenedorMenu, configuracion.getClave());
	}
	
	private void realizarTipoLabel() throws Exception{
		invokeMethod("labelArticle", contenedorMenu, configuracion.getClave());
	}

	private void realizarTipoMetodo() throws Exception{
        if(configuracion.getParametros()!=null){
        	invokeMethod(configuracion.getClave(), contenedorMenu, configuracion.getParametros());
        }
        else{
            invokeMethod(configuracion.getClave(), contenedorMenu);
        }
	}
	
	private void realizarTipoPago() throws Exception{
		BigDecimal importe = new BigDecimal(configuracion.getClave());
		invokeMethod("seleccionarImporte", contenedorMenu, importe);
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
    				Method method = clazz.getDeclaredMethod(methodName, paramClass);
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
            log.error("invokeMethod() Error invocando al método "+methodName+" desde botonera");
    	    throw new NoSuchMethodException(methodName);
    	}
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
    public void setContenedorMenu(IContenedorBotonera contenedorMenu) {
        this.contenedorMenu = contenedorMenu;
    }

    /**
     * @return the contenedorMenu
     */
    public IContenedorBotonera getContenedorMenu() {
        return contenedorMenu;
    }

    public ConfiguracionBotonBean getConfiguracionBoton() {
    	return configuracion;
    }

	public void setConfiguracionBoton(ConfiguracionBotonBean configuracion) {
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
        return configuracion.getClave();
    }
    /**
     * @return  tipo de la acción configurada
     */
    public String getTipo() {
        return configuracion.getTipo();
    }

    /**
     * @param key parametro cuyo valor deseamos consultar
     * @return valor del parámetro consultado
     */
    public Object getParametro(String key) {
        log.debug("getContenedorMenu() Obteniendo el parametro + "+key+ " del boton");
        return configuracion.getParametro(key);
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
    	eliminaComponentes();
    	keyEventHandler = null;
    	configuracion = null;
    	contenedorMenu = null;
    	panelBoton = null;
    	panelInterno = null;
    	btAccion = null;
    }
    
}
