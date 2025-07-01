package com.comerzzia.pos.core.gui.components.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.StageStyle;
import javafx.stage.Window;

@Component
@Scope("prototype")
public class DialogWindowBuilder {
	
	protected AlertType alertType = AlertType.INFORMATION;
	protected Window parentWindow;
	protected String title;
    protected String mainMessage;
    protected String message;
    protected Throwable exception;
    protected StageStyle STAGE_STYLE = StageStyle.UNDECORATED;
    protected List<ButtonType> buttons = new ArrayList<>();
    protected boolean beep = true;
    
    protected EventHandler<DialogEvent> onShowing;
    protected EventHandler<DialogEvent> onHiding;
    
    protected Window parentStage;
    
    protected Node content;
    protected Node expandableContent;
    protected Node headerContent;
    protected Node graphic;
    
    protected DialogWindowBuilder(Window parentStage) {
    	this.parentStage = parentStage;
    }
    
    /**
     * Set the alert type. Possible types are:
     * <ul>
     * <li>INFORMATION</li>
     * <li>CONFIRMATION</li>
     * <li>WARNING</li>
     * <li>ERROR</li>
     * </ul>
     * See {@link AlertType}
     * Defaults to INFORMATION
     * @param alertType
     * @return
     */
	public DialogWindowBuilder type(AlertType alertType) {
		this.alertType = alertType;
		return this;
	}
	
	public DialogWindowBuilder title(String title) {
		this.title = title;
        return this;
	}

	public DialogWindowBuilder message(String message) {
		this.message = message;
		return this;
	}
	
	/**
	 * Add a button to the dialog. <br>
	 * The order in which the buttons are displayed is defined by the button data and is dependent on the operating system. <br>
	 * <br>
	 * See {@link ButtonType} for the list of possible buttons. <br>
	 * See {@link javafx.scene.control.ButtonBar.ButtonData ButtonData} for the list of possible button data. <br>
	 * See {@link javafx.scene.control.ButtonBar ButtonBar} for the ordering on the different operating systems.
	 * @param buttonType
	 * @return
	 */
	public DialogWindowBuilder addButton(ButtonType buttonType) {
		buttons.add(buttonType);
		return this;
	}

	/**
	 * Set the buttons to be displayed in the dialog. <br>
	 * The order in which the buttons are displayed is defined by the button data and is dependent on the operating system. <br>
	 * <br>
	 * See {@link ButtonType} for the list of possible buttons. <br>
	 * See {@link javafx.scene.control.ButtonBar.ButtonData ButtonData} for the list of possible button data. <br>
	 * See {@link javafx.scene.control.ButtonBar ButtonBar} for the ordering on the different operating systems.
	 * @param buttonType
	 * @return
	 */
	public DialogWindowBuilder buttons(List<ButtonType> buttonType) {
		buttons = buttonType;
		return this;
	}
	
	/**
	 * Set the exception to be displayed in the dialog. The stack trace of the exception will be displayed in a expandable area.
	 * If the exception is set the alert type will be automatically set to ERROR.
	 * @param exception
	 * @return
	 */
	public DialogWindowBuilder exception(Throwable exception) {
		this.exception = exception;
		if(exception!=null) {
			this.alertType = AlertType.ERROR;
		}
		return this;
	}
	
	public DialogWindowBuilder mainMessage(String mainMessage) {
		this.mainMessage = mainMessage;
		return this;
	}

	public DialogWindowBuilder beep(boolean beep) {
		this.beep = beep;
		return this;
	}
	
	/**
	 * Override the default showing event
	 * @param onShowing
	 * @return
	 */
	public DialogWindowBuilder onShowing(EventHandler<DialogEvent> onShowing) {
		this.onShowing = onShowing;
		return this;
	}
	
	/**
	 * Override the default hiding event
	 * 
	 * @param onHiding
	 * @return
	 */
	public DialogWindowBuilder onHiding(EventHandler<DialogEvent> onHiding) {
		this.onHiding = onHiding;
		return this;
	}
	
	/**
	 * Set the content of the dialog. This will replace the default content of the
	 * dialog.
	 * 
	 * @param content
	 * @return
	 */
	public DialogWindowBuilder content(Node content) {
		this.content = content;
		return this;
	}

	/**
	 * Set the content of the dialog's header. This will replace the default content of the
	 * dialog's header.
	 * 
	 * @param content
	 * @return
	 */
	public DialogWindowBuilder headerContent(Node headerContent) {
		this.headerContent = headerContent;
		return this;
	}
	
	/**
	 * Set the graphic of the dialog. This will replace the default image of the
	 * dialog.
	 * 
	 * @param graphic
	 * @return
	 */
	public DialogWindowBuilder graphic(Node graphic) {
		this.graphic = graphic;
		return this;
	}
	
	/**
	 * Set the expandable content of the dialog. This will be displayed in an
	 * expandable area below the main message and will replace the exception content.
	 * 
	 * @param expandableContent
	 * @return
	 */
	public DialogWindowBuilder expandableContent(Node expandableContent) {
		this.expandableContent = expandableContent;
		return this;
	}

    public CzzAlertDialog build() {
    	
    	if (message == null) {
    		message = I18N.getText("Error inesperado. Para mas información consulte el log.");
		}
    	
    	if (buttons.isEmpty()) {
    		buttons.add(ButtonType.OK);
    	}
    	
    	
    	CzzAlertDialog dialog = CoreContextHolder.get().getBean(CzzAlertDialog.class, alertType, message, buttons.toArray(new ButtonType[buttons.size()]));
        dialog.setBeep(beep);
        dialog.initOwner(parentStage);
        
		if (exception != null) {
			dialog.createThrowableContent(exception);
		}

		if (StringUtils.isNotBlank(title)) {
			dialog.setTitle(title);
		}

		if (StringUtils.isNotBlank(mainMessage)) {
			dialog.setHeaderText(mainMessage);
		}
		
		if(onShowing != null) {
			dialog.setOnShowing(onShowing);
		}
		
		if(onHiding != null) {
			dialog.setOnHiding(onHiding);
		}
		
		if (headerContent != null) {
			dialog.getDialogPane().setHeader(headerContent);
		}
		
		if(expandableContent != null) {
			dialog.getDialogPane().setExpandableContent(expandableContent);
		}

		if (content != null) {
			dialog.getDialogPane().setContent(content);
		}
		
		if (graphic != null) {
			dialog.setGraphic(graphic);
		}
    	
    	return dialog;
    }
    
    public CzzAlertDialog buildAndShow() {
    	CzzAlertDialog dialog = build();
    	dialog.showAndWait();
    	return dialog;
    }
    
    /* ************************************ UTILITY METHODS ************************************ */
    
	public static DialogWindowBuilder getBuilder(Window parentStage) {
		return CoreContextHolder.get().getBean(DialogWindowBuilder.class, parentStage);
	}
	
	public CzzAlertDialog simpleErrorDialog(String message) {
		return type(AlertType.ERROR).message(message).buildAndShow();
	}

	public CzzAlertDialog simpleErrorDialog(String mainMessage, String message) {
		return type(AlertType.ERROR).mainMessage(mainMessage).message(message).buildAndShow();
	}
	
	public CzzAlertDialog simpleInfoDialog(String message) {
		return type(AlertType.INFORMATION).message(message).buildAndShow();
	}
	
	public CzzAlertDialog simpleThrowableDialog(Throwable e) {
		return type(AlertType.ERROR).message(I18N.getText("Lo sentimos, ha ocurrido un error.")).exception(e).buildAndShow();
	}

	public CzzAlertDialog simpleThrowableDialog(String message, Throwable e) {
		return type(AlertType.ERROR).message(message).exception(e).buildAndShow();
	}

	public CzzAlertDialog simpleThrowableDialog(String mainMessage, String message, Throwable e) {
		return type(AlertType.ERROR).mainMessage(mainMessage).message(message).exception(e).buildAndShow();
	}
	
	public CzzAlertDialog simpleWarningDialog(String message) {
		return type(AlertType.WARNING).message(message).buildAndShow();
	}

	public CzzAlertDialog simpleWarningDialog(String mainMessage, String message) {
		return type(AlertType.WARNING).mainMessage(mainMessage).message(message).buildAndShow();
	}
	
	public boolean simpleConfirmDialog(String message, String acceptButton, String cancelButton) {
		return type(AlertType.CONFIRMATION).message(message).addButton(new ButtonType(acceptButton, ButtonData.OK_DONE)).addButton(new ButtonType(cancelButton, ButtonData.CANCEL_CLOSE)).buildAndShow().isAccepted();
    }

	public boolean simpleConfirmDialog(String message) {
		return type(AlertType.CONFIRMATION).message(message).addButton(new ButtonType(I18N.getText("Aceptar"), ButtonData.OK_DONE))
		        .addButton(new ButtonType(I18N.getText("Cancelar"), ButtonData.CANCEL_CLOSE)).buildAndShow().isAccepted();
	}

	public boolean simpleConfirmDialog(String mainMessage, String message) {
		return type(AlertType.CONFIRMATION).mainMessage(mainMessage).message(message).addButton(new ButtonType(I18N.getText("Aceptar"), ButtonData.OK_DONE))
				.addButton(new ButtonType(I18N.getText("Cancelar"), ButtonData.CANCEL_CLOSE)).buildAndShow().isAccepted();
	}
    
    

}
