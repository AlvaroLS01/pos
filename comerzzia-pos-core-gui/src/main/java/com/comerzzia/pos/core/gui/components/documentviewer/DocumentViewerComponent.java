package com.comerzzia.pos.core.gui.components.documentviewer;

import com.comerzzia.pos.core.gui.components.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j;

/**
 * Componente de visualización de documentos por pantalla
 */
@Log4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class DocumentViewerComponent extends Stage {
    protected Scene scene;
    protected Scene primaryScene; // escena principal.
    
    protected double ancho, alto; // ancho y alto configurado de la pantalla
    protected Modality modalidad;
    protected Window ventanaPadre;
    protected String titulo;
    
    protected BorderPane panelInterno;
    protected VBox vBoxMensage;
    protected WebView taPlantilla;
    
    protected HBox panelBotones;
    protected Button btnOk;
    // </editor-fold>
    
     // <editor-fold defaultstate="collapsed" desc="Clase interna constructora - Construye y establece los parámetros de la nueva ventana">   
    /**
     * Clase interna Constructora.
     */
    public static class Constructor {

        protected static final String ESTILO_VENTANA = "TRANSPARENT";// "UTILITY"; //DECORATED, UNDECORATED, TRANSPARENT, UTILITY
        protected static final boolean CUADRO_DIALOGO_CABECERA_CUADRO = true; // Indica si el cuadro de diálogo tiene titulo de cuadro o no
        protected static final int CUADRO_IMPRESO_MIN_ANCHO = 400;
        protected static final int CUADRO_IMPRESO_MAX_ANCHO = 800;
        protected static final int CUADRO_IMPRESO_MIN_ALTO = 600;
        protected static final int ANCHO_BOTONES_DIALOGO = 90;
        protected static final int ALTO_BOTONES_DIALOGO = 30;

        protected DocumentViewerComponent ventana;

        /**
         * Crea la ventan de dialogo en función de la configuración.
         *
         * @return
         */
        public Constructor create() {
            log.debug("crear() - Creando ventana de diálogo");
            ventana = new DocumentViewerComponent();
            ventana.setResizable(false);

            switch (ESTILO_VENTANA) {
                case "UNDECORATED":
                    ventana.initStyle(StageStyle.UNDECORATED);
                    break;
                case "TRANSPARENT":
                    ventana.initStyle(StageStyle.TRANSPARENT);
                    break;
                case "UTILITY":
                    ventana.initStyle(StageStyle.UTILITY);
                    break;
                default:
                    ventana.initStyle(StageStyle.DECORATED);
                    break;
            }

            // Configuración de la ventana
            ventana.initStyle(StageStyle.DECORATED);
            ventana.centerOnScreen();
            ventana.initModality(Modality.NONE);
            ventana.setIconified(false);
            
            ventana.panelInterno = new BorderPane();
            ventana.panelInterno.setMinWidth(CUADRO_IMPRESO_MIN_ANCHO);
            
            // Layout vertical
            ventana.vBoxMensage = new VBox();
            ventana.vBoxMensage.setMinHeight(CUADRO_IMPRESO_MIN_ALTO);
            ventana.vBoxMensage.setPrefHeight(CUADRO_IMPRESO_MIN_ALTO);
            ventana.vBoxMensage.setAlignment(Pos.CENTER_LEFT);
            
            // Cuadro que contendrá el texto
            ventana.taPlantilla = new WebView();
            ventana.taPlantilla.setMinWidth(CUADRO_IMPRESO_MIN_ANCHO);
            ventana.taPlantilla.setMaxWidth(CUADRO_IMPRESO_MAX_ANCHO);
            ventana.taPlantilla.setMinHeight(CUADRO_IMPRESO_MIN_ALTO);
            
            EventHandler<Event> handler = new EventHandler<Event>(){
				@Override
				public void handle(Event event) {
				}
			};
			ventana.taPlantilla.setOnDragEntered(handler);
			ventana.taPlantilla.setOnDragOver(handler);
			ventana.taPlantilla.setOnDragExited(handler);

            ventana.vBoxMensage.getChildren().add(ventana.taPlantilla);
            ventana.panelInterno.setCenter(ventana.vBoxMensage);
            BorderPane.setAlignment(ventana.vBoxMensage, Pos.CENTER);
            BorderPane.setMargin(ventana.vBoxMensage, new Insets(10, 10, 10, 2 * 10)); //probar con estilos

            // botones
            ventana.panelBotones = new HBox();
            ventana.panelBotones.setSpacing(10); // probar con estilos
            ventana.panelBotones.setAlignment(Pos.BOTTOM_CENTER);
            BorderPane.setMargin(ventana.panelBotones, new Insets(0, 0, 1.5 * 10, 0));
            ventana.panelInterno.setBottom(ventana.panelBotones);
            ventana.panelInterno.widthProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    ventana.panelBotones.layout();
                }
            });

            ventana.scene = new CssScene(ventana.panelInterno);
            ventana.setScene(ventana.scene);

            // Estilos de ventana
            ventana.panelInterno.setId("ventanaVisorImpreso");
            ventana.scene.setFill(Color.TRANSPARENT);            
            
            ventana.taPlantilla.getStyleClass().add("impresion-tickets");

            ventana.panelInterno.setFocusTraversable(false);
        	ventana.taPlantilla.setFocusTraversable(false);
        	ventana.vBoxMensage.setFocusTraversable(false);
        	ventana.panelBotones.setFocusTraversable(false);
        	
        	addEnterKeyListener();

            return this;
        }

		protected void addEnterKeyListener() {
			ventana.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){
            	@Override
            	public void handle(KeyEvent event){
            		if(event.getCode() == KeyCode.ENTER) {
                    	if(ventana.getScene().getFocusOwner() instanceof Button) {
                    		((Button)ventana.getScene().getFocusOwner()).fire();
                    	}
                    }
            	}
            });
		}

        public Constructor setTitulo(String titulo) {
            ventana.titulo = titulo;
            return this;
        }
      
        public Constructor setTextoInterno(String texto){
        	ventana.taPlantilla.getEngine().loadContent(texto);
            return this;
        }

        public Constructor setModalidad(Modality modalidad) {
            ventana.modalidad = modalidad;
            return this;
        }

        public Constructor setVentanaPadre(Window ventanaPadre) {
            ventana.ventanaPadre = ventanaPadre;
            return this;
        }

        /**
         * Añade un botón generico
         *
         * @param cadena cadena de localización de idiomas para el botón
         * @param actionHandler acción que se realiza al pulsar el botón (si no
         * se desea acción, será null)
         * @return
         */
		public Constructor addBoton(String cadena, final EventHandler actionHandler) {

            Button btGenerico = new Button(I18N.getText(cadena));
            // Configuramos el botón
            btGenerico.setMinWidth(ANCHO_BOTONES_DIALOGO);
            btGenerico.setMinHeight(ALTO_BOTONES_DIALOGO);

            // Asignamos la acción
            btGenerico.setOnAction(new EventHandler<ActionEvent>() {
				@Override
                public void handle(ActionEvent t) {
                    if (ventana.primaryScene != null) {
                        ventana.primaryScene.getRoot().setEffect(null); // Eliminamos el efecto del fondo de pantalla
                    }
                    ventana.close();
                    if (actionHandler != null) {
                        actionHandler.handle(t);  // Si se definió un handler para la ventana, se lanza.
                    }
                }
            });

            ventana.panelBotones.getChildren().add(btGenerico);
            return this;
        }

        // Funciones de creación de botones comunes
        public Constructor addBotonAceptar(final EventHandler actionHandler) {
            log.debug("addBotonAceptar()");
            addBoton("Aceptar", actionHandler);
            return this;
        }

        public Constructor addBotonCancelar(final EventHandler actionHandler) {
            log.debug("addBotonCancelar()");
            addBoton("Cancelar", actionHandler);
            return this;
        }

        public Constructor addBotonSi(final EventHandler actionHandler) {
            log.debug("addBotonSi()");
            addBoton("comun.ventana.dialogo.texto.boton.si", actionHandler);
            return this;
        }

        public Constructor addBotonNo(final EventHandler actionHandler) {
            log.debug("addBotonNo()");
            addBoton("comun.ventana.dialogo.texto.boton.no", actionHandler);
            return this;
        }

        public DocumentViewerComponent build() {
            log.debug("build()");
            if (ventana.panelBotones.getChildren().size() == 0) {
                log.error("build() - Error cargando pantalla - El panel ha de tener almenos un botón");
                throw new RuntimeException("El panel ha de tener almenos un botón");
            }
            // Establece el foco
            ventana.panelBotones.getChildren().get(0).requestFocus();
            
            if(ventana.ventanaPadre != null){
                log.debug("crear() - Establecemos efecto modal para el fondo de la escena");
                ventana.primaryScene = ventana.ventanaPadre.getScene();
                //Le ponemos initOwner para que la nueva ventana no aparezca en la barra de tareas
                ventana.initOwner(ventana.ventanaPadre);
            }
            return ventana;
        }
    }

    @Override
	public void showAndWait() {
    	Keyboard keyboard = SpringContext.getBean(Keyboard.class);
    	keyboard.close();
		super.showAndWait();
	}
  
    /**
     * Crea una ventana de información
     *
     * @param texto texto que se mostrará en el componente
     * @param ventanaPadre ventana padre
     * @param primaryStage stage principal
     */
    public static void createDocumentViewer(String texto, Stage primaryStage) {
        log.debug("createDocumentViewer() - " );
        new DocumentViewerComponent.Constructor()
                .create()
                .setVentanaPadre(primaryStage)
                .setTextoInterno(texto)
                .addBotonAceptar(null)
                .build()
                .show();
    }
    
    // </editor-fold>
     
}
