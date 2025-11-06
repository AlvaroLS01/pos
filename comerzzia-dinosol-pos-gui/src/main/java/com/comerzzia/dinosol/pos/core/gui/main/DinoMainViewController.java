package com.comerzzia.dinosol.pos.core.gui.main;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.main.MainViewController;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.gui.inicio.InicioView;

@Primary
@Component
public class DinoMainViewController extends MainViewController {

	private static final Logger log = Logger.getLogger(DinoMainViewController.class.getName());

	@Override
	public void replaceView(View view) {

		log.debug("replaceView() - Cargando pantalla en frame interno ");
		View currentView = getView().getSubViews().peek();

		// Eliminamos las acciones del panel anterior
		if (currentView != null) {
			accionOcultarMenu(); // ocultamos el menú
			currentView.getController().onClose();
		}

		log.debug("replaceView() - Eliminando panel previo y cargando pantalla");
		panelPrincipal.getChildren().clear();

		getView().getSubViews().remove(view);
		getView().getSubViews().push(view);

		// Establecemos la pantalla al maximo tamaño disponible
		Pane viewPane = (Pane) getView().getCurrentSubView().getViewNode();
		AnchorPane.setTopAnchor(viewPane, 0.0);
		AnchorPane.setBottomAnchor(viewPane, 0.0);
		AnchorPane.setLeftAnchor(viewPane, 0.0);
		AnchorPane.setRightAnchor(viewPane, 0.0);

		// Añadimos la pantalla al panel principal
		panelPrincipal.getChildren().add(viewPane);

		if (view instanceof InicioView) {
			accionMostrarMenu();
		}

	}

}
