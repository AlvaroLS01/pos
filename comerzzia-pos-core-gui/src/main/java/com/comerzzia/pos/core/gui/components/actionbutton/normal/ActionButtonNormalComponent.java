package com.comerzzia.pos.core.gui.components.actionbutton.normal;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActionButtonNormalComponent extends ActionButtonComponent {

	// Componentes internos de botón
	protected HBox linea1, linea2, linea3;

	protected ImageView imagen;
	protected Label lbTexto;
	protected Label lbTeclaAccesoRapido;

	/**
	 * Constructor de botón con imagen y dos etiquetas
	 */
	public ActionButtonNormalComponent() {
		super();
		
		VBox mainContainer = new VBox();

		linea1 = new HBox();
		linea2 = new HBox();
		linea3 = new HBox();

		imagen = new ImageView();

		lbTexto = new Label();
		lbTexto.setAlignment(Pos.CENTER);
		lbTexto.setContentDisplay(ContentDisplay.CENTER);
		lbTexto.getStyleClass().add("button-label");
		
		lbTeclaAccesoRapido = new Label();
		lbTeclaAccesoRapido.setAlignment(Pos.CENTER);
		lbTeclaAccesoRapido.setContentDisplay(ContentDisplay.CENTER);
		lbTeclaAccesoRapido.getStyleClass().add("lb-quick-access");

		linea1.getChildren().add(imagen);
		linea1.setAlignment(Pos.CENTER);
		linea2.getChildren().add(lbTexto);
		linea2.setAlignment(Pos.CENTER);
		linea3.getChildren().add(lbTeclaAccesoRapido);
		linea3.setAlignment(Pos.CENTER);

		HBox.setHgrow(imagen, Priority.ALWAYS);
		HBox.setHgrow(lbTexto, Priority.ALWAYS);
		VBox.setVgrow(linea2, Priority.ALWAYS);
		
		AnchorPane.setTopAnchor(linea3, 0.0);
		AnchorPane.setRightAnchor(linea3, 0.0);
		AnchorPane.setTopAnchor(mainContainer, 0.0);
		AnchorPane.setLeftAnchor(mainContainer, 0.0);
		AnchorPane.setRightAnchor(mainContainer, 0.0);
		AnchorPane.setBottomAnchor(mainContainer, 0.0);


		mainContainer.getChildren().addAll(linea1, linea2);
		panelInterno.getChildren().addAll(mainContainer, linea3);

		btAccion.setPickOnBounds(true);

		btAccion.setGraphic(panelInterno);

	}

	@Override
	protected void inicializeCustomComponents(Double ancho, Double alto) {
		changeImageUrl(configuracion.getImagePath());

		// Buscamos el texto del botón en las properties
		lbTexto.setText(I18N.getText(configuracion.getText()));
		lbTexto.setWrapText(configuracion.isWrapText());
		
		// Establecemos el texto de acceso rápido
		lbTeclaAccesoRapido.setText(configuracion.getShortcut());
	}
	
	public void changeImageUrl(String rutaImagen) {
		if (StringUtils.isNotBlank(rutaImagen)) {
			imagen.setImage(SkinManager.getInstance().getImage(rutaImagen));
		}
	}
	
	public void changeText(String texto) {
		lbTexto.setText(texto);
	}

}
