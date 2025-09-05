package com.comerzzia.ametller.pos.gui.ventas.tickets.articulos;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.ametller.pos.gui.ventas.tickets.AmetllerTicketManager;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

/**
 * Controlador personalizado que extiende del controlador estándar de
 * facturación para añadir un botón que permite activar/desactivar un
 * descuento automático del 25 % para las líneas que se creen a partir de
 * su pulsación.
 */
@Component
public class AmetllerFacturacionArticulosController extends FacturacionArticulosController {

    /** Botón para activar/desactivar el descuento del 25 %. */
    private Button btnDescuento25;

    @Override
    public void initializeComponents() throws InitializeGuiException {
        super.initializeComponents();

        btnDescuento25 = new Button("25% DESCUENTO");
        AnchorPane.setTopAnchor(btnDescuento25, 5.0);
        AnchorPane.setRightAnchor(btnDescuento25, 5.0);
        btnDescuento25.setOnAction(e -> {
            if (ticketManager instanceof AmetllerTicketManager) {
                AmetllerTicketManager manager = (AmetllerTicketManager) ticketManager;
                boolean activo = manager.toggleDescuento25();
                if (activo) {
                    btnDescuento25.getStyleClass().add("active-discount");
                } else {
                    btnDescuento25.getStyleClass().remove("active-discount");
                }
            }
        });
        panelBotonera.getChildren().add(btnDescuento25);
    }
}
