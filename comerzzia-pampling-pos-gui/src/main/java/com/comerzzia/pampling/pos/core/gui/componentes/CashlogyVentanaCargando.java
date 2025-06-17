package com.comerzzia.pampling.pos.core.gui.componentes;

import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import com.comerzzia.pampling.pos.services.payments.methods.types.CashlogyTask;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CashlogyVentanaCargando extends Stage {
	
	private static Logger log = Logger.getLogger(CashlogyVentanaCargando.class);
	
    static CashlogyVentanaCargando ventana;
    
    protected Scene scene;
        
    protected ProgressIndicator cargando = new ProgressIndicator();
    
    protected BorderPane panelInterno;
    protected VBox vBoxMensage;
    
    protected Timer timer;
    protected int timeoutVentana = 0;
    
    private static Runnable onCancel;

    public static void crearVentanaCargando(Stage stageOwner, Runnable cancelAction) {
        ventana = new CashlogyVentanaCargando();
        onCancel = cancelAction;
        ventana.timer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (ventana.isShowing()) {
                    ventana.timeoutVentana++;
                    log.warn("Ventana progreso activa (" + ventana.timeoutVentana + ")");
                }
            }
        });

        ventana.setResizable(false);
        ventana.initStyle(StageStyle.UNDECORATED); // Popup flotante sin barra superior
        ventana.centerOnScreen();
        ventana.setIconified(false);

        // Panel principal con estilo popup
        ventana.panelInterno = new BorderPane();
        ventana.panelInterno.getStyleClass().add("mainFxmlClass");

        // Mensaje superior
        Label lbMensaje = new Label(I18N.getTexto("Introduzca dinero"));
        VBox vBoxTop = new VBox();
        vBoxTop.setStyle("-fx-alignment: center; -fx-background-radius: 5 5 0 0; -fx-border-width:5;");
        lbMensaje.setStyle("-fx-font-size: 20px;");
        vBoxTop.getChildren().add(lbMensaje);
        vBoxTop.setPadding(new Insets(10));
        ventana.panelInterno.setTop(vBoxTop);

        // Rueda de carga
        ventana.cargando = new ProgressIndicator();
        ventana.cargando.setPrefSize(70, 70);

        Label lbCargando = new Label(I18N.getTexto("Cargando..."));
        VBox vBoxCentro = new VBox(ventana.cargando, lbCargando);
        vBoxCentro.setAlignment(Pos.CENTER);
        vBoxCentro.setSpacing(10);
        vBoxCentro.setPadding(new Insets(10, 10, 10, 10));
        ventana.panelInterno.setCenter(vBoxCentro);

        // Botón Cancelar
        Button btnCancelar = new Button(I18N.getTexto("Cancelar"));
        btnCancelar.getStyleClass().add("btCancelar");
        btnCancelar.setOnAction(event -> {
            log.info("Botón Cancelar pulsado");
            if (onCancel != null) {
                try {
                    onCancel.run();
                } catch (Exception e) {
                    log.error("Error ejecutando acción de cancelación", e);
                }
            }
            cerrar();
        });

        VBox vBoxBottom = new VBox(btnCancelar);
        vBoxBottom.setAlignment(Pos.CENTER);
        vBoxBottom.setPadding(new Insets(0, 10, 15, 10));
        ventana.panelInterno.setBottom(vBoxBottom);

        // Escena final, tamaño compacto (popup)
        ventana.scene = new Scene(ventana.panelInterno, 320, 220);
        ventana.scene.setFill(Color.TRANSPARENT);

        POSApplication.getInstance().addBaseCSS(ventana.scene); // Tu CSS base, si aplica
        ventana.setScene(ventana.scene);

        ventana.initOwner(stageOwner);
        ventana.initModality(Modality.APPLICATION_MODAL); // Bloquea hasta cerrar
    }
	
    public static void mostrar() {
    	log.debug("mostrar()");
        if (ventana != null) {
        	ventana.timeoutVentana = 0;
        	ventana.timer.restart();
			ventana.show();
			if (ventana.getOwner() != null && ventana.getOwner().getScene() != null) {
               ventana.getOwner().getScene().setCursor(Cursor.WAIT);
			}
        }
    }
    
    public static void cerrar() {
    	log.debug("cerrar()");
        if (ventana != null) {
            ventana.close();
            log.debug("cerrar() close");
            if (ventana.getOwner() != null && ventana.getOwner().getScene() != null) {
               ventana.getOwner().getScene().setCursor(Cursor.DEFAULT);
            }
        }
        
    }
}
