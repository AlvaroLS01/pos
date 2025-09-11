package com.comerzzia.iskaypet.pos.gui.main.notifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.model.notificaciones.Notificacion.Tipo;
import com.comerzzia.iskaypet.pos.persistence.notifications.NotificationsVersion;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones.ChangeEvent;
import com.comerzzia.iskaypet.pos.services.notificaciones.IskaypetNotificaciones.ChangeListener;
import com.comerzzia.iskaypet.pos.services.notifications.NotificationsService;
import com.comerzzia.pos.core.gui.componentes.ComponentController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
@SuppressWarnings("unlikely-arg-type")
public class IskaypetNotificationsPopupController extends ComponentController{

	private static final String lastBorderStyle = "-fx-border-width: 0 0 0 0;";
	@FXML
	public VBox container;
	@FXML
	ScrollPane scrollPane;
	private Map<Notificacion, IskaypetNotificationRow> mapNotification = new HashMap<>();
	private IskaypetNotificationRow emptyNotification;

	protected NotificationsService notificationsVersionService = SpringContext.getBean(NotificationsService.class);
	
	@Override
	public void initializeComponent(){
		Notificacion emptyNotif = new Notificacion(I18N.getTexto("No hay notificaciones"), Tipo.INFO, new Date());
		emptyNotification = new IskaypetNotificationRow(emptyNotif);
		emptyNotification.init();
		emptyNotification.getStyleClass().add("notif-empty-row");

		// Sacamos los datos de la tabla personalizada donde se guardan las notificaciones de versión.
		//List<Notificacion> notifications = Notificaciones.get().getNotifications();
		List<Notificacion> notifications = new ArrayList<Notificacion>();
		List<NotificationsVersion> response = notificationsVersionService.getNotificationsNotView();
		for(NotificationsVersion notificationsVersion : response){
			Map<String, NotificationsVersion> mapNotificationIsk = IskaypetNotificaciones.get().getMapNotificationIsk();
			Notificacion notificacion = new Notificacion(notificationsVersion.getMsg(), Tipo.INFO, new Date());
			notifications.add(notificacion);
			mapNotificationIsk.put(notificacion.getTexto(), notificationsVersion);
		}
		
		
		synchronized(IskaypetNotificaciones.LOCK){
			for(Notificacion notif : notifications){
				IskaypetNotificationRow row = new IskaypetNotificationRow(notif);
				
				IskaypetNotificaciones.get().addNotification(notif);
				
				row.init();
				container.getChildren().add(row);
				mapNotification.put(notif, row);
			}
		}
		IskaypetNotificaciones.get().setNumNotificaciones(notifications.size());
		update();
		IskaypetNotificaciones.get().addListener(createListChangeListener());
	}

	private ChangeListener createListChangeListener(){
		return new ChangeListener(){

			@Override
			public void onChange(final ChangeEvent event){
				Platform.runLater(new Runnable(){
					
					@Override
					public void run(){
						final Notificacion notif = (Notificacion) event.getObject();
						Map<String, NotificationsVersion> mapNotificationIsk = IskaypetNotificaciones.get().getMapNotificationIsk();
						if(event.wasAdded()){
							IskaypetNotificationRow row = new IskaypetNotificationRow(notif);
							row.init();
				
							if(IskaypetNotificaciones.get().isLimpiarPantalla()) {
								container.getChildren().clear();
								IskaypetNotificaciones.get().setLimpiarPantalla(false);
							}
							container.getChildren().add(row);
							mapNotification.put(notif, row);

							update();
						}
						if(event.wasRemoved()){
							IskaypetNotificationRow row = mapNotification.remove(notif);
							removeWithAnim(row);
							/*notificationsVersionService.updateNotificationsViewCheck(mapNotificationIsk.get(notif.getTexto()));*/
							notificationsVersionService.updateNotificationsViewCheckMsgDistinct(mapNotificationIsk.get(notif.getTexto()));
							
							mapNotificationIsk.remove(mapNotificationIsk.get(notif.getTexto()));
							IskaypetNotificaciones.get().setNumNotificaciones(mapNotificationIsk.size());
							
						}
						if(event.wasCleared()){
							for(Node row : container.getChildren()){
								mapNotification.remove(notif);
								removeWithAnim((IskaypetNotificationRow) row);
							}
							notificationsVersionService.updateClearNotification();
							
							IskaypetNotificaciones.get().setNumNotificaciones(0);
						}
					}
				});
			}
		};
	}

	private void removeWithAnim(final IskaypetNotificationRow row){
		if(row != emptyNotification){
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), row);
			fadeTransition.setFromValue(1.0);
			fadeTransition.setToValue(0.0);
			fadeTransition.setOnFinished(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent paramT){
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
		updateScrollPaneHeight();
	}

	private void updateLastBorder(){
		if(!container.getChildren().isEmpty()){

			// Quitamos bordes al ultimo
			IskaypetNotificationRow row = (IskaypetNotificationRow) container.getChildren().get(container.getChildren().size() - 1);
			String style = row.getStyle();
			style += lastBorderStyle;
			row.setStyle(style);

			// Ponemos al penúltimo como estaba antes
			if(container.getChildren().size() > 1){
				row = (IskaypetNotificationRow) container.getChildren().get(container.getChildren().size() - 2);
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
		}
		else{
			children.remove(emptyNotification);
		}
	}

	public void updateScrollPaneHeight(){
		ObservableList<Node> children = container.getChildren();
		double height = 0;
		for(Node node : children){
			IskaypetNotificationRow row = (IskaypetNotificationRow) node;
			height += row.getHeight();
		}
		if(height == 0){
			height = 69;
		}
		scrollPane.setPrefHeight(height + 2);
	}

	@FXML
	public void clearAll(){
		IskaypetNotificaciones.get().clear();
		mapNotification.clear();
	}

	@FXML
	public void close(){
		((IskaypetNotificationsPopup) getComponent()).getWindow().close();
	}

	@Override
	public void initializeFocus(){

	}

	public VBox getContainer(){
		return container;
	}
	
	

}
