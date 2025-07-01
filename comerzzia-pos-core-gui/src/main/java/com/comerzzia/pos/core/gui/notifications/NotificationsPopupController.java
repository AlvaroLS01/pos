
package com.comerzzia.pos.core.gui.notifications;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationType;
import com.comerzzia.pos.core.gui.components.ComponentController;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.core.services.notifications.Notifications.ChangeEvent;
import com.comerzzia.pos.core.services.notifications.Notifications.ChangeListener;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NotificationsPopupController extends ComponentController implements Initializable {

	private static final String lastBorderStyle = "-fx-border-width: 0 0 0 0;";
	@FXML VBox container;
	@FXML ScrollPane scrollPane;
	private Map<Notification, NotificationRow> mapNotification = new HashMap<>();
	private NotificationRow emptyNotification;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Notification emptyNotif = new Notification(I18N.getText("No hay notificaciones"), NotificationType.INFO, new Date());
		emptyNotification = new NotificationRow(emptyNotif);
		emptyNotification.init();
		emptyNotification.getStyleClass().add("notif-empty-row");
		
		List<Notification> notifications = Notifications.get().getNotifications();
		synchronized (Notifications.LOCK) {
			for(Notification notif : notifications){
				NotificationRow row = new NotificationRow(notif);
				row.init();
				container.getChildren().add(row);
				mapNotification.put(notif, row);
			}
		}
		update();
		
		DoubleBinding prefHeightScrollPaneBinding = createScrollPaneHeightBinding();
		scrollPane.prefHeightProperty().bind(prefHeightScrollPaneBinding);
		
		Notifications.get().addListener(createListChangeListener());
	}
	
	private DoubleBinding createScrollPaneHeightBinding() {
		DoubleBinding prefHeightScrollPaneBinding = new DoubleBinding() {
			
			{
				//Listens to prefHeight of every NotificationRow
				container.getChildren().forEach(n -> bind(((NotificationRow)n).prefHeightProperty()) );
				
				//Listens to NotificationRow list changes and adds/removes bindings according to the list current content
				container.getChildren().addListener((ListChangeListener<? super Node>)change -> {
					while (change.next()) {
						 change.getAddedSubList().forEach(n -> bind(((NotificationRow)n).prefHeightProperty()));
						 change.getRemoved().forEach(n -> unbind(((NotificationRow)n).prefHeightProperty())) ;
					}
				});

		        bind(container.getChildren());
			}
			
			@Override
			protected double computeValue() {
				double height = 0;
				//Gets the prefHeight of every NotificationRow (it is calculated according to its content)
				for (Node node : container.getChildren()) {
					NotificationRow row = (NotificationRow) node;
					height += row.getPrefHeight();
				}
				//If there is no items in the notification list, it defaults to 69px
				if(height == 0){
					height = 69;
				}
				return height + 4;
			}
		};
		
		return prefHeightScrollPaneBinding;
	}
	
	private ChangeListener createListChangeListener() {
		return new ChangeListener(){
			@Override
			public void onChange(final ChangeEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						final Notification notif = (Notification) event.getObject();
						if(event.wasAdded()){
							NotificationRow row = new NotificationRow(notif);
							row.init();
							container.getChildren().add(row);
							mapNotification.put(notif, row);
							
							update();
						}
						if(event.wasRemoved()){
							NotificationRow row = mapNotification.remove(notif);
							removeWithAnim(row);
						}
						if(event.wasCleared()){
							for (Node row : container.getChildren()) {
								mapNotification.remove(notif);
								removeWithAnim((NotificationRow) row);
							}
						}
					}
				});
			}
		};
	}

	private void removeWithAnim(final NotificationRow row){
		if(row != emptyNotification){
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), row);
	        fadeTransition.setFromValue(1.0);
	        fadeTransition.setToValue(0.0);
	        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent paramT) {
					container.getChildren().remove(row);		
					update();
				}
			});
	        fadeTransition.play();
		}
	}
	
	private void update(){
		updateEmptyRow();
		updateLastBorder();
	}
	
	private void updateLastBorder(){
		if(!container.getChildren().isEmpty()){
			
			//Quitamos bordes al ultimo
			NotificationRow row = (NotificationRow) container.getChildren().get(container.getChildren().size() - 1);
			String style = row.getStyle();
			style += lastBorderStyle;
			row.setStyle(style);
			
			//Ponemos al penúltimo como estaba antes
			if(container.getChildren().size() > 1){
				row = (NotificationRow) container.getChildren().get(container.getChildren().size() - 2);
				style = row.getStyle();
				style = style.replaceAll(lastBorderStyle, "");
				row.setStyle(style);
			}
		}
	}
	
	private void updateEmptyRow(){
		ObservableList<Node> children = container.getChildren();
		if(children.size() == 0){
			children.add(emptyNotification);
		}else{
			children.remove(emptyNotification);
		}
	}

	@FXML
	public void clearAll(){
		Notifications.get().clear();
		mapNotification.clear();
	}
	
	@FXML
	public void close(){
		((NotificationsPopup)getComponent()).getWindow().close();
	}
	
	public VBox getContainer(){
		return container;
	}
	
}
