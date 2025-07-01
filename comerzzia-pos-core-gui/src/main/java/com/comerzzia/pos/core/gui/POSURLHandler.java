
package com.comerzzia.pos.core.gui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.gui.controllers.StageController;

import javafx.application.Platform;
import lombok.extern.log4j.Log4j;

@Log4j
public class POSURLHandler extends URLStreamHandler {
	protected static Map<String, List<URLMethodHandler>> methods = new HashMap<>();
	protected static MainStageManager mainStageManager = CoreContextHolder.getInstance("mainStageManager");
			
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		final String host = u.getHost();
		final Map<String, String> paramsMap = splitQuery(u);
		
		
		if(host.equals("skin")) { 
            return new CzzSkinResourceUrlConnection(u);
		} else if(host.equals("images")) {
			return new CzzImgResourceUrlConnection(u);
		} else if(host.equals("method")) {
			String method = u.getPath().split("\\/")[1];
			final List<URLMethodHandler> list = methods.get(method);
			
			//For compatibility
			if(list != null) {
				Platform.runLater(() -> {
					for (URLMethodHandler urlMethodHandler : list) {
						urlMethodHandler.onURLMethodCalled(method, paramsMap);
					}
				});
				return new URL("nousa:").openConnection();
			}
			//new url method handler
			Platform.runLater(() -> sceneOnUrlMethodCalled(method, paramsMap));
		} else {
			log.warn(String.format("openConnection() - " + "No hay manejadores registrados para el método '%s'. Entramos en modo retrocompatibilidad", host));
			String method = u.getHost();
			
			Platform.runLater(() -> sceneOnUrlMethodCalled(method, paramsMap));
			
		}
		return new URL("nousa:").openConnection();
	}

	protected void sceneOnUrlMethodCalled(String method, final Map<String, String> paramsMap) {
		StageController currentStage = mainStageManager.getFocusedStageController();
		if(currentStage == null) {
			currentStage = mainStageManager;
		}
		currentStage.getCurrentScene().onURLMethodCalled(method, paramsMap);
	}
	
	
	public interface URLMethodHandler {
		public void onURLMethodCalled(String method, Map<String, String> params);
	}
	
	/**
	 * @deprecated implement {@link com.comerzzia.pos.core.gui.controllers.SceneController#onURLMethodCalled(String, Map) onURLMethodCalled} on the scene controller instead
	 * 
	 * @param method
	 * @param methodHandler
	 */
	@Deprecated
	public static void addMethodHandler(String method, URLMethodHandler methodHandler){
		List<URLMethodHandler> list = methods.putIfAbsent(method, new ArrayList<>());
		
		list.clear();
		list.add(methodHandler);
	}
	
	public static Map<String, String> splitQuery(URL url) {
	    Map<String, String> queryPairs = new LinkedHashMap<>();
	    String query = url.getQuery();
	    if(query != null) {
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        try {
		        	if(idx!=-1) {
		        		queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		        	}
				} catch (UnsupportedEncodingException e) {
					log.error("splitQuery() - UnsupportedEncodingException", e);
				}
		    }
	    }
	    return queryPairs;
	}
	
	public static void clearMethods() {
		methods.clear();
	}
	

}
