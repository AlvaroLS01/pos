package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.serviciosreparto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.ventas.reparto.ServiciosRepartoService;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

@Component
public class SeleccionarServicioRepartoController extends WindowController {

	private Logger log = Logger.getLogger(SeleccionarServicioRepartoController.class);

	public static final String PARAM_CANCELAR = "SeleccionarServicioRepartoController.Cancelar";

	public static final String PARAM_SERVICIO_SELECCIONADO = "SeleccionarServicioRepartoController.ServicioSeleccionado";

	@FXML
	private HBox hbBotonesServicios;

	@Autowired
	private ServiciosRepartoService serviciosRepartoService;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.debug("initializeComponents() - Creando la pantalla de selección de servicios de reparto");
		List<ServicioRepartoDto> serviciosReparto = serviciosRepartoService.getServiciosReparto();
		mostrarBotones(serviciosReparto);
	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Entrando en la pantalla de selección de servicios de reparto");
	}

	private void mostrarBotones(List<ServicioRepartoDto> serviciosReparto) {
		for (ServicioRepartoDto servicio : serviciosReparto) {
			Button boton = new Button();

			ImageView imageView = new ImageView();
			URL urlImagen = serviciosRepartoService.getUrlImage(servicio.getIcono());
			if (urlImagen != null) {
				Image image = new Image(urlImagen.toString());
				imageView.setImage(image);
				boton.setGraphic(imageView);
			}
			else {
				boton.setText(servicio.getNombre());
			}

			boton.setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					seleccionarServicio(servicio);
				}
			});

			hbBotonesServicios.getChildren().add(boton);
		}
	}

	private void seleccionarServicio(ServicioRepartoDto servicio) {
		getDatos().put(PARAM_SERVICIO_SELECCIONADO, servicio);
		getStage().close();
	}

	public void accionCancelar() {
		getDatos().put(PARAM_CANCELAR, true);
		getStage().close();
	}

}
