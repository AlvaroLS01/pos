package com.comerzzia.cardoso.pos.gui.configuracion.c3i;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.worldline.TefWorldlineManager;
import com.comerzzia.cardoso.pos.services.pagos.worldline.C3Callbacks;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.ingenico.fr.jc3api.JC3ApiInterface;
import com.ingenico.fr.jc3api.JC3ApiInterfaceNet;
import com.ingenico.fr.jc3api.JC3ApiParams;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Component
public class WorldlineAutotestController extends Controller {

	private final Logger log = Logger.getLogger(WorldlineAutotestController.class);

	@FXML
	private Button btAnular;
	@FXML
	private Button btCorregir;
	@FXML
	private Button btAceptar;
	@FXML
	private Button btCero;
	@FXML
	private Button btUno;
	@FXML
	private Button btDos;
	@FXML
	private Button btTres;
	@FXML
	private Button btCuatro;
	@FXML
	private Button btCinco;
	@FXML
	private Button btSeis;
	@FXML
	private Button btSiete;
	@FXML
	private Button btOcho;
	@FXML
	private Button btNueve;
	@FXML
	private TextField tfPosDisplay;
	@FXML
	private Button btIniciar;

	@Autowired
	protected PaymentsManager paymentsManager;
	@Autowired
	protected PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	private Button[] NUM;
	private Button[] ANN_COR_VAL;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		NUM = new Button[] { btCero, btUno, btDos, btTres, btCuatro, btCinco, btSeis, btSiete, btOcho, btNueve };
		ANN_COR_VAL = new Button[] { btAnular, btCorregir, btAceptar };
		tfPosDisplay.setEditable(false);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfPosDisplay.clear();
		Stream.of(NUM).forEach(button -> button.setDisable(true));
		Stream.of(ANN_COR_VAL).forEach(button -> button.setDisable(true));
	}

	public void inicarAutotest() throws Exception {
		JC3ApiParams c3AgentParams = new JC3ApiParams();
		c3AgentParams.disableFileCheck();

		c3AgentParams.setPclStartBefore(true);
		c3AgentParams.setPclStopAfter(false);
		c3AgentParams.setPclCompanionType("USB");
		c3AgentParams.setPclBridgeTcpPort("9518");

		URI libUrl = Thread.currentThread().getContextClassLoader().getResource("lib").toURI();
		String lib = new File(libUrl).toString();
		String pcl = lib + "\\ext\\pcl";
		c3AgentParams.setPclDllPath(pcl);

		c3AgentParams.setC3NetStartBefore(false);
		c3AgentParams.setC3NetStopAfter(false);
		c3AgentParams.setC3NetAddress("127.0.0.1:9518");

		c3AgentParams.setC3EmbFilesType("all");
		c3AgentParams.setC3EmbFilesUpload(true);
		c3AgentParams.setC3EmbFilesDir("\\logs-pinpad");
		c3AgentParams.setC3EmbFilesPurge("14");

		c3AgentParams.setC3ApiExtended(true);

		C3Callbacks callbacks = new C3Callbacks(getStage(), tfPosDisplay, NUM, ANN_COR_VAL);
		JC3ApiInterface c3Agent = new JC3ApiInterfaceNet(callbacks, c3AgentParams);

		paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
		Optional<PaymentMethodManager> worldlineManagerOpt = paymentsManager.getPaymentsMehtodManagerAvailables().values().stream().filter(TefWorldlineManager.class::isInstance).findFirst();

		if (!worldlineManagerOpt.isPresent()) {
			throw new InitializeGuiException("No se ha encontrado el manager del medio de pago de Worldline.\nPor favor, revise la configuración.");
		}

		TefWorldlineManager worldlineManager = (TefWorldlineManager) worldlineManagerOpt.get();
		new BackgroundTask<Void>(false){

			@Override
			protected Void call() throws Exception {
				log.debug("BackgroundTask(WorldlineAutotestController) - call() - Iniciando Autotest");
				c3Agent.processC3AutoTest(worldlineManager.getIdTerminal());
				log.debug("BackgroundTask(WorldlineAutotestController) - call() - Autotest finalizado");
				btIniciar.setDisable(false);
				return null;
			}

			@Override
			protected void failed() {
				VentanaDialogoComponent.crearVentanaError(getStage(), "Ha ocurrido un error al ejecutar el Autotest: " + getException().getMessage(), getException());
				super.failed();
			}
		}.start();
		btIniciar.setDisable(true);
	}

}
