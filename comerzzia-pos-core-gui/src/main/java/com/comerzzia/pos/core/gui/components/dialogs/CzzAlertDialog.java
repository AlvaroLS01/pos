package com.comerzzia.pos.core.gui.components.dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.components.keyboard.Keyboard;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;
import com.comerzzia.pos.util.events.CzzChildComponentClosedEvent;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Component
@Scope("prototype")
public class CzzAlertDialog extends Alert {
	
	protected boolean beep;
	protected Throwable throwable;
	
	public CzzAlertDialog(@NamedArg("alertType") AlertType alertType) {
        this(alertType, "");
    }

	public CzzAlertDialog(@NamedArg("alertType") AlertType alertType,
            @NamedArg("contentText") String contentText) {
		this(alertType, contentText, (ButtonType[])null);
	}
	public CzzAlertDialog(@NamedArg("alertType") AlertType alertType,
            @NamedArg("contentText") String contentText,
            ButtonType... buttons) {
		super(alertType, contentText, buttons);
		setHeaderText(null); //Default to null
		initStyle(StageStyle.UNDECORATED);
		initModality(Modality.WINDOW_MODAL);
		
		createMainContent(contentText);
		addBaseCSS(this.getDialogPane());
		
		setOnShowing(e -> onShowingEvent(e));
		setOnHiding(e -> onHidingEvent(e));
		
	}
	
	public void onShowingEvent(DialogEvent event) {
		if(beep) {
			Toolkit.getDefaultToolkit().beep();
		}
		if (getOwner() != null) {
			getOwner().getScene().getRoot().setEffect(new GaussianBlur(3));
		}
		
		//TODO: ?? Necesario??
		Keyboard keyboard = CoreContextHolder.get().getBean(Keyboard.class);
    	keyboard.close();
//    	ScenicView.show(getDialogPane());
	}
	
	public void onHidingEvent(DialogEvent event) {
		if (getOwner() != null) {
			getOwner().getScene().getRoot().setEffect(null);
			getOwner().getScene().getRoot().fireEvent(new Event(CzzChildComponentClosedEvent.CLOSED_CHILD_EVENT));
		}
	}

	public void createThrowableContent(Throwable throwable) {
		this.throwable = throwable;
		
		String exceptionText;
		final StringWriter salida = new StringWriter();
        try (PrintWriter psalida = new PrintWriter(salida)) {
        	throwable.printStackTrace(psalida);
        	exceptionText = salida.toString();
        }
		
		Label label = new Label(I18N.getText("Traza del error:"));
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		getDialogPane().setExpandableContent(expContent);
		getDialogPane().expandedProperty().addListener((obs, oldE, newE) -> {
			//Center the expanded dialog
			double centerX;
			double centerY;
			if (getOwner() != null) {
				centerX =  getOwner().getX() + getOwner().getWidth()/2d;
				centerY =  getOwner().getY() + getOwner().getHeight()/2d;
			} else {
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				centerX = screenSize.width/2d;
				centerY = screenSize.height/2d;
				
			}
			setX(centerX - getWidth()/2);
	        setY(centerY - getHeight()/2);
		});
	}
	
	public void createMainContent(String contentText) {
		VBox vbMessage = new VBox();
        vbMessage.setAlignment(Pos.CENTER_LEFT);

        Label lbMessage = new Label();
        lbMessage.setWrapText(true);
        lbMessage.setMinWidth(180);
        lbMessage.setText(contentText);
        
        vbMessage.getStyleClass().add("lb-message");
        vbMessage.getChildren().add(lbMessage);
		
		getDialogPane().setContent(vbMessage);
		getDialogPane().setId("ventanaDialogo");
	}
	
	public boolean isAccepted(){
		return getResult() != null && 
				(ButtonType.OK.getButtonData().equals(getResult().getButtonData()) || 
						ButtonType.YES.getButtonData().equals(getResult().getButtonData()));
	}
	
	protected void addBaseCSS(DialogPane scene) {
		AppConfigData appConfig = AppConfig.getCurrentConfiguration();
		List<String> baseCSSs;
		// Cacheamos la lista de Strings para no llamar a getResource cada vez.
		ArrayList<String> cssList = new ArrayList<>();
		String stylesCss;
		String keyboardCss;
		File f = null;
		String cmzHomePath = appConfig.getCmzHomePath();
		if (cmzHomePath != null) {
			if (!StringUtils.equals(appConfig.getDEFAULT_SKIN(), appConfig.getSkin())) {
				stylesCss = cmzHomePath + "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/styles.css";
				f = new File(stylesCss);
				if (f != null && f.exists()) {
					cssList.add("file:///" + stylesCss);
				}
				keyboardCss = cmzHomePath + "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
				f = new File(keyboardCss);
				if (f != null && f.exists()) {
					cssList.add("file:///" + keyboardCss);
				}
			}
			stylesCss = cmzHomePath + "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/styles.css";
			f = new File(stylesCss);
			if (f != null && f.exists()) {
				cssList.add("file:///" + stylesCss);
			}
			keyboardCss = cmzHomePath + "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
			f = new File(keyboardCss);
			if (f != null && f.exists()) {
				cssList.add("file:///" + keyboardCss);
			}
		}
		else {
			URL resource;
			if (!StringUtils.equals(appConfig.getDEFAULT_SKIN(), appConfig.getSkin())) {
				stylesCss = "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/styles.css";
				resource = CssScene.class.getResource(stylesCss);
				if (resource != null) {
					cssList.add(stylesCss);
				}
				keyboardCss = "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
				resource = CssScene.class.getResource(stylesCss);
				if (resource != null) {
					cssList.add(keyboardCss);
				}
			}
			String defaultSkin = "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/styles.css";
			resource = CssScene.class.getResource(defaultSkin);
			if (resource != null) {
				cssList.add(defaultSkin);
			}
			keyboardCss = "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
			resource = CssScene.class.getResource(defaultSkin);
			if (resource != null) {
				cssList.add(keyboardCss);
			}
		}

		baseCSSs = cssList;
		for (String css : baseCSSs) {
			scene.getStylesheets().add(css);
		}
	}
	
}
