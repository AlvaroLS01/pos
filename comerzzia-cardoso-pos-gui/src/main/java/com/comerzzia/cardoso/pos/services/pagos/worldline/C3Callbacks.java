package com.comerzzia.cardoso.pos.services.pagos.worldline;

import java.util.HashMap;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.comerzzia.cardoso.pos.gui.configuracion.c3i.dialogo.AutotestDialogoController;
import com.comerzzia.cardoso.pos.gui.configuracion.c3i.dialogo.AutotestDialogoView;
import com.comerzzia.cardoso.pos.gui.ventas.c3i.WorldlineAuxScreenController;
import com.comerzzia.cardoso.pos.gui.ventas.c3i.WorldlineAuxScreenView;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.view.View;
import com.ingenico.fr.jc3api.JC3ApiConstants;
import com.ingenico.fr.jc3api.JC3ApiConstants.C3Keys;
import com.ingenico.fr.jc3api.JC3ApiInterface;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class C3Callbacks extends JC3ApiInterface.JC3ApiCallbacksExt {

	private final Logger log = Logger.getLogger(C3Callbacks.class);
	private final int USER_INPUT_TIMEOUT_SECS = 150;

	// Auxiliar screen parameters
	private Stage mainStage;
	private TextField posDisplay;
	private Button[] NUM_;
	private Button[] ANN_COR_VAL_;

	// Callbacks Parameters
	private boolean forceANNButton;
	private int keyCode;
	private final Object keyCodeLock;
	private C3Timer keyTimer;

	private boolean mostrarPantallaAux;

	public C3Callbacks() {
		this.keyCodeLock = new Object();

		// Es necesario ejecutarlo en el hilo de JavaFX
		Platform.runLater(() -> {
			try {
				View paymentAuxScreen = View.loadView(WorldlineAuxScreenView.class);
				paymentAuxScreen.loadAndInitialize();
				this.mainStage = paymentAuxScreen.getStage();
				this.posDisplay = ((WorldlineAuxScreenController) paymentAuxScreen.getController()).getTfPosDisplay();
				this.NUM_ = ((WorldlineAuxScreenController) paymentAuxScreen.getController()).getNUM();
				this.ANN_COR_VAL_ = ((WorldlineAuxScreenController) paymentAuxScreen.getController()).getANN_COR_VAL();

				Stream.of(NUM_).forEach(number -> number.setOnAction(new ButtonAction()));
				Stream.of(ANN_COR_VAL_).forEach(value -> value.setOnAction(new ButtonAction()));
				// Al iniciar el pinpad no queremos que se muestre la pantalla
				mostrarPantallaAux = false;
			}
			catch (Exception e) {
				log.error(e.getMessage());
			}
		});
	}

	public C3Callbacks(Stage mainStage, TextField posDisplay, Button[] NUM, Button[] ANN_COR_VAL) {
		this.mainStage = mainStage;
		this.posDisplay = posDisplay;
		this.NUM_ = NUM;
		this.ANN_COR_VAL_ = ANN_COR_VAL;
		this.keyCodeLock = new Object();

		Stream.of(NUM_).forEach(number -> number.setOnAction(new ButtonAction()));
		Stream.of(ANN_COR_VAL_).forEach(value -> value.setOnAction(new ButtonAction()));
		mostrarPantallaAux = true;
	}

	@Override
	public void display(String msg, int mode) {
		log.debug(String.format("POS DISPLAY [%s] '%s'", JC3ApiConstants.C3DisplayModes.getInfo(mode), msg));

		// Bypass para los mensajes de firma
		if (msg.contains("FIRMA")) {
			keyCode = C3Keys.C3_KEY_VALIDATION.getKey();
			return;
		}

		keyTimerStop();

		addDisplayMsg(msg);

		disableAllButtons();

		boolean startTimer = false;

		switch (mode) {

			case JC3ApiConstants.C3DSP_WAIT_NONE:

				break;

			case JC3ApiConstants.C3DSP_WAIT_KEY:

				enableAllButtons();

				while (!keyAvailable()) {

					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
					}
				}

				getKey();

				break;

			case JC3ApiConstants.C3DSP_WAIT_1SEC:

				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
				}

				break;

			case JC3ApiConstants.C3DSP_WAIT_NUM:

				enableNUMButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_BREAKABLE:

				enableANNButton();

				break;

			case JC3ApiConstants.C3DSP_WAIT_ANN:

				enableANNButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_WAIT_COR:

				enableCORButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_WAIT_VAL:

				enableVALButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_WAIT_VAL_COR:

				enableCORButton();

				enableVALButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_WAIT_VAL_ANN:

				enableANNButton();

				enableVALButton();

				startTimer = true;

				break;

			case JC3ApiConstants.C3DSP_WAIT_VAL_ANN_COR:

				enableANNButton();

				enableCORButton();

				enableVALButton();

				startTimer = true;

				break;

			default:

				break;

		}

		// ANN button is necessary to abort unsolicited card read process
		if (this.forceANNButton && !isEnabledANNButton()) {
			enableANNButton();
		}

		// start an asynchronous task which will automatically cancel the input in case of user input timeout
		if (startTimer) {

			// Implement timer callback interface
			C3Timer.TimerCallback timerCallback = () -> {
				// Return automatic key upon timeout

				synchronized (keyCodeLock) {

					if (isEnabledANNButton()) {

						keyCode = JC3ApiConstants.C3KEY_CANCELLATION;

					}
					else if (isEnabledCORButton()) {

						keyCode = JC3ApiConstants.C3KEY_CORRECTION;

					}
					else if (isEnabledVALButton()) {

						keyCode = JC3ApiConstants.C3KEY_VALIDATION;

					}
					else if (isEnabledNUMButton()) {

						keyCode = JC3ApiConstants.C3KEY_NUMERIC_0;

					}
					else {

						keyCode = JC3ApiConstants.C3KEY_CANCELLATION;

					}
					log.warn(String.format("No user key input after %d secs ! Aborting with KEY [%s]", USER_INPUT_TIMEOUT_SECS, JC3ApiConstants.C3Keys.getInfo(keyCode)));
				}
			};

			keyTimer = new C3Timer(timerCallback, USER_INPUT_TIMEOUT_SECS);
		}
	}

	protected void keyTimerStop() {
		if (keyTimer != null) {
			// stop the timer (if running)
			keyTimer.stop();
			keyTimer = null;
		}
	}

	public void addDisplayMsg(String msg) {
		if (mostrarPantallaAux && !mainStage.isShowing()) {
			posDisplay.setText(msg);

			try {
				Platform.runLater(() -> POSApplication.getInstance().getMainView().showModalCentered(WorldlineAuxScreenView.class, POSApplication.getInstance().getMainView().getStage()));
			}
			catch (Exception e) {
				log.warn("Ha ocurrido un error al mostrar la pantalla modal: " + e.getMessage());
			}

		}
		else {
			posDisplay.setText(msg);
		}
	}

	public void closeAuxiliarScreen() {
		Platform.runLater(() -> mainStage.close());
	}

	@Override
	public int getKey() {
		// code below is necessary for C3 DLL implementation which does not trigger keyAvailable() callback

		if (!keyAvailable()) {

			return JC3ApiConstants.C3KEY_NOKEY;

		}

		synchronized (keyCodeLock) {

			int key = keyCode;

			keyCode = JC3ApiConstants.C3KEY_NOKEY;

			disableAllButtons();

			log.debug(String.format("POS GET KEY [%s]", JC3ApiConstants.C3Keys.getInfo(key)));

			return key;
		}
	}

	@Override
	public int getSalesConfirmation() {
		// Bypass para las devoluciones
		return JC3ApiConstants.C3KEY_VALIDATION;
	}

	@Override
	public int getSecurity() {
		// Bypass para las devoluciones
		return JC3ApiConstants.C3KEY_VALIDATION;
	}

	@Override
	public int getString(StringBuffer str, int maxlen, String msg) {
		log.debug(String.format("POS GET STRING '%s' (maxlen = %d)", msg, maxlen));

		HashMap<String, Object> datos = new HashMap<>();
		datos.put(AutotestDialogoController.PARAM_TITULO_ENTRADA, msg);

		try {
			POSApplication.getInstance().getMainView().showModalCentered(AutotestDialogoView.class, datos, mainStage);
		}
		catch (Exception e) {
			log.warn("Ha ocurrido un error al mostrar la pantalla modal: " + e.getMessage());
		}

		String inputValue = (String) datos.get(AutotestDialogoController.PARAM_RESPUESTA_SALIDA);

		if (inputValue == null) {

			return JC3ApiConstants.C3KEY_CANCELLATION;

		}

		str.setLength(0);

		str.append(inputValue);
		str.append("");

		return JC3ApiConstants.C3KEY_VALIDATION;
	}

	@Override
	public boolean keyAvailable() {
		synchronized (keyCodeLock) {
			return (keyCode != JC3ApiConstants.C3KEY_NOKEY);
		}

	}

	@Override
	public void printTicket(String ticket) {
	}

	protected void disableANNButton() {
		ANN_COR_VAL_[0].setDisable(true);
	}

	protected void disableCORButton() {
		ANN_COR_VAL_[1].setDisable(true);
	}

	protected void disableVALButton() {
		ANN_COR_VAL_[2].setDisable(true);
	}

	protected void disableNUMButton() {
		for (int i = 0; i < NUM_.length; i++) {
			NUM_[i].setDisable(true);
		}
	}

	protected void disableAllButtons() {
		disableANNButton();
		disableCORButton();
		disableVALButton();
		disableNUMButton();
	}

	protected void enableANNButton() {
		ANN_COR_VAL_[0].setDisable(false);
	}

	protected void enableCORButton() {
		ANN_COR_VAL_[1].setDisable(false);
	}

	protected void enableVALButton() {
		ANN_COR_VAL_[2].setDisable(false);
	}

	protected void enableNUMButton() {
		for (int i = 0; i < NUM_.length; i++) {
			NUM_[i].setDisable(false);
		}
	}

	protected void enableAllButtons() {
		enableANNButton();
		enableCORButton();
		enableVALButton();
		enableNUMButton();
	}

	public void forceANNButton(boolean forceANNButton) {
		this.forceANNButton = forceANNButton;
	}

	protected boolean isEnabledANNButton() {
		return !ANN_COR_VAL_[0].isDisabled();
	}

	protected boolean isEnabledCORButton() {
		return !ANN_COR_VAL_[1].isDisabled();
	}

	protected boolean isEnabledVALButton() {
		return !ANN_COR_VAL_[2].isDisabled();
	}

	protected boolean isEnabledNUMButton() {
		return !NUM_[0].isDisabled();
	}

	private class ButtonAction implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// stop the key timer
			keyTimerStop();
			// check key code
			synchronized (keyCodeLock) {
				if (e.getSource() == ANN_COR_VAL_[0]) {
					keyCode = JC3ApiConstants.C3KEY_CANCELLATION;
				}
				else if (e.getSource() == ANN_COR_VAL_[1]) {
					keyCode = JC3ApiConstants.C3KEY_CORRECTION;
				}
				else if (e.getSource() == ANN_COR_VAL_[2]) {
					keyCode = JC3ApiConstants.C3KEY_VALIDATION;
				}
				else {
					for (int i = 0; i < NUM_.length; i++) {
						if (e.getSource() == NUM_[i]) {
							keyCode = JC3ApiConstants.C3KEY_NUMERIC_0 + i;
						}
					}
				}
			}
		}
	}

	public void setMostrarPantallaAux(boolean mostrarPantallaAux) {
		this.mostrarPantallaAux = mostrarPantallaAux;
	}

}
