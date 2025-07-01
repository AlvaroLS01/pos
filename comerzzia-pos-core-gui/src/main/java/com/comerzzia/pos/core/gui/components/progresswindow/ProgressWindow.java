
package com.comerzzia.pos.core.gui.components.progresswindow;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.util.events.CzzChildComponentClosedEvent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProgressWindow extends Stage {
	
	protected BorderPane internalPane;
	protected Label loaderlabel;
	protected WorkerProgressPane workerProgressPane;
	
    public static void createProgressWindow(final Worker<?> worker, Stage stageOwner) {
    		ProgressWindow progressWindow = CoreContextHolder.get().getBean(ProgressWindow.class);
    		progressWindow.build(worker, stageOwner);
    }
    
    protected void build(final Worker<?> worker, Stage stageOwner) {
    	setResizable(false);
        initStyle(StageStyle.TRANSPARENT);
        
        // Configuración de la ventana
        centerOnScreen();
        
        setIconified(false);
        
        internalPane = new BorderPane();
        
        // message
        HBox hBoxMensage = new HBox();
        hBoxMensage.setAlignment(Pos.CENTER);
        
        loaderlabel = new Label();
        loaderlabel.setAlignment(Pos.CENTER);
        loaderlabel.setContentDisplay(ContentDisplay.CENTER);
        
        WorkerProgressPane workerProgressPane = new WorkerProgressPane(this);
        workerProgressPane.setWorker(worker);

        hBoxMensage.getChildren().add(workerProgressPane);
        hBoxMensage.getChildren().add(loaderlabel);
        HBox.setHgrow(loaderlabel, Priority.ALWAYS);
        
        internalPane.setCenter(hBoxMensage);
        BorderPane.setAlignment(hBoxMensage, Pos.CENTER);
        
        Scene scene = new CssScene(internalPane);            
        
        // Estilos de ventana
        internalPane.setId("ventanaCargando");
        internalPane.setStyle("-fx-background-color:rgba(255,255,255,0.5);");
        
        scene.setFill(Color.TRANSPARENT);

        hBoxMensage.setFocusTraversable(false);
        internalPane.setFocusTraversable(false);
        setScene(scene);
        
        setWidth(stageOwner.getWidth());
        setHeight(stageOwner.getHeight());
        
        initOwner(stageOwner);
        initModality(Modality.APPLICATION_MODAL);
        
        getScene().setCursor(Cursor.WAIT);
        
     // Update the position of the progress window to match the owner window
	    if (getOwner() != null) {
	        setX(getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2);
	        setY(getOwner().getY() + getOwner().getHeight() / 2 - getHeight() / 2);
	    }
	    
	    setOnHiding(e -> {
	    	if(getOwner() == null) {
	    		return;
	    	}
        	getOwner().getScene().getRoot().fireEvent(new Event(CzzChildComponentClosedEvent.CLOSED_CHILD_EVENT));
        });
    }
    
    protected class WorkerProgressPane extends BorderPane {
		private Worker<?> worker;

		private boolean dialogVisible = false;
		private boolean cancelDialogShow = false;

		private ChangeListener<Worker.State> stateListener = new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State old, State value) {
				switch (value) {
				case CANCELLED:
				case FAILED:
				case SUCCEEDED:
					if (!dialogVisible) {
						cancelDialogShow = true;
						end();
					} else if (old == State.SCHEDULED || old == State.RUNNING) {
						end();
					}
					break;
				case SCHEDULED:
					begin();
					break;
				default: // no-op
				}
			}
		};

		public final void setWorker(final Worker<?> newWorker) {
			if (newWorker != worker) {
				if (worker != null) {
					worker.stateProperty().removeListener(stateListener);
					end();
				}

				worker = newWorker;

				if (newWorker != null) {
					newWorker.stateProperty().addListener(stateListener);
					if (newWorker.getState() == Worker.State.RUNNING
							|| newWorker.getState() == Worker.State.SCHEDULED) {
						// It is already running
						begin();
					}
				}
			}
		}

		// If the progress indicator changes, then we need to re-initialize
		// If the worker changes, we need to re-initialize

		private final ProgressWindow dialog;
		private final ProgressIndicator progressIndicator;

		public WorkerProgressPane(ProgressWindow dialog) {
			this.dialog = dialog;

			progressIndicator = new ProgressIndicator();
			progressIndicator.setMaxWidth(Double.MAX_VALUE);
			progressIndicator.setMaxHeight(Double.MAX_VALUE);
			setCenter(progressIndicator);

			if (worker != null) {
				progressIndicator.progressProperty().bind(worker.progressProperty());
			}
		}

		private void begin() {
			cancelDialogShow = false;

			Platform.runLater(() -> {
				if (!cancelDialogShow) {
					progressIndicator.progressProperty().bind(worker.progressProperty());
					dialogVisible = true;
					dialog.internalShow();
				}
			});
		}

		private void end() {
			progressIndicator.progressProperty().unbind();
			dialogVisible = false;
			dialog.internalHide();
		}


	}
    
    protected void internalShow() {
    	show();
    }
    
    protected void internalHide() {
    	hide();
    }
    
}
