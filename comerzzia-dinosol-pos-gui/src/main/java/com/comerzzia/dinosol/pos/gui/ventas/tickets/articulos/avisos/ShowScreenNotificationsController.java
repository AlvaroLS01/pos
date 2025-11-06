package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.avisos;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@Component
public class ShowScreenNotificationsController extends WindowController {

	private Logger log = Logger.getLogger(ShowScreenNotificationsController.class);

	public static String PARAM_TITLE = "ShowScreenNotificationsController.Title";

	public static String PARAM_NOTIFICATIONS = "ShowScreenNotificationsController.Notifications";

	public static String PARAM_AVISO_RESULTADO = "ShowScreenNotificationsController.Resultado";

	@FXML
	protected ScrollPane panelPromos;

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
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		vbNotifications.getChildren().clear();
		vbNotifications.setAlignment(Pos.CENTER);

		List<ScreenNotificationDto> notifications = (List<ScreenNotificationDto>) getDatos().get(PARAM_NOTIFICATIONS);
		if (notifications == null) {
			throw new InitializeGuiException(I18N.getTexto("No se han especificado las notificaciones. Contacte con un administrador si el problema persiste."));
		}

		String title = (String) getDatos().get(PARAM_TITLE);
		if (StringUtils.isNotBlank(title)) {
			lbTitle.setText(title);
		}

		showNotifications(notifications);
		resetearPosiconScroll();
	}

	@Override
	public void initializeFocus() {
		btClose.requestFocus();
	}

	public void close() {
		log.debug("close() - El cajero cancela.");

		getDatos().put(PARAM_AVISO_RESULTADO, false);
		accionCancelar();
	}

	public void pagar() {
		log.debug("pagar() - El cajero acepta.");

		getDatos().put(PARAM_AVISO_RESULTADO, true);
		getStage().close();
	}

	private void resetearPosiconScroll() {
		if (panelPromos != null) {
			panelPromos.setVvalue(0);
		}
	}

	private void showNotifications(List<ScreenNotificationDto> notifications) {		
		for (ScreenNotificationDto notification : notifications) {
			HBox mainContainer = new HBox(10.0);
			mainContainer.setStyle("-fx-border-color: #0082aa");
			mainContainer.setAlignment(Pos.CENTER);

			VBox container2 = new VBox(5.0);
			container2.setPadding(new Insets(0, 0, 0, 0));

			Label lbNotificationTitle = new Label(notification.getTitle());
			lbNotificationTitle.setAlignment(Pos.CENTER);
			lbNotificationTitle.getStyleClass().add("lb-destacado");
			lbNotificationTitle.setPrefWidth(1000.0);

			Label lbNotificationText = new Label(notification.getText());
			lbNotificationText.setAlignment(Pos.CENTER);
			lbNotificationText.setWrapText(true);
			lbNotificationText.setMinHeight(Region.USE_PREF_SIZE);
			lbNotificationText.setStyle("-fx-font-size: 20px;");

			container2.getChildren().add(lbNotificationTitle);
			container2.getChildren().add(lbNotificationText);

			mainContainer.getChildren().add(container2);

			vbNotifications.getChildren().add(mainContainer);
		}
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				double height = 150;
				for(Node node : vbNotifications.getChildren()) {
					for(Node nodeV : ((HBox) node).getChildren()) {
						for(Node label : ((VBox) nodeV).getChildren()) {
							height = height + ((Label) label).getHeight();
						}
					}
				}
				
				vbNotifications.setPrefHeight(height);
			}
		});
	}

}
