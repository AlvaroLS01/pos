package com.comerzzia.pos.core.gui.controllers;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;

@Component
@CzzActionScene
public class WebController extends ActionSceneController implements Initializable {

    private static final Logger log = Logger.getLogger(WebController.class.getName());

    @FXML
    protected ProgressBar progressBar;
    
    @FXML
    protected WebView navegador;
    
    @FXML
    protected Label lbTitulo;
        
    protected String url;
    
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

	/**Configura SSL para que acepte todos los certificados*/
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
        }else if(newState == State.READY || newState == State.SCHEDULED) {
        	progressBar.setVisible(true);
        }else if(newState == State.FAILED) {
        	log.warn("loadWorkerStateChanged() - Fallo al cargar la url", navegador.getEngine().getLoadWorker().getException());
        }
	}

	@Override
	public void initializeFocus() {
		
	}
    
    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	ActionDetail accion = getAction();
        
        lbTitulo.setText(accion.getTitle());
        if(accion.getParams() != null){
        	if (url == null || !url.equals(accion.getParams())) {
	        	this.url = accion.getParams();
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
        }else{
        	log.error("initializeForm() - La acción no tiene URL en la columna parámetros. No hacemos nada.");
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
