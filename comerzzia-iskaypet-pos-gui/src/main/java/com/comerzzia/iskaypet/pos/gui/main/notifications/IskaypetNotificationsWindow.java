package com.comerzzia.iskaypet.pos.gui.main.notifications;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import com.comerzzia.pos.core.gui.POSApplication;

/**
 * GAP61 - IMPRESIÓN ETIQUETAS LINEAL
 */
public class IskaypetNotificationsWindow extends Stage{

	private IskaypetNotificationsPopup notificationsPopup;

	public IskaypetNotificationsWindow(){
		POSApplication app = POSApplication.getInstance();

		double ancho = app.getStage().getWidth();
		double alto = app.getStage().getHeight();
		setWidth(ancho);
		setHeight(alto);
		setX(app.getStage().getX());
		setY(app.getStage().getY());
		initOwner(POSApplication.getInstance().getStage());
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.WINDOW_MODAL);

		notificationsPopup = new IskaypetNotificationsPopup();
		notificationsPopup.setWindow(this);

		AnchorPane anchorPane = new AnchorPane();
		anchorPane.getChildren().add(getNotificationsPopup());
		anchorPane.setStyle("-fx-background-color: transparent;");

		Scene scene = new Scene(anchorPane);
		// ComerzziaPosGui.addBaseCSS(scene);
		scene.setFill(Color.rgb(0, 0, 0, 0.1)); // Casi transparente, si es transparente no llegan clicks
		setScene(scene);

		getNotificationsPopup().setOnMousePressed(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event){
				// Controla que si se ha pulsado dentro de container, no le pase el evento al Scene y éste no lo cierre.
				VBox container = notificationsPopup.getController().getContainer();
				Bounds bounds = container.localToScene(container.getBoundsInLocal());

				if(event.getSceneY() < bounds.getMaxY()){
					event.consume();
				}
			}
		});
		scene.setOnMousePressed(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event){
				close();
			}
		});

		scene.setOnKeyReleased(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event){
				if(event.getCode() == KeyCode.ESCAPE){
					close();
				}
			}
		});
		setOnShowing(createShowingHandler());
		setOnHiding(createHidingHandler());
	}

	private EventHandler<WindowEvent> createHidingHandler(){
		return new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent paramT){
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), notificationsPopup);
				fadeTransition.setFromValue(1.0);
				fadeTransition.setToValue(0.0);
				fadeTransition.play();
			}
		};
	}

	private EventHandler<WindowEvent> createShowingHandler(){
		return new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent paramT){
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), notificationsPopup);
				fadeTransition.setFromValue(0.0);
				fadeTransition.setToValue(1.0);
				fadeTransition.play();

				Platform.runLater(new Runnable(){

					@Override
					public void run(){
						notificationsPopup.getController().updateScrollPaneHeight();
					}
				});
			}
		};
	}

	public IskaypetNotificationsPopup getNotificationsPopup(){
		return notificationsPopup;
	}

}
