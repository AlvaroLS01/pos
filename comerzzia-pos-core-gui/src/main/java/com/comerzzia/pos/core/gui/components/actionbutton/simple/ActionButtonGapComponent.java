

package com.comerzzia.pos.core.gui.components.actionbutton.simple;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;

import javafx.scene.layout.AnchorPane;


public class ActionButtonGapComponent extends ActionButtonComponent {

    /**
     * Constructor de botón con imagen y dos etiquetas
     */
    public ActionButtonGapComponent() {
        super();

        panelBoton.setMinHeight(80.0);
        panelBoton.setMaxHeight(80.0);
        panelBoton.setMinWidth(60.0);
        panelBoton.setMaxWidth(400.0);
        
        btAccion.setMinHeight(80.0);
        btAccion.setMaxHeight(80.0);
        btAccion.setMinWidth(60.0);
        btAccion.setMaxWidth(400.0);

        panelInterno.setMinHeight(80.0);
        panelInterno.setMaxHeight(80.0);
        panelInterno.setMinWidth(60.0);
        panelInterno.setMaxWidth(400.0);
        panelInterno.setPickOnBounds(true);

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

        btAccion.setGraphic(panelInterno);
        
        panelBoton.getChildren().clear();
    }

    @Override
    protected void inicializeCustomComponents(Double ancho, Double alto) {
    }
}
