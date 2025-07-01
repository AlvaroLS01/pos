package com.comerzzia.pos.core.gui.components.actionbutton.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

public class ActionButtonImageComponent extends ActionButtonNormalComponent {

	private Logger log = Logger.getLogger(ActionButtonImageComponent.class);
	
	protected HBox linea1, linea3;

	protected ImageView imagen;
	
	protected Label lbTexto;
	
	protected double width, heigth;

	/**
	 * Constructor de botón con imagen y dos etiquetas
	 */
	public ActionButtonImageComponent() {
		super();

		linea1 = new HBox();
		linea3 = new HBox();

		imagen = new ImageView();
		imagen.setPreserveRatio(true);

		lbTexto = new Label();
		lbTexto.setAlignment(Pos.CENTER);
		lbTexto.setContentDisplay(ContentDisplay.CENTER);
		lbTexto.setTextAlignment(TextAlignment.CENTER);

		linea1.getChildren().add(imagen);
		linea1.setAlignment(Pos.CENTER);
		linea3.getChildren().add(lbTexto);
		linea3.setAlignment(Pos.CENTER);

		HBox.setHgrow(imagen, Priority.ALWAYS);
		HBox.setHgrow(lbTexto, Priority.ALWAYS);
		
		panelInterno.setPickOnBounds(true);
		panelInterno.getChildren().addAll(linea1, linea3);

		AnchorPane.setTopAnchor(linea1, 0.0);
		AnchorPane.setBottomAnchor(linea1, 0.0);
		AnchorPane.setLeftAnchor(linea1, 0.0);
		AnchorPane.setRightAnchor(linea1, 0.0);

		AnchorPane.setBottomAnchor(linea3, 0.0);
		AnchorPane.setLeftAnchor(linea3, 0.0);
		AnchorPane.setRightAnchor(linea3, 0.0);

		AnchorPane.setTopAnchor(btAccion, 0.0);
		AnchorPane.setBottomAnchor(btAccion, 0.0);
		AnchorPane.setLeftAnchor(btAccion, 0.0);
		AnchorPane.setRightAnchor(btAccion, 0.0);

		btAccion.setPickOnBounds(true);
		
		AnchorPane.setTopAnchor(panelBoton, 0.0);
		AnchorPane.setBottomAnchor(panelBoton, 0.0);
		AnchorPane.setLeftAnchor(panelBoton, 0.0);
		AnchorPane.setRightAnchor(panelBoton, 0.0);
		
		AnchorPane.setTopAnchor(panelInterno, 0.0);
		AnchorPane.setBottomAnchor(panelInterno, 0.0);
		AnchorPane.setLeftAnchor(panelInterno, 0.0);
		AnchorPane.setRightAnchor(panelInterno, 0.0);

		btAccion.setGraphic(panelInterno);

		lbTexto.getStyleClass().add("lb-composite-button");
	}

	@Override
	protected void inicializeCustomComponents(Double ancho, Double alto) {
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
					log.error("inicializaComponentesPersonalizados() - No se ha podido crear la imagen " + rutaImagen + ": " + e.getMessage(), e);
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
			lbTexto.setPrefWidth(ancho);
		}

		// Establecemos el texto de acceso rápido
		if(lbTeclaAccesoRapido!=null) {
			lbTeclaAccesoRapido.setText(configuracion.getShortcut());
		}
	}

	public Image createImageBoton(String path) throws URISyntaxException, MalformedURLException {
		if(path.startsWith("http")) {
			return getImageFromUrl(path);
		}
		else {			
			return getImageFromFile(path);
		}
	}

	protected Image getImageFromUrl(String path) {
		log.trace("getImageUrl() - Loading URL: " + path);
		
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				HttpURLConnection connection = null;
				Image i = null;
				try {
					InputStream inputStream = null;
					if (path.startsWith("http")) {
						URL url = new URL(path);
						connection = (HttpURLConnection) url.openConnection();
						
						if (url.getUserInfo() != null) {
						    String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
						    connection.setRequestProperty("Authorization", basicAuth);
						}
						
						inputStream = connection.getInputStream();
					}
					else {
						inputStream = new FileInputStream(path);
					}

					if (inputStream != null) {
						i = new Image(inputStream);
						loadImage(i);
					}
					else {
						log.warn("No se encontró la imagen en la URL: " + path);
					}
				}
				catch (Exception e) {
					log.debug("Ha habido un error al descargar la imagen: " + e.getMessage());
				}
				finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		});
		
		thread.start();
		
		return null;
	}

	protected Image getImageFromFile(String path) {
		InputStream is = null;
		Image i = null;
		try {
			URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

			File imageFile = new File(resource.getFile());
			try {
				is = new FileInputStream(imageFile);
			}
			catch (FileNotFoundException e) {
				log.warn("No se encontró la imagen dentro del directorio config: " + imageFile.toURI());
			}
			if (is == null) {
				imageFile = new File(resource.getFile());
				try {
					is = new FileInputStream(imageFile);
				}
				catch (FileNotFoundException e) {
					log.warn("No se encontró la imagen dentro del directorio config: " + imageFile.toURI());
				}
				
				resource = null;
			}

			if (is != null) {
				i = new Image(is);
			}
			
			imageFile = null;
		}
		catch (Exception e) {
			log.warn("No se encontró la imagen dentro del directorio config: " + path);
		}
		finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			}
			catch (IOException e) {
				log.warn("createImage() - Error al cerrar InputStream " + e.getMessage(), e);
			}
		}
		return i;
	}
	
	@Override
	public void destroy() {
		super.destroy();
	    
	    linea1 = null;
	    linea2 = null;
	    linea3 = null;
	    lbTexto = null;
	    imagen = null;
	}

	protected void loadImage(Image i) {
		imagen.setImage(i);
		imagen.setFitWidth(width - 4);
		imagen.setFitHeight(heigth - 2);
	}

}
