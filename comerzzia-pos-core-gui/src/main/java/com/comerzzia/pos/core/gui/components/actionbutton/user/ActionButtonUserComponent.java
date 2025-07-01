
package com.comerzzia.pos.core.gui.components.actionbutton.user;

import com.comerzzia.core.facade.model.User;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.user.UserButtonConfigurationBean;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActionButtonUserComponent extends ActionButtonComponent{
    
    protected User usuario;
    
    protected Label lbTexto1, lbTexto2;
    
    protected UserButtonConfigurationBean configuracionBotonUsuario;
    
    public ActionButtonUserComponent(){
        super();
        VBox lineasBox = new VBox();
        
        lbTexto1 = new Label();
        VBox.setVgrow(lbTexto1, Priority.ALWAYS);
        lbTexto1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lbTexto1.setAlignment(Pos.CENTER);
        lbTexto2 = new Label();
        VBox.setVgrow(lbTexto2, Priority.ALWAYS);
        lbTexto2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lbTexto2.setAlignment(Pos.CENTER);
        
        lbTexto1.getStyleClass().add("boton-seleccion-usuario-linea1"); 
        lbTexto2.getStyleClass().add("boton-seleccion-usuario-linea2"); 
        
        lineasBox.getChildren().addAll(lbTexto1, lbTexto2);
        panelInterno.getChildren().add(lineasBox);
        
        AnchorPane.setTopAnchor(lineasBox, 0.0);
        AnchorPane.setBottomAnchor(lineasBox, 0.0);
        AnchorPane.setLeftAnchor(lineasBox, 0.0);
        AnchorPane.setRightAnchor(lineasBox, 0.0);

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
    }
    
    public void setButtonConfiguration(ButtonConfigurationBean configuracion) {
        this.configuracionBotonUsuario = (UserButtonConfigurationBean)configuracion;
        super.setButtonConfiguration(configuracion);
    }
    
    @Override
    protected void inicializeCustomComponents(Double ancho, Double alto) {
        
        lbTexto1.setText(configuracionBotonUsuario.getUsuario().getUserCode());
        lbTexto1.setWrapText(configuracion.isWrapText());
        lbTexto2.setText(configuracionBotonUsuario.getUsuario().getUserDes());
        lbTexto2.setWrapText(configuracion.isWrapText());
        usuario = configuracionBotonUsuario.getUsuario();
    }

    public User getUsuario() {
        return usuario;
    }
    
}
