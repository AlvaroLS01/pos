package com.comerzzia.bricodepot.pos.dispositivo.fidelizacion.busqueda;

import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bricodepot.pos.util.format.BricoEmailValidator;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import javafx.fxml.FXML;

@Primary
@Component
public class BricoBusquedaFidelizadoController extends BusquedaFidelizadoController {
    
    @Override
    @FXML
    public void accionAceptar() {
        String email = tfEmail.getText();
        
        String error = BricoEmailValidator.getValidationErrorKey(email);
        if (error != null) {
            VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(error, getStage());
            return;
        }
        
        super.accionAceptar();
    }
}
