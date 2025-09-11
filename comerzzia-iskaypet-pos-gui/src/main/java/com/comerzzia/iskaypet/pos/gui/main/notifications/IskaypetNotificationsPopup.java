package com.comerzzia.iskaypet.pos.gui.main.notifications;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones.ChangeEvent;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones.ChangeListener;
import com.comerzzia.pos.core.gui.componentes.Component;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
public class IskaypetNotificationsPopup extends Component{

	private double width = 400;
	private IskaypetNotificationsWindow window;

	public IskaypetNotificationsPopup(){
		setId("popup");
		setPrefWidth(width);
		AnchorPane.setTopAnchor(this, 45d);
		AnchorPane.setRightAnchor(this, 202d);

	}

	public void setOpenerButton(final Button btnOpener){
		//updateCount(btnOpener);
		updateIcon(btnOpener);

		IskaypetNotificaciones.get().addListener(new ChangeListener(){

			@Override
			public void onChange(ChangeEvent event){
				Platform.runLater(new Runnable(){

					@Override
					public void run(){
						updateIcon(btnOpener);
					}
				});
			}
		});
	}

	public void updateCount(Button btnOpener){
		int count = IskaypetNotificaciones.get().count();
		if(count > 0){
			btnOpener.setText(count + "");
		}
		else{
			btnOpener.setText("");
		}
	}

	private void updateIcon(Button btnOpener){
		List<Notificacion> notifications = IskaypetNotificaciones.get().getNotifications();
		Tipo maxType = Tipo.INFO;
		synchronized(IskaypetNotificaciones.LOCK){
			for(Notificacion notification : notifications){
				if(notification.getTipo() == Tipo.WARN){
					maxType = Tipo.WARN;
				}
				if(notification.getTipo() == Tipo.ERROR){
					maxType = Tipo.ERROR;
					break;
				}
			}
		}

		if(btnOpener.getStyleClass().size() > 3){
			btnOpener.getStyleClass().remove(btnOpener.getStyleClass().size() - 1);
		}
		
		IskaypetNotificaciones.get().setNumNotificaciones(notifications.size());
		
		if(notifications.size() == 0){
			btnOpener.getStyleClass().add("gray");
		}
		else if(maxType == Tipo.INFO){
			btnOpener.getStyleClass().add("info");
		}
		else if(maxType == Tipo.WARN){
			btnOpener.getStyleClass().add("warn");
		}
		else if(maxType == Tipo.ERROR){
			btnOpener.getStyleClass().add("error");
		}
		
		updateCount(btnOpener);
	}

	@Override
	public IskaypetNotificationsPopupController getController(){
		return (IskaypetNotificationsPopupController) super.getController();
	}

	public IskaypetNotificationsWindow getWindow(){
		return window;
	}

	public void setWindow(IskaypetNotificationsWindow window){
		this.window = window;
	}
}
