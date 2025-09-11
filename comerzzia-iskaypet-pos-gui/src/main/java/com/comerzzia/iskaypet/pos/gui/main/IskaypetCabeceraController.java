package com.comerzzia.iskaypet.pos.gui.main;

import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.main.notifications.IskaypetNotificationsWindow;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
@Primary
@Component
public class IskaypetCabeceraController extends com.comerzzia.pos.core.gui.main.cabecera.CabeceraController{

	protected IskaypetNotificationsWindow notificationsTasksWindow;

	@FXML
	public void newLabelsNotifications(){
		this.notificationsTasksWindow.show();
	}

	@Override
	public void initializeComponents(){
		super.initializeComponents();

		boolean isTareasPendientesActivo = true;

		Button boton = new Button();
		String clave = "";
		for(Map.Entry<ConfiguracionBotonBean, BotonBotoneraComponent> entry : this.botonera.getMapConfiguracionesBotones().entrySet()){
			ConfiguracionBotonBean config = entry.getKey();
			clave = config.getClave();
			boton = entry.getValue().getBtAccion();
			boton.getStyleClass().add(clave);

			if(clave.equals("newLabelsNotifications")){
				if(!isTareasPendientesActivo){
					boton.setVisible(false);
					return;
				}
				this.notificationsTasksWindow = new IskaypetNotificationsWindow();
				this.notificationsTasksWindow.getNotificationsPopup().setOpenerButton(boton);
				this.notificationsTasksWindow.getNotificationsPopup().init(getScene());
			}
		}
	}
}