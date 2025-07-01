
package com.comerzzia.pos.core.gui.notifications;


import java.util.List;

import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.omnichannel.facade.model.notification.Notification.NotificationType;
import com.comerzzia.pos.core.gui.components.Component;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.core.services.notifications.Notifications.ChangeEvent;
import com.comerzzia.pos.core.services.notifications.Notifications.ChangeListener;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class NotificationsPopup extends Component {

	protected double width = 400;
	protected NotificationsWindow window;
	
	public NotificationsPopup(){
		setId("popup");
		setPrefWidth(width);
		AnchorPane.setTopAnchor(this, 45d);
		AnchorPane.setLeftAnchor(this, 91.5d);	
	}
	
	public void setOpenerButton(final Label btnOpener){
		updateCount(btnOpener);
		updateIcon(btnOpener);
		
		Notifications.get().addListener(new ChangeListener() {
			@Override
			public void onChange(ChangeEvent event) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						updateCount(btnOpener);
						updateIcon(btnOpener);
					}
				});
			}
		});
	}
	
	private void updateCount(Label btnOpener) {
		int count = Notifications.get().count();
		btnOpener.setText(count+"");
	}
	
	private void updateIcon(Label btnOpener) {
		List<Notification> notifications = Notifications.get().getNotifications();
		NotificationType maxType = NotificationType.INFO;
		synchronized (Notifications.LOCK) {
			for (Notification notification : notifications) {
				if(notification.getType() == NotificationType.WARN){
					maxType = NotificationType.WARN;
				}
				if(notification.getType() == NotificationType.ERROR){
					maxType = NotificationType.ERROR;
					break;
				}
			}
		}
		
		if(btnOpener.getStyleClass().size()>3){
			btnOpener.getStyleClass().remove(btnOpener.getStyleClass().size()-1); 
		}
		if(notifications.size() == 0){
			btnOpener.getStyleClass().add("gray");
		}else if(maxType == NotificationType.INFO){
			btnOpener.getStyleClass().add("info");
		}else if(maxType == NotificationType.WARN){
			btnOpener.getStyleClass().add("warn");
		}else if(maxType == NotificationType.ERROR){
			btnOpener.getStyleClass().add("error");
		}	
	}

	@Override
	public NotificationsPopupController getController() {
		return (NotificationsPopupController) super.getController();
	}

	public NotificationsWindow getWindow() {
		return window;
	}

	public void setWindow(NotificationsWindow window) {
		this.window = window;
	}
}
