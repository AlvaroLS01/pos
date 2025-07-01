

package com.comerzzia.pos.core.gui.components.actionbutton.simple;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.skin.SkinManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class ActionButtonSimpleComponent extends ActionButtonComponent {

    public ActionButtonSimpleComponent() {
        super();

        panelBoton.setMinHeight(40.0);
        panelBoton.setMaxHeight(40.0);
        panelBoton.setMinWidth(40.0);
        panelBoton.setMaxWidth(40.0);

        btAccion.setMinHeight(40.0);
        btAccion.setMaxHeight(40.0);
        btAccion.setMinWidth(40.0);
        btAccion.setMaxWidth(40.0);

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

    }

    /**
     * Inicialización personalizada de componente botón de tipo medio de pago
     *
     * @param ancho
     * @param alto
     */
    @Override
    protected void inicializeCustomComponents(Double ancho, Double alto) {
        Image image = SkinManager.getInstance().getImage(configuracion.getImagePath());
        btAccion.setGraphic(new ImageView(image));
    }
}
