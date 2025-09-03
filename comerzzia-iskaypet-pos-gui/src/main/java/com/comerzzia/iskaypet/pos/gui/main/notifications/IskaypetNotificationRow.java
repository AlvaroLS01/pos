package com.comerzzia.iskaypet.pos.gui.main.notifications;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.iskaypet.pos.services.main.notifications.imagenes.CargarImagenService;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.util.config.SpringContext;

import com.comerzzia.pos.util.i18n.I18N;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
public class IskaypetNotificationRow extends AnchorPane{

	private Notificacion notification;
	private Label label;
	private Button btnRemove;
	private Label lblDate;
	private ImageView imageView;
	private static String cssPath;

	public IskaypetNotificationRow(Notificacion notification){
		this.notification = notification;
		getStylesheets().add(getCssPath());

		getStyleClass().add("mainFxmlClass");
		setMaxHeight(Double.MAX_VALUE);
		setMinHeight(-1);
		setPrefWidth(400);

		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.setPrefWidth(-1);
		vBox.setPrefHeight(-1);
		AnchorPane.setBottomAnchor(vBox, 0.0);
		AnchorPane.setLeftAnchor(vBox, 3.0);
		AnchorPane.setTopAnchor(vBox, 0.0);
		getChildren().add(vBox);

		imageView = new ImageView();
		imageView.setId("imageView");
		imageView.setFitWidth(40);
		imageView.setFitHeight(40);
		imageView.setPickOnBounds(true);
		imageView.setPreserveRatio(true);
		vBox.getChildren().add(imageView);

		label = new Label();
		label.setId("label");
		label.setAlignment(Pos.TOP_LEFT);
		label.setMaxHeight(70);
		label.setMinHeight(35);
		label.setPrefHeight(-1);
		label.setWrapText(true);
		AnchorPane.setLeftAnchor(label, 55.0);
		AnchorPane.setRightAnchor(label, 53.0);
		AnchorPane.setTopAnchor(label, 15.0);
		getChildren().add(label);

		btnRemove = new Button();
		btnRemove.setId("btnRemove");
		btnRemove.setMnemonicParsing(false);
		btnRemove.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent paramT){
				remove();
			}
		});
		btnRemove.setPickOnBounds(true);
		btnRemove.setPrefHeight(42.0);
		btnRemove.setPrefWidth(42.0);
		AnchorPane.setRightAnchor(btnRemove, 3.0);
		AnchorPane.setTopAnchor(btnRemove, 2.0);
		AnchorPane.setBottomAnchor(btnRemove, 2.0);
		getChildren().add(btnRemove);

		lblDate = new Label();
		lblDate.setId("lblDate");
		lblDate.setAlignment(Pos.CENTER);
		lblDate.setTextAlignment(TextAlignment.LEFT);
		AnchorPane.setLeftAnchor(lblDate, 55.0);
		AnchorPane.setRightAnchor(lblDate, 53.0);
		AnchorPane.setTopAnchor(lblDate, 0.0);
		getChildren().add(lblDate);
	}

	protected void remove(){
		IskaypetNotificaciones.get().remove(notification);
	}

	public String getCssPath(){
		// Cacheamos en atributo estático
		if(cssPath == null){
			cssPath = POSApplication.getInstance().getSkinResource("com/comerzzia/pos/core/gui/main/notificaciones/notificationrow.css").toExternalForm();
		}
		return cssPath;
	}

	public void init(){
		// El contenedor (AnchorPane) se adapta al tamaño de los label + 19 de padding
		prefHeightProperty().bind(label.heightProperty().add(19.0).add(lblDate.heightProperty()));

		label.setText(I18N.getTexto(notification.getTexto()));
		
		CargarImagenService cargarImagenService = SpringContext.getBean(CargarImagenService.class);
		
		if(notification.getTipo() == Tipo.WARN){
			imageView.setImage(cargarImagenService.getImageWarn());
		}
		else if(notification.getTipo() == Tipo.INFO){
			imageView.setImage(cargarImagenService.getImageInfo());
		}
		else if(notification.getTipo() == Tipo.ERROR){
			imageView.setImage(cargarImagenService.getImageError());
		}

		//lblDate.setText(FormatUtil.getInstance().formateaFechaHora(notification.getDate()));
		lblDate.setText("");
	}

	public Notificacion getNotification(){
		return notification;
	}

	public void setNotification(Notificacion notification){
		this.notification = notification;
	}

}
