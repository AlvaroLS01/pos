

package com.comerzzia.pos.core.gui.components.actionbutton.amount;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.skin.SkinManager;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Componente para botonera compuesto por una imagen superior y una etiqueta bajo la imagen
 */
public class ActionButtonAmountComponent extends ActionButtonComponent {
    private static final Logger log = Logger.getLogger(ActionButtonAmountComponent.class.getName());
    
    // Componentes internos de botón
    protected HBox linea1;

    protected ImageView imagen;

    public ActionButtonAmountComponent() {
        super();
        log.trace("BotonBotoneraNormalComponent() - Creando componentes internos del elemento Boton de Botonera");

        panelBoton.setMinHeight(80.0);
        panelBoton.setMaxHeight(80.0);
        panelBoton.setMinWidth(64.0);
        panelBoton.setMaxWidth(400.0);
                        
        btAccion.setMinHeight(80.0);
        btAccion.setMaxHeight(80.0);
        btAccion.setMinWidth(64.0);
        btAccion.setMaxWidth(400.0);

        linea1 = new HBox();
        //linea2 = new HBox();

        imagen = new ImageView();
        imagen.setFitHeight(64);
        imagen.setFitWidth(64);
        imagen.setLayoutX(10);
        
        linea1.getChildren().add(imagen);
        linea1.setAlignment(Pos.CENTER);
      

        HBox.setHgrow(imagen, Priority.ALWAYS);

        panelInterno.setMinHeight(80.0);
        panelInterno.setMaxHeight(80.0);
        panelInterno.setMinWidth(64.0);
        panelInterno.setMaxWidth(120.0);
        panelInterno.setPickOnBounds(true);
        panelInterno.getChildren().addAll(linea1);
        
        AnchorPane.setTopAnchor(linea1, 0.0);
        AnchorPane.setBottomAnchor(linea1, 0.0);
        AnchorPane.setLeftAnchor(linea1, 0.0);
        AnchorPane.setRightAnchor(linea1, 0.0);

        AnchorPane.setTopAnchor(btAccion, 0.0);
        AnchorPane.setBottomAnchor(btAccion, 0.0);
        AnchorPane.setLeftAnchor(btAccion, 0.0);
        AnchorPane.setRightAnchor(btAccion, 0.0);

        btAccion.setPickOnBounds(true);

        AnchorPane.setTopAnchor(this, 0.0d);
        AnchorPane.setBottomAnchor(this, 0.0d);
        AnchorPane.setLeftAnchor(this, 0.0d);
        AnchorPane.setRightAnchor(this, 0.0d);
        AnchorPane.setTopAnchor(panelBoton, 0.0d);
        AnchorPane.setBottomAnchor(panelBoton, 0.0d);
        AnchorPane.setLeftAnchor(panelBoton, 0.0d);
        AnchorPane.setRightAnchor(panelBoton, 0.0d);
        AnchorPane.setTopAnchor(panelInterno, 0.0d);
        AnchorPane.setBottomAnchor(panelInterno, 0.0d);
        AnchorPane.setLeftAnchor(panelInterno, 0.0d);
        AnchorPane.setRightAnchor(panelInterno, 0.0d);
        
        // Establecemos estilos específicos
        btAccion.getStyleClass().add("boton-tipo-medio-pago");        
        
        btAccion.setGraphic(panelInterno);
    }

    @Override
    protected void inicializeCustomComponents(Double ancho, Double alto) {
        log.trace("inicializeCustomComponents() - creando elementos personalizados del botón");
		String rutaImagen = configuracion.getImagePath();
		if (rutaImagen != null) {
			Image image = SkinManager.getInstance().getImage(rutaImagen);
			imagen.setImage(image);
		}
    }
}
