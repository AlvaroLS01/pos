

package com.comerzzia.pos.core.gui.components.buttonsgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonGapComponent;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;


public class ButtonsGroupComponent extends AnchorPane {

    // Log
    private static final Logger log = Logger.getLogger(ButtonsGroupComponent.class.getName());

    // Constantes definidas para calcular el tamaño del panel de menú
    private static final int PADDING = 0;

    protected ButtonsGroupController buttonGroupContainer;     // Objeto sobre el que realizaremos las acciones del menú
    protected List<ButtonConfigurationBean> listaConfiguracionBotones; // Listado de acciones del menú
    protected Map<String,ActionButtonComponent> listaControladoresAcciones;
    protected Map<ButtonConfigurationBean, ActionButtonComponent> buttonsConfigurations;
    
    protected int rows;    // filas
    protected int columns; // columnas

    // Componentes de la ventana
    protected GridPane mainPanel;
    
    /**
     * Lista de botones que tiene integrados la botonera.
     */
    protected ArrayList<Button> buttonsList = new ArrayList<>();

    /**
     * Constructor vacío de la botonera. No crea las acciones y utiliza los
     * valores por defecto
     */
    public ButtonsGroupComponent() {
        super();
        getStyleClass().add("botonera");
        
        listaControladoresAcciones = new HashMap<>();
        buttonsConfigurations = new HashMap<>();

        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        
        mainPanel = new GridPane();
        mainPanel.getStyleClass().add("background-botonera");
        GridPane.setHgrow(mainPanel, Priority.ALWAYS);
        mainPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        AnchorPane.setTopAnchor(mainPanel, 0.0);
        AnchorPane.setLeftAnchor(mainPanel, 0.0);
        AnchorPane.setRightAnchor(mainPanel, 0.0);
        
        getChildren().add(mainPanel);
    }

    /**Constructor usando PanelBotoneraBean, que es generado desde XML
     * @throws LoadWindowException */
    public ButtonsGroupComponent(ButtonsGroupModel panelBotonera, Double width, Double height, ButtonsGroupController contenedorBotonera, Class<? extends ActionButtonComponent> clazz) throws LoadWindowException {
    	this();
    	List<ButtonConfigurationBean> listaConfBoton = new ArrayList<>();
    	int filas = panelBotonera.getButtonsRow().size();
    	
    	int cols = 0; //Será el mayor size (solo visibles) de lineaPanel
    	//Comprobar cual es la fila con mayor columnas
    	for (ButtonsGroupPanelRowBean lineaPanel : panelBotonera.getButtonsRow()) {
    		int visCols = countVisibleColumns(lineaPanel.getButtonRow());
    		if(visCols > cols){
    			cols = visCols;
    		}
    	}
    	
    	for (ButtonsGroupPanelRowBean lineaPanel : panelBotonera.getButtonsRow()) {
    		listaConfBoton.addAll(lineaPanel.getButtonRow());
    		//Rellenamos con huecos las filas hasta completar el número máximo de columna de una fila
    		//Para que se respeten el número de líneas definidos en la definición de la botonera del xml
    		int numLineas = countVisibleColumns(lineaPanel.getButtonRow());
    		if(numLineas < cols){
    			while(numLineas < cols){
        			ButtonConfigurationBean configBoton = new ButtonConfigurationBean();
        			configBoton.setType(ButtonConfigurationBean.TYPE_EMPTY);
        			listaConfBoton.add(configBoton);
        			numLineas++;
        		}
    		}
		}
    	
        this.columns = cols;
        this.rows = filas;
        this.listaConfiguracionBotones = listaConfBoton;
        this.buttonGroupContainer = contenedorBotonera;
        inicializeComponents(width, height, clazz.getName(), null);
    }
    
    private int countVisibleColumns(List<ButtonConfigurationBean> lineaBotones) {
    	int cols = 0;
    	for (ButtonConfigurationBean configuracionBotonBean : lineaBotones) {
			if(configuracionBotonBean.isVisible()){
				cols++;
			}
		}
		return cols;
	}
    
    /**
     * Constructor de componente botonera
     *
     * @param filas nº de filas de la botonera
     * @param columnas nº de columnas de la botonera
     * @param contenedorBotonera contenedor de la botonera
     * @param listaAcciones listado de acciones. Un null equivale a que no hay
     * botón en dicha posición
     * @param width anchura de la botonera
     * @param height altura de la botonera
     * @param clase clase del botón que contendrá la botonera
     * @throws com.comerzzia.pos.LoadWindowException.exception.CargarPantallaException
     */
    public ButtonsGroupComponent(int filas, int columnas, ButtonsGroupController contenedorBotonera, List<ButtonConfigurationBean> listaAcciones, Double width, Double height, String clase, Integer separador) throws LoadWindowException {
        this();

        this.columns = columnas;
        this.rows = filas;
        this.listaConfiguracionBotones = listaAcciones;
        this.buttonGroupContainer = contenedorBotonera;
        inicializeComponents(width, height, clase, separador);
    }

	/**
     * Constructor de componente botonera
     *
     * @param filas nº de filas de la botonera
     * @param columnas nº de columnas de la botonera
     * @param contenedorBotonera contenedor de la botonera
     * @param listaAcciones listado de acciones. Un null equivale a que no hay
     * botón en dicha posición
     * @param width anchura de la botonera
     * @param height altura de la botonera
     * @param clase clase del botón que contendrá la botonera
     * @throws com.comerzzia.pos.LoadWindowException.exception.CargarPantallaException
     */
    public ButtonsGroupComponent(int filas, int columnas, ButtonsGroupController contenedorBotonera, List<ButtonConfigurationBean> listaAcciones, Double width, Double height, String clase) throws LoadWindowException {
        this(filas, columnas, contenedorBotonera, listaAcciones, width, height, clase, null);
    }
    
    /**
     * Inicializa los componentes del menú PRE: contenedor menu y lista de
     * acciones deben estar seteadas acciones inicializadas
     *
     * @param ancho de la botonera
     * @param alto de la botonera
     * @param clase clase del botón que contendrá la botonera
     * @throws com.comerzzia.pos.LoadWindowException.exception.CargarPantallaException
     */
    public void inicializeComponents(Double ancho, Double alto, String clase, Integer separador) throws LoadWindowException {
        log.trace("inicializarComponentes() - Creando botonera");
        Double anchocelda = null;
        Double altocelda = null;
        if(ancho != null && ancho < 0){
        	ancho = null;
        }
        if(alto != null && alto < 0){
        	alto = null;
        }

        log.trace("inicializarComponentes() Calculando ancho y alto de celda para el tamaño dado");
        
        separador = separador ==null? 0 : separador;
        if (ancho != null) {
        	anchocelda = (ancho - (2 * (PADDING) + (getColumns() - 1) * separador)) / getColumns();
        	mainPanel.setMinWidth(ancho);
        	mainPanel.setMaxWidth(ancho);
        }
        if (alto != null) {
            altocelda = (alto - (2 * (PADDING) + (getRows() - 1) * separador)) / getRows();
            mainPanel.setMinHeight(alto);
            mainPanel.setMaxHeight(alto);
            setMinHeight(alto);
            setMaxHeight(alto);
        }
        log.trace("inicializarComponentes() - Ancho:" + anchocelda + "   Alto:" + altocelda);

        log.trace("inicializarComponentes() - Procesando acciones para la botonera");
        int row = 0;
        int col = 0;
        for (ButtonConfigurationBean configuracion : getButtonsActionsList()) {
            if (configuracion == null || configuracion.getType().equals(ButtonConfigurationBean.TYPE_EMPTY)) {
                log.trace("inicializarComponentes() - Panel vacío - Acción nula");
                // Insertamos un panel vacío
                AnchorPane apVacio = new AnchorPane();
                if (ancho != null && alto != null) {
                    apVacio.setMinWidth(anchocelda);
                    apVacio.setMinHeight(altocelda);
                    apVacio.setMaxWidth(anchocelda);
                    apVacio.setMaxHeight(altocelda);
                }
                mainPanel.add(apVacio, col, row);
                col++;
            } else {
                try {
                    Class<?> pantallaClass = Class.forName(clase);
                    if (configuracion.getType().equals(ButtonConfigurationBean.TYPE_EMPTY_N)) {
                    	pantallaClass = ActionButtonGapComponent.class;
                    	configuracion.setKey("HUECO");
                    }
                    log.trace("inicializarComponentes() - Insertamos botón con acción :" + configuracion.getKey());
                    ActionButtonComponent boton = (ActionButtonComponent) pantallaClass.newInstance();
                    GridPane.setHalignment(boton, HPos.CENTER);
                    log.trace("inicializarComponentes() - Configurando controlador de botón");
                    // Establecemos la configuración
                    boton.setButtonConfiguration(configuracion);
                    boton.getBtAccion().setUserData(configuracion);

                    // Establecemos el contenedor                    
                    boton.setContenedorMenu(getButtonGroupContainer());

                    log.trace("inicializarComponentes() - Inicializamos componentes del botón");
                    boton.inicializaComponentes(anchocelda, altocelda);
                    listaControladoresAcciones.put(configuracion.getKey(), boton);
                    buttonsConfigurations.put(configuracion, boton);
                    
                    boton.getStyleClass().add(configuracion.getCssClass());
                   
                    //Modificamos tamaño de boton
                    if (alto != null) { 
                    	boton.setMinHeight(altocelda);
                    	boton.setMaxHeight(altocelda);
                    	GridPane.setValignment(boton, VPos.TOP);
                    }else{
                    	AnchorPane.setBottomAnchor(mainPanel, 0.0);
                    }
                    if (ancho != null) {
                        boton.setMinWidth(anchocelda);
                        boton.setMaxWidth(anchocelda);
                    }                
                    // Añadimos el panel
                    if(configuracion.isVisible()){
                    	mainPanel.add(boton, col, row);
                    	col++;
                    }else{
                    	//Si no es visible lo añadimos al contenedor para que no ocupe huecos en el GridPane
                    	boton.setVisible(false);
                    	getChildren().add(boton);
                    }
                    
                    
                    //Guardamos el boton en la listade botones de la botonera
                    buttonsList.add(boton.getBtAccion());
                    
                } catch (ClassNotFoundException ex) {
                    log.error("inicializarComponentes() - No se ha encontrado la clase para el botón de tipo : " + clase + " - mensaje:" + ex.getMessage(), ex);
                    throw new LoadWindowException("Error Interno de la aplicación. Error generando elemento de botonera");
                } catch (InstantiationException ex) {
                    log.error("inicializarComponentes() - No se pudo instanciar la clase para el botón de tipo : " + clase + " - mensaje:" + ex.getMessage(), ex);
                    throw new LoadWindowException();
                } catch (IllegalAccessException ex) {
                    log.error("inicializarComponentes() - No se pudo acceder a la clase : " + clase + " - mensaje:" + ex.getMessage(), ex);
                    throw new LoadWindowException();
                }
            }
            if(anchocelda != null){
        		mainPanel.getColumnConstraints().add(new ColumnConstraints(anchocelda));
        	}
            if(col >= getColumns()){
            	col = 0;
            	if(altocelda != null){
            		mainPanel.getRowConstraints().add(new RowConstraints(altocelda));
            	}
            	row++;
            }
        }
        
        setFocusablesButtons(false);
        checkOperationsPermissions();
    }

    /**
     * Elimina los handlers de teclado del menú en la pantalla
     */
    public void deleteComponents() {
        log.trace("deleteComponents() - Eliminando componente de menú ");
        for (ActionButtonComponent boton : listaControladoresAcciones.values()) {
            boton.deleteComponents();   // Con esto eliminamos los handlers de teclado que hayamos definido en pantalla con el menú
        }
    }

    /**Habilita o deshabilita los botones de tipo operación según los permisos actuales
     * */
    public void checkOperationsPermissions(){
    	for (ButtonConfigurationBean confBoton : listaConfiguracionBotones) {
			if(confBoton != null && confBoton.getType().equals(ButtonConfigurationBean.TYPE_OPERATION)){
				try {
					buttonGroupContainer.checkOperationPermissions(confBoton.getKey());
					buttonsConfigurations.get(confBoton).setDisable(false);
				} catch (PermissionDeniedException e) {
					buttonsConfigurations.get(confBoton).setDisable(true);
				}
			}
		}
    }
    
    /**
     * @return contenedorBotonera
     */
    public ButtonsGroupController getButtonGroupContainer() {
        return buttonGroupContainer;
    }

    /**
     * @param buttonGroupContainer contenedorBotonera a establecer
     */
    public void setButtonGroupContainer(ButtonsGroupController buttonGroupContainer) {
        this.buttonGroupContainer = buttonGroupContainer;
    }

    /**
     * @return listaAcciones
     */
    public List<ButtonConfigurationBean> getButtonsActionsList() {
        return listaConfiguracionBotones;
    }

    /**
     * @return filas
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the filas a establecer
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return columnas
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns the columnas a establecer
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    public void setFocusablesButtons(boolean focusable){
        for (ActionButtonComponent bot : buttonsConfigurations.values()){
            bot.setFocusable(focusable);
        }        
    }
    
    /**
     * Hacemos que un botón de la botonera, correspondiente a un nombre de acción, se habilite o deshabilite
     * @param claveAccion
     * @param disabled 
     */
    public void setAccionDisabled(String claveAccion, boolean disabled){
    	ActionButtonComponent boton = listaControladoresAcciones.get(claveAccion);
    	if (boton ==null){
    		log.debug("setAccionDisabled- No se ha encontrado la acción: "+claveAccion+" para su cambio de estado disable a "+disabled );
    		return;
    	}
    	
    	boton.setActionDisabled(disabled);
    }
    
    /**
     * Hacemos que un botón de la botonera, correspondiente a un nombre de acción, sea visible o invisible
     * @param claveAccion
     * @param visible 
     */
    public void setAccionVisible(String claveAccion, boolean visible){
        ActionButtonComponent boton = listaControladoresAcciones.get(claveAccion);
        if (boton ==null){
            log.debug("setAccionVisible- No se ha encontrado la acción: "+claveAccion+" para su cambio de estado visible a "+visible );
            return;
        }
        
        boton.setVisible(visible);
    }
    
    /**
     *  Devuelve la lista de botones que contiene la botonera.
     * 
     * @return
     */
    public ArrayList<Button> getButtonsList() {
        return buttonsList;
    }
    
    public Button getButtonByKey(String key){
    	ActionButtonComponent component = getButtonGroupButtonByKey(key);
    	
    	if (component != null) return component.getBtAccion();
    	
    	return null;
    }
    
    public ActionButtonComponent getButtonGroupButtonByKey(String key){
    	for (Entry<ButtonConfigurationBean, ActionButtonComponent> entry : buttonsConfigurations.entrySet()) {
			ButtonConfigurationBean config = (ButtonConfigurationBean) entry.getKey();
			if(config.getKey().equals(key)){
				return entry.getValue();
			}
		}
    	return null;
    }
    
    public static List<ButtonConfigurationBean> buildActionsForSimpleTableNavigation(){
    	List<ButtonConfigurationBean> listaAcciones = new ArrayList<>();
    	listaAcciones.add(new ButtonConfigurationBean("icons/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
        listaAcciones.add(new ButtonConfigurationBean("icons/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION"));
        return listaAcciones;
    }

	public Map<ButtonConfigurationBean, ActionButtonComponent> getButtonsConfigurations() {
		return buttonsConfigurations;
	}
    
    public void destroy() {
    	buttonGroupContainer = null;
    	listaConfiguracionBotones.clear();;
    	
    	for(ActionButtonComponent boton : listaControladoresAcciones.values()) {
    		boton.destroy();
    	}
    	listaControladoresAcciones.clear();;

    	
    	for(ActionButtonComponent boton : buttonsConfigurations.values()) {
    		boton.destroy();
    	}
    	buttonsConfigurations.clear();;
    	
    	getChildren().clear();
    	mainPanel = null;
    	buttonsList.clear();
    }
    
}
