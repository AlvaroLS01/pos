package com.comerzzia.pos.devices.printer;


import java.util.concurrent.TimeoutException;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.model.documents.DocumentIssued;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.CzzAlertDialog;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public class BackgroundPrintTask  {
	
	protected static DocumentIssued<?> buildDocumentIssued(String docTypeCode, String documentUid) {
		DocumentIssued<?> documentIssued = new DocumentIssued<>();
		documentIssued.setDocTypeCode(docTypeCode);
		documentIssued.setDocumentUid(documentUid);
		return documentIssued;
	}
	
	public BackgroundPrintTask(Stage stage, String docTypeCode, String documentUid) {
		this(stage, buildDocumentIssued(docTypeCode, documentUid));
	}
	
	public BackgroundPrintTask(Stage stage, DocumentIssued<?> documentIssued) {
		this(stage, null, documentIssued);
	}

	public BackgroundPrintTask(Stage stage, PrintDocumentRequest printRequest, DocumentIssued<?> documentIssued) {
		new InternalPrintTask(stage, printRequest, documentIssued, false).start();
	}

	
	protected class InternalPrintTask extends BackgroundTask<Void> {
		protected Stage stage;
		protected boolean resetPrinter;
		protected PrintService printService;
		protected DocumentIssued<?> documentIssued;
		protected PrintDocumentRequest printRequest;
		protected boolean canRetry = true;		
	
		public InternalPrintTask(Stage stage, PrintDocumentRequest printRequest, DocumentIssued<?> documentIssued, boolean resetPrinter) {
			super(true, Devices.getInstance().getPrinter1().getTimeoutMillis());
			this.stage = stage;
			this.resetPrinter = resetPrinter;
			this.printService = CoreContextHolder.get().getBean(PrintService.class);
			this.documentIssued = documentIssued;
			this.printRequest = printRequest;
			
			if (this.printRequest == null) {
				this.printRequest = printService.getDefaultPrintSettings(documentIssued.getDocTypeCode());
			}
		}
		
		@Override
		protected Void execute() throws Exception {
			if (resetPrinter) {
				updateProgressMessage(I18N.getText("Reiniciando impresora..."));
				log.info("Print reset....");
				DevicePrinter impresora = Devices.getInstance().getPrinter1();

				if (impresora.reset()) {
					log.info("Printer reset OK");
				}
			}
			updateProgressMessage(I18N.getText("Iniciando impresión..."));

			if (documentIssued.getRawXmlPrintOutput() == null || documentIssued.getRawXmlPrintOutput().length == 0) {
				log.info("Printing from documents service " + documentIssued.getDocTypeCode() + "/" + documentIssued.getDocumentUid());			
				printService.printDocument(documentIssued.getDocumentUid(), documentIssued.getDocTypeCode(), printRequest);			
				log.info("Printing from documents service end " + documentIssued.getDocTypeCode() + "/" + documentIssued.getDocumentUid());
			} else {		
				log.info("Printing from rawXmlPrintOutput " + documentIssued.getDocTypeCode() + "/" + documentIssued.getDocumentUid());
				printService.print(printRequest, documentIssued.getRawXmlPrintOutput(), documentIssued.getDocTypeCode());
				log.info("Printing from rawXmlPrintOutput end " + documentIssued.getDocTypeCode() + "/" + documentIssued.getDocumentUid());
			}
			
			return null;
		}
		
		@Override
		protected void succeeded() {
			super.succeeded();
			
			printSucceded();
		}
		
		@Override
		protected void failed() {
			super.failed();

			Throwable e = getException();

			log.error("BackgroundPrintTask::failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);

			String error = e.getMessage();
			if (e instanceof DeviceException && e.getCause() != null) {
				error = e.getCause().getMessage();
			} else if (e instanceof ThreadDeath || e instanceof TimeoutException) {
				error = I18N.getText("Tiempo de espera agotado. Impresión interrumpida.") + System.lineSeparator();
			}
			CzzAlertDialog errorDialog = errorDialogBuilder(stage,  error, canRetry).buildAndShow();
			handleErrorDialog(stage, printRequest, documentIssued, errorDialog, canRetry);
		}
		
		@Override
		protected void cancelled() {
			super.cancelled();
			DialogWindowBuilder.getBuilder(stage).simpleErrorDialog(I18N.getText("Tiempo de espera agotado. Impresión interrumpida. Es posible que tenga que reiniciar la aplicación."));
			printFailed();
		}
	}
	
	protected DialogWindowBuilder errorDialogBuilder(Stage stage, String message,
			boolean canRetry) {
		DialogWindowBuilder windowBuilder = DialogWindowBuilder.getBuilder(stage).type(AlertType.ERROR);
		String messageError = message;
		if (canRetry) {
			messageError += System.lineSeparator() + I18N.getText("¿Desea reintentar la impresión?");

			windowBuilder.addButton(new ButtonType(I18N.getText("Reintentar"), ButtonData.OK_DONE));
		}
		windowBuilder.addButton(new ButtonType(I18N.getText("Continuar"), ButtonData.CANCEL_CLOSE))
		.beep(true)
		.message(messageError);

		return windowBuilder;
	}
	
	protected void handleErrorDialog(Stage stage, PrintDocumentRequest printRequest, DocumentIssued<?> documentIssued, CzzAlertDialog window, boolean canRetry) {
		if (canRetry && window.isAccepted()) {
			new InternalPrintTask(stage, printRequest, documentIssued, true).start();
		} else {
			printFailed();
		}
	}
	
	public void printSucceded() {
		printEnd();
	}
	
	public void printFailed() {
		printEnd();
	}
	
	public void printEnd() {
		// no default implementation
	}

	

}
