
package com.comerzzia.pos.core.gui.notifications;


import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationType;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class NotificationRow extends AnchorPane {

	private Notification notification;
	private Label label;
	private Button btnRemove;
	private Label lblDate;
	private ImageView imageView;
	private static String cssPath;

	public NotificationRow(Notification notification){
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
		AnchorPane.setRightAnchor(label, 83.0);
		AnchorPane.setTopAnchor(label, 15.0);
		getChildren().add(label);
		
		btnRemove = new Button();
		btnRemove.setId("btnRemove");
		btnRemove.setMnemonicParsing(false);
		btnRemove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				remove();
			}
		});
		btnRemove.setPickOnBounds(true);
		btnRemove.setPrefHeight(42.0);
		btnRemove.setPrefWidth(42.0);
		AnchorPane.setRightAnchor(btnRemove, 33.0);
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
	
	protected void remove() {
		Notifications.get().remove(notification);
	}

	public String getCssPath(){
		//Cacheamos en atributo estático
		if(cssPath == null){
			cssPath = SkinManager.getInstance().getResource("com/comerzzia/pos/core/gui/notifications/notificationrow.css").toExternalForm();
		}
		return cssPath;
	}
	
	public void init(){
		//El contenedor (AnchorPane) se adapta al tamaño de los label + 19 de padding
		prefHeightProperty().bind(label.heightProperty().add(19.0).add(lblDate.heightProperty()));
		
		label.setText(notification.getText());
		if(notification.getType() == NotificationType.WARN){
			imageView.setImage(SkinManager.getInstance().getImage("notifications/warn.png"));
		}else if(notification.getType() == NotificationType.INFO){
			imageView.setImage(SkinManager.getInstance().getImage("notifications/info.png"));
		}else if(notification.getType() == NotificationType.ERROR){
			imageView.setImage(SkinManager.getInstance().getImage("notifications/error.png"));
		}
		
		lblDate.setText(FormatUtils.getInstance().formatDateTime(notification.getDate()));
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
}
