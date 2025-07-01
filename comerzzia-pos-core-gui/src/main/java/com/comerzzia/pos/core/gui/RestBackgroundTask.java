
package com.comerzzia.pos.core.gui;

import org.apache.log4j.Logger;

import com.comerzzia.core.commons.exception.BadRequestException;
import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.stage.Stage;

public abstract class RestBackgroundTask<V> extends BackgroundTask<V> {

	protected static final Logger log = Logger.getLogger(RestBackgroundTask.class);

	protected Stage stage;

	protected Callback<V> callback;

	public RestBackgroundTask() {
		super();
	}

	public RestBackgroundTask(Stage stage) {
		super();
		this.stage = stage;
	}

	public RestBackgroundTask(Callback<V> callback, Stage stage) {
		super();
		this.callback = callback;
		this.stage = stage;
	}

	public RestBackgroundTask(Stage stage, boolean mostrarVentanaCargando) {
		super(mostrarVentanaCargando);
		this.stage = stage;
	}

	public RestBackgroundTask(Callback<V> callback, Stage stage, boolean mostrarVentanaCargando) {
		super(mostrarVentanaCargando);
		this.callback = callback;
		this.stage = stage;
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		try {
			callback.succeeded(getValue());
		} catch (Exception e) {
			log.error("succeeded() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	protected void failed() {
		super.failed();
		Throwable e = getException();
		log.error("failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);

		if (e instanceof BadRequestException) {
			handleBadRequestException(e);
		} else if (e instanceof NotFoundException) {
			handleNotFoundException(e);
		} else {
			DialogWindowBuilder.getBuilder(getStage())
					.simpleThrowableDialog(I18N.getText("Error inesperado. Para mas información consulte el log."), e);
		}

		if (callback instanceof FailedCallback) {
			try {
				((FailedCallback<V>) callback).failed(e);
			} catch (Exception e1) {
				log.error("failed() - " + e1.getClass().getName() + " - " + e1.getLocalizedMessage(), e1);
			}
		}
	}

	protected void tratarApiException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void tratarRestException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage())
				.simpleThrowableDialog(I18N.getText("Lo sentimos, ha ocurrido un error en la petición"), e);
	}

	protected void tratarRestTimeoutException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage())
				.simpleThrowableDialog(I18N.getText("El servidor ha tardado demasiado tiempo en responder"), e);
	}

	protected void tratarRestConnectException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage())
				.simpleThrowableDialog(I18N.getText("No se ha podido conectar con el servidor"), e);
	}

	protected void tratarValidationRequestRestException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void tratarValidationDataRestException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void tratarHttpServiceRestException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void tratarNotFoundException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void tratarRestHttpException(Throwable e) {
		DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e.getMessage(), e);
	}

	protected void handleBadRequestException(Throwable e) {
		tratarApiException(e);
	}

	protected void handleNotFoundException(Throwable e) {
		tratarApiException(e);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setCallback(Callback<V> callback) {
		this.callback = callback;
	}

	public interface Callback<V> {
		void succeeded(V result);
	}

	public interface FailedCallback<V> extends Callback<V> {
		void failed(Throwable throwable);
	}

}
