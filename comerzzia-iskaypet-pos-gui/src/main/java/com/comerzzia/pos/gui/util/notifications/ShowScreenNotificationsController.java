package com.comerzzia.pos.gui.util.notifications;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Component
public class ShowScreenNotificationsController extends WindowController {
	
	public static String PARAM_TITLE = "ShowScreenNotificationsController.Title";
	
	public static String PARAM_NOTIFICATIONS = "ShowScreenNotificationsController.Notifications";
	
	@FXML
	protected Button btClose;
	
	@FXML
	protected Label lbTitle;
	
	@FXML
	protected VBox vbNotifications;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		vbNotifications.getChildren().clear();
		
		List<ScreenNotificationDto> notifications = (List<ScreenNotificationDto>) getDatos().get(PARAM_NOTIFICATIONS);
		if(notifications == null) {
			throw new InitializeGuiException(I18N.getTexto("No se han especificado las notificaciones. Contacte con un administrador si el problema persiste."));
		}
		
		String title = (String) getDatos().get(PARAM_TITLE);
		if(StringUtils.isNotBlank(title)) {
			lbTitle.setText(title);
		}
		
		showNotifications(notifications);
	}

	@Override
	public void initializeFocus() {
		btClose.requestFocus();
	}
	
	public void close() {
		accionCancelar();
	}

	private void showNotifications(List<ScreenNotificationDto> notifications) {
		for(ScreenNotificationDto notification : notifications) {
			HBox mainContainer = new HBox(10.0);
			mainContainer.setStyle("-fx-border-color: #0082aa");
			mainContainer.setAlignment(Pos.CENTER_LEFT);
			
			Label container1 = new Label();
			container1.setPrefHeight(50.0);
			container1.getStyleClass().add(notification.getType());
			container1.setAlignment(Pos.CENTER);
			
			VBox container2 = new VBox(5.0);
			container2.setPadding(new Insets(0, 10, 0, 0));
			
			Label lbNotificationTitle = new Label(notification.getTitle());
			lbNotificationTitle.getStyleClass().add("lb-destacado");
			
			Label lbNotificationText = new Label(notification.getText());
			lbNotificationText.setWrapText(true);
			
			container2.getChildren().add(lbNotificationTitle);
			container2.getChildren().add(lbNotificationText);
			
			mainContainer.getChildren().add(container1);
			mainContainer.getChildren().add(container2);
			
			vbNotifications.getChildren().add(mainContainer);
		}
	}

}
