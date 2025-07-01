
package com.comerzzia.pos.core.gui.components.itemimage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.comerzzia.pos.util.config.AppConfig;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Componente para pintar la imagen de un artículo.
 * 
 */
public class ItemImage extends ImageView {

	private Logger log = Logger.getLogger(ItemImage.class.getName());

	private Thread thread;
	private ImageDownload runnable;
	
	private boolean searchDefaultImage = false;

	/**
	 * Muestra la imagen del artículo pasado por parámetro
	 * 
	 * @param articulo
	 *            Código del artículo
	 */
	public void showImage(String articulo) {
		super.setImage(null);
		
		searchDefaultImage = false;

		String rutaImagen = AppConfig.getCurrentConfiguration().getImagesPath();
		if (!rutaImagen.endsWith("/")) {
			rutaImagen = rutaImagen + "/";
		}
		rutaImagen = rutaImagen + articulo + ".jpg";

		downloadImage(rutaImagen);
	}

	@SuppressWarnings("deprecation")
	private void downloadImage(String rutaImagen) {
		if (thread != null) {
			try {
				thread.interrupt();
				thread.stop();
			}
			catch (Exception e) {
				log.debug("Se ha cerrado un hilo con un proceso en ejecución.");
			}
		}

		runnable = new ImageDownload();
		runnable.setUrl(rutaImagen);
		thread = new Thread(runnable);
		thread.start();
	}

	private void changeImage(InputStream inputStream) {
		Image image = new Image(inputStream);
		super.setImage(image);
	}

	private class ImageDownload implements Runnable {

		private String url;

		public void setUrl(String url) {
			this.url = url;
		}

		private void downloadImage(String imageUrl) {
			HttpURLConnection connection = null;
			try {
				InputStream inputStream = null;
				if (imageUrl.startsWith("http")) {
					URL url = new URL(imageUrl);
					connection = (HttpURLConnection) url.openConnection();
					
					if (url.getUserInfo() != null) {
					    String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
					    connection.setRequestProperty("Authorization", basicAuth);
					}
					
					inputStream = connection.getInputStream();
				}
				else {
					inputStream = new FileInputStream(imageUrl);
				}

				if (inputStream != null) {
					changeImage(inputStream);
				}
				else {
					if(AppConfig.getCurrentConfiguration().getDefaultImage() != null && !searchDefaultImage) {
						searchDefaultImage = true;
						downloadImage(AppConfig.getCurrentConfiguration().getDefaultImage());
					}
				}
			}
			catch (Exception e) {
				log.debug("Ha habido un error al descargar la imagen: " + e.getMessage());
				if(AppConfig.getCurrentConfiguration().getDefaultImage() != null && !searchDefaultImage) {
					searchDefaultImage = true;
					downloadImage(AppConfig.getCurrentConfiguration().getDefaultImage());
				}
			}
			finally {
				if (connection != null) {
					connection.disconnect();
				}
			}

		}

		@Override
		public void run() {
			downloadImage(url);
		}
	}

}
