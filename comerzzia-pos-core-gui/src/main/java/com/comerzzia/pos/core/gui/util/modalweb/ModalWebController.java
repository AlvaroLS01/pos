package com.comerzzia.pos.core.gui.util.modalweb;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.ResourceBundle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;

@Component
@CzzScene
public class ModalWebController extends SceneController {
	
	private Logger log = Logger.getLogger(ModalWebController.class);
	
	public static String PARAM_URL = "ModalWebController.URL";
	public static String PARAM_PARENT_SCENE = "ModalWebController.PARENT_SCENE";

	@FXML
	protected ProgressBar progressBar;

	@FXML
	protected WebView navegador;

	@FXML
	protected Label lbTitulo;

	protected String url;
	protected SceneController parentScene;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		final WebEngine webEngine = navegador.getEngine();

		webEngine.setConfirmHandler(new Callback<String, Boolean>() {

			@Override
			public Boolean call(String mensaje) {
				return DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(mensaje);
			}

		});

		webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {

			@Override
			public void handle(WebEvent<String> event) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(event.getData());
			}
		});

		configureSSL();

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				loadWorkerStateChanged(ov, oldState, newState);
			}
		});
	}

	/** Configura SSL para que acepte todos los certificados */
	protected void configureSSL() throws InitializeGuiException {
		try {
			TrustManager trm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			};
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { trm }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier nullVerifier = new HostnameVerifier() {
				@Override
				public boolean verify(String paramString, SSLSession paramSSLSession) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(nullVerifier);
		} catch (Exception e) {
			throw new InitializeGuiException(e);
		}
	}

	protected void loadWorkerStateChanged(ObservableValue<? extends State> ov, State oldState, State newState) {
		if (newState == State.SUCCEEDED) {
			lbTitulo.setText(navegador.getEngine().getLocation());
			progressBar.setVisible(false);
		} else if (newState == State.READY || newState == State.SCHEDULED) {
			progressBar.setVisible(true);
		} else if (newState == State.FAILED) {
			log.warn("loadWorkerStateChanged() - Fallo al cargar la url",
					navegador.getEngine().getLoadWorker().getException());
		}
	}

	@Override
	public void initializeFocus() {

	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		if(sceneData.get(ModalWebController.PARAM_PARENT_SCENE) != null) {
			parentScene = (SceneController) sceneData.get(ModalWebController.PARAM_PARENT_SCENE);
		} else  {
			parentScene = null;
		}
		if (url == null || !url.equals((String) sceneData.get(ModalWebController.PARAM_URL))) {
        	this.url = (String) sceneData.get(ModalWebController.PARAM_URL);
	        final WebEngine webEngine = navegador.getEngine();
	        
	    	webEngine.setConfirmHandler(new Callback<String, Boolean>() {

				@Override
				public Boolean call(String mensaje) {
					return DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(mensaje);
				}
			});
			
			webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
				
				@Override
				public void handle(WebEvent<String> event) {
					DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(event.getData());
				}
			});
			
	        progressBar.setVisible(true);
	        progressBar.setPrefHeight(progressBar.getMaxHeight());
	        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
	        
	        log.debug("initializeForm() - Cargando URL: " + url.toString());
	        webEngine.load(url);
    	}
	}
	
	@Override
	public void onURLMethodCalled(String method, Map<String, String> params) {
		if(parentScene != null) {
			parentScene.onURLMethodCalled(method, params);
		}else {
			super.onURLMethodCalled(method, params);
		}
	}


	@Override
	public boolean canClose() {
		boolean canClose = super.canClose();
		if (canClose) {
			this.url = null;
		}
		return canClose;
	}

}
