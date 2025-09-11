package com.comerzzia.pos.gui.ventas.tickets.articulos.balanza;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;

@Component
public class SolicitarPesoArticuloController extends WindowController {
	
	private Logger log = Logger.getLogger(SolicitarPesoArticuloController.class);
	
	public static final String PARAM_PESO = "pesoHecho";
	public static final String PARAM_CANCELAR = "pesoCancelado";
	
	@FXML
	protected Button btCancelar;
	
	protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;
	protected BigDecimal peso;

	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
    public void initializeComponents() throws InitializeGuiException {
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){

			IBalanza balanza = Dispositivos.getInstance().getBalanza();

			@Override
			public void run() {
				try {
					BigDecimal pesoNuevo = BigDecimal.valueOf(balanza.getPeso(BigDecimal.ZERO));
					if (!BigDecimalUtil.isIgual(peso, pesoNuevo)) {
						peso = pesoNuevo;
						getDatos().put(PARAM_PESO, peso);
						Platform.runLater(new Runnable(){
							@Override
							public void run() {
								getStage().close();
							}
						});
					}
				}
				catch (Exception e) {
					scheduledThreadPoolExecutor = null;
					log.error("ScheduledThreadPoolExecutor() - Error al solicitar el peso.", e);
				}
			}
		}, 0, AppConfig.milisegundosPeticionPeso, TimeUnit.MILLISECONDS);
    }

	@Override
    public void initializeForm() throws InitializeGuiException {
		peso = BigDecimal.ZERO;
    }

	@Override
    public void initializeFocus() {
		btCancelar.requestFocus();
    }
	
	@Override
	public void accionCancelar() {
		getDatos().put(PARAM_CANCELAR, true);
	    super.accionCancelar();
	}

}
