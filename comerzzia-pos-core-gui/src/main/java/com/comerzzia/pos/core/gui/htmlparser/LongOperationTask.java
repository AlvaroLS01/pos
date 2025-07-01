package com.comerzzia.pos.core.gui.htmlparser;

import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.CzzAlertDialog;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LongOperationTask<V> extends BackgroundTask<V> {
	
	protected SceneController sceneController;
	protected Boolean canRetry;

	public LongOperationTask(SceneController sceneController, Boolean canRetry){
		super(sceneController.getWebView() == null);
		this.sceneController = sceneController;
		this.canRetry = canRetry;
	}

	public LongOperationTask(SceneController sceneController, Boolean canRetry, Boolean waitwindow){
		super(waitwindow);
		this.sceneController = sceneController;
		this.canRetry = canRetry;
	}
	
	@Override
	protected void succeeded() {
		super.succeeded();
		try {
			Platform.runLater(()-> {
				sceneController.getScene().getRoot().setDisable(false);
				taskSucceded();
			});
		}catch(Exception e) {
			log.error("succeeded() - An error was on task succeeded. ", e);
			DialogWindowBuilder.getBuilder(sceneController.getStage()).simpleThrowableDialog(e);
		}
	}
	
	@Override
	protected void failed() {
		super.failed();
		Throwable e = getException();
		log.error("failed() - An error was thrown executing the operation", e);
		
		if(canRetry) {
			String mensajeError = I18N.getText("Ha ocurrido un error al ejecutar la operación.") + System.lineSeparator() + System.lineSeparator() 
            + e.getMessage()  + System.lineSeparator() 
            + I18N.getText("¿Desea reintentar la ejecución?");

			CzzAlertDialog dialog = DialogWindowBuilder.getBuilder(sceneController.getStage())
				.type(AlertType.ERROR)
				.message(mensajeError)
				.beep(true)
				.addButton(new ButtonType(I18N.getText("Reintentar"), ButtonData.OK_DONE))
				.addButton(new ButtonType(I18N.getText("Cancelar"), ButtonData.CANCEL_CLOSE))
				.buildAndShow();
			
			LongOperationTask<V> currentObject = this;
			if(dialog.isAccepted()) {
				new LongOperationTask<V>(sceneController, canRetry) {
					
					@Override
					protected void succeeded() {
						currentObject.succeeded();
					}

					@Override
					protected V execute() throws Exception {
						V value = currentObject.execute();
						currentObject.updateValue(value);
						return value;
					}
					
					@Override
					protected void failed() {
						currentObject.failed();
					}
					
					@Override
					protected void finalize() throws Throwable {
						currentObject.finalize();
					}
				}.start();
				
				return;
			}			

		}else {
			displayExceptionError(e);
		}
		if(sceneController.getWebView()!=null) {
			sceneController.loadWebView("commons/empty", null, sceneController.getWebView());
		}
		sceneController.getScene().getRoot().setDisable(false);
		Platform.runLater(()-> taskFailed());
	}
	
	protected void displayExceptionError(Throwable e) {
		DialogWindowBuilder.getBuilder(sceneController.getStage())
            .simpleThrowableDialog(e);
	}
	
	@Override
	protected void cancelled() {
		super.cancelled();
		Platform.runLater(()-> {
			sceneController.getScene().getRoot().setDisable(false);
			taskEnd();
		});
	}
	
	protected void taskFailed() {
		taskEnd();
	}
	
	protected void taskSucceded() {
		taskEnd();
	}
	
	protected void taskEnd() {
	}
	
	@Override
	public void start() {
		sceneController.getScene().getRoot().setDisable(true);
		if(sceneController.getWebView()!=null) {
			sceneController.loadWebView("commons/loadingData", null, sceneController.getWebView());
		}
		super.start();
	}
}
