package com.comerzzia.pos.core.gui.components.actionbutton.language;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.pos.core.gui.components.actionbutton.image.ActionButtonImageComponent;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionButtonLanguageComponent extends ActionButtonImageComponent {
	
	public ActionButtonLanguageComponent() {
		super();

		imagen.setPreserveRatio(false);
		
		btAccion.getStyleClass().add("bt-language");
		
		imagen.getStyleClass().add("img-language");
		
		lbTexto.getStyleClass().clear();
		lbTexto.getStyleClass().add("button-label");
		
		lbTeclaAccesoRapido = new Label();
		lbTeclaAccesoRapido.setAlignment(Pos.CENTER);
		lbTeclaAccesoRapido.setContentDisplay(ContentDisplay.CENTER);
		lbTeclaAccesoRapido.getStyleClass().add("lb-quick-access");
		
		AnchorPane.setTopAnchor(lbTeclaAccesoRapido, 1.5);
		AnchorPane.setRightAnchor(lbTeclaAccesoRapido, 1.5);
		
		panelInterno.getChildren().add(lbTeclaAccesoRapido);
//		HBox.setMargin(lbTexto, new Insets(0, 0, -15, 0));
		
	}
	
	@Override
	protected void inicializeCustomComponents(Double ancho, Double alto) {
		if(alto == null) {
			setPrefHeight(0d);
        	GridPane.setHgrow(this, Priority.ALWAYS);
		}
		String rutaImagen = configuracion.getImagePath();
		if (rutaImagen != null) {
			Image image = SkinManager.getInstance().getImage(rutaImagen);
			if (image == null) {
				try {
					width = ancho;
					heigth = alto;
					
					image = createImageBoton(rutaImagen);
				}
				catch (Exception e) {
					log.error("inicializeCustomComponents() - No se ha podido crear la imagen " + rutaImagen + ": " + e.getMessage(), e);
				}
			}
			
			if(image != null) {
				loadImage(image);
			}
		}

		String text = configuracion.getText();
		if(StringUtils.isNotBlank(text)) {
			// Buscamos el texto del botón en las properties
			lbTexto.setText(I18N.getText(configuracion.getText()));
			lbTexto.setWrapText(getConfiguracionBoton().isWrapText());
			lbTexto.setPrefWidth(ancho!=null?ancho:-1);
			lbTexto.setMaxWidth(Double.MAX_VALUE);
		}

		// Establecemos el texto de acceso rápido
		if(lbTeclaAccesoRapido!=null) {
			lbTeclaAccesoRapido.setText(configuracion.getShortcut());
		}
	}
	
	@Override
	protected void loadImage(Image i) {
		imagen.setImage(i);
		imagen.setFitWidth(width);
		imagen.setFitHeight(heigth);
	}
	
	
	
}
