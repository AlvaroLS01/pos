package com.comerzzia.pos.core.gui.components.waitwindow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

@Component
@CzzScene
public class TimedWaitWindowController extends SceneController {

	private Logger log = Logger.getLogger(TimedWaitWindowController.class);

	public static final String PARAM_TIEMPO_MS = "VentanaEsperaCronometradaController.Tiempo.Milisegundos";
	public static final String PARAM_FORCE_UPDATE = "FORCE_UPDATE";

	@FXML
	protected Label lbTitulo, lbLinea1, lbLinea2, lbCronometro;

	@FXML
	protected Button btAceptar, btCancelar;
	
	protected Timeline timeline;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.info("onSceneOpen() - Arrancando ventana de reinicio automático.");

		boolean forceUpdate = (boolean) sceneData.get(PARAM_FORCE_UPDATE);
		if (forceUpdate) {
			lbLinea2.setVisible(false);
			lbLinea2.setManaged(false);
			btCancelar.setVisible(false);
			btCancelar.setManaged(false);
		}
		
		final Integer milisegundos = (Integer) sceneData.get(PARAM_TIEMPO_MS);

		timeline = new Timeline(new KeyFrame(Duration.millis(1), new Temporizador(milisegundos)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	@Override
	protected void initializeFocus() {
		btAceptar.requestFocus();
	}

	protected class Temporizador implements EventHandler<ActionEvent> {

		protected Integer milisegundos;

		public Temporizador(Integer milisegundos) {
			super();
			this.milisegundos = milisegundos;
		}

		@Override
		public void handle(ActionEvent event) {
			milisegundos--;

			String tiempoFormateado = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milisegundos),
			        TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos)));
			lbCronometro.setText(tiempoFormateado);
			
			if(milisegundos <= 0) {
				aceptar();
			}
		}
	}
	
	public void aceptar() {
		closeSuccess();
	}
	
	@Override
	public boolean canCloseCancel() {
		boolean forceUpdate = (boolean) sceneData.get(PARAM_FORCE_UPDATE);
		return !forceUpdate;
	}

	@Override
	public void close() {
		timeline.stop();
		super.close();
	}

}
