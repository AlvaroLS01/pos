package com.comerzzia.pos.devices.linedisplay;

import java.awt.Rectangle;
import java.net.URL;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.omnichannel.facade.model.notification.Notification;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.controllers.SceneControllerBuilder;
import com.comerzzia.pos.core.gui.controllers.SecondaryScreenSceneController;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.sale.SecondaryScreenSaleController;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.sale.SecondaryScreenSaleControllerAbstract;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.scale.SecondaryScreenScaleController;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.scale.SecondaryScreenScaleControllerAbstract;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.tender.SecondaryScreenTenderController;
import com.comerzzia.pos.devices.linedisplay.secondaryscreen.tender.SecondaryScreenTenderControllerAbstract;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;

public class DeviceLineDisplaySecondaryScreen extends DeviceAbstractImpl implements DeviceLineDisplay {
	
	protected static final Logger log = Logger.getLogger(DeviceLineDisplaySecondaryScreen.class.getName());
	
	public static final String TAG_CT_SCREEN_CONFIG = "config";
	public static final String TAG_CT_SCREEN_WAITSCREEN = "waitscreen";
	public static final String TAG_CT_SCREEN_NUMBER = "screennumber";
	
	protected String web;
	
	protected Integer screenNumber;
		
	protected Screen secondaryScreen;
	
	protected WebView webView;
	
	protected static BasketPromotable<?> basket;
	
	protected Scene scene;
		
	public static int MODE_OFF = 0;
	
	public static int MODE_ON = 1;
	
	public static int MODE_WAIT = 2;
	
	public static int MODE_ITEMIZATION = 3;
	
	public static int MODE_TENDER = 4;

	public static int MODE_WEIGHT = 5;
	
	protected static int mode;
	
	protected JFrame frame = null;
	
	protected SecondaryScreenSaleControllerAbstract salesController;
	
	protected SecondaryScreenTenderControllerAbstract paymentsController;

	protected SecondaryScreenScaleControllerAbstract scaleController;
		    	
	protected void initializaWindow(){
		// crear ventana Swing
		Rectangle r = new Rectangle();
		r.setLocation(Double.valueOf(secondaryScreen.getBounds().getMinX()).intValue(), Double.valueOf(secondaryScreen.getBounds().getMinY()).intValue());
		r.setSize(Double.valueOf(secondaryScreen.getBounds().getWidth()).intValue(), Double.valueOf(secondaryScreen.getBounds().getHeight()).intValue());
		
		frame = new JFrame();
		frame.setFocusableWindowState(false);
		frame.setBounds(r);		
		frame.setUndecorated(true);
		frame.setEnabled(false);
		frame.setFocusable(false);		
        
        webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        URL url = getClass().getResource("/" + web);
        webEngine.load(url.toExternalForm());
        if(scene == null) {
        	scene = new CssScene(webView, secondaryScreen.getBounds().getWidth(), secondaryScreen.getBounds().getHeight());
        }
        // asignar vista/escena a ventana swing
		JFXPanel fxPanel = new JFXPanel();
		fxPanel.setFocusable(false);
		fxPanel.setEnabled(false);
		frame.add(fxPanel);
		
		fxPanel.setScene(scene);
		
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	frame.setVisible(true);		
	        }
	    });
	}
	
	@Override
	public void connect() {
        if (Screen.getScreens().size() > 1) {
        	if(screenNumber!=null && Screen.getScreens().size()>screenNumber) {
        		secondaryScreen =Screen.getScreens().get(screenNumber);
        	} else {
        		Screen pantallaPrincipal = Screen.getPrimary();
        		for(Screen screen : Screen.getScreens()) {
        			if(!screen.equals(pantallaPrincipal)) {
        				secondaryScreen = screen;
        				break;
        			}

        		}
        	}
            
            initializaWindow();
            
            setState(MODE_ON);
            
            setMode(MODE_ON);
            
    		standbyMode();
        }else{
        	setState(MODE_OFF);
        	disconnect();
        	Notifications.get().addNotification(new Notification(I18N.getText("Compruebe que el segundo monitor está encendido."), Notification.NotificationType.WARN, new Date()));
        }
	}
	
	@Override
	public void disconnect() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
	}
	
	@Override
	public void standbyMode() {
		if(Screen.getScreens().size()>1) {
			if(getState()==MODE_ON && getMode()!=MODE_WAIT){
				try {
					scene.setRoot(webView);
					setMode(MODE_WAIT);
				} catch (Exception e) {
					log.error("modoEspera() Error cargando vista:" +e.getMessage(), e);
				}
			}else if(getState()==MODE_OFF){
				connect();
			}
		}else{
			if(getState()==MODE_ON){
				Notifications.get().addNotification(new Notification(I18N.getText("Compruebe que el segundo monitor está encendido."), Notification.NotificationType.WARN, new Date()));
			}
			setState(MODE_OFF);
		}
	}

	@Override
	public void saleMode(BasketPromotable<?> basket) {
		try {
			if(salesController == null) {
				if(Screen.getScreens().size()>1){
					if(getState()==MODE_ON){
				        try {
				        	setBasket(basket);
							setMode(MODE_ITEMIZATION);
							salesController = (SecondaryScreenSaleControllerAbstract) ((SceneControllerBuilder)CoreContextHolder.getInstance("sceneControllerBuilder")).buildSceneController(getSaleController(), null, null);
				        	scene.setRoot(salesController.getSceneView().getViewNode());
				        	salesController.refrescarDatosPantalla();
						} catch (InitializeGuiException e) {
							log.error("saleMode() Error cargando vista:" +e.getMessage(), e);
						}
					}
					else{
						connect();
					}
				}else{
					if(getState()==MODE_ON){
						Notifications.get().addNotification(new Notification(I18N.getText("Compruebe que el segundo monitor está encendido."), Notification.NotificationType.WARN, new Date()));
					}
					setState(MODE_OFF);
				}
			}
			else {
				scene.setRoot(salesController.getSceneView().getViewNode());
				setBasket(basket);
				setMode(MODE_ITEMIZATION);
				salesController.refrescarDatosPantalla();
			}
		}
		catch(Exception e) {
			log.error("saleMode() - Ha habido un error al mostrar el modo venta en la pantalla secundaria: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void tenderMode(BasketPromotable<?> basket) {
		try {
			if(paymentsController == null) {
				if(Screen.getScreens().size()>1){
					if(getState()==MODE_ON){
				        try {
				        	setBasket(basket);
							setMode(MODE_TENDER);
							paymentsController = (SecondaryScreenTenderControllerAbstract) ((SceneControllerBuilder)CoreContextHolder.getInstance("sceneControllerBuilder")).buildSceneController(getTenderController(), null, null);
				        	scene.setRoot(paymentsController.getSceneView().getViewNode());
				        	paymentsController.refrescarDatosPantalla();
						} catch (InitializeGuiException e) {
							log.error("tenderMode() Error cargando vista:" +e.getMessage(), e);
						}
					}
					else{
						connect();
					}
				}else{
					if(getState()==MODE_ON){
						Notifications.get().addNotification(new Notification(I18N.getText("Compruebe que el segundo monitor está encendido."), Notification.NotificationType.WARN, new Date()));
					}
					setState(MODE_OFF);
				}
			}
			else {
				scene.setRoot(paymentsController.getSceneView().getViewNode());
				setBasket(basket);
				setMode(MODE_TENDER);
				paymentsController.refrescarDatosPantalla();
			}
		}
		catch(Exception e) {
			log.error("tenderMode() - Ha habido un error al mostrar el modo pago en la pantalla secundaria: " + e.getMessage(), e);
		}
	}
	
	public void weightRequiredMode(int status, BasketItem basketItem) {
		try {
			if(Screen.getScreens().size()<=1) {
				setState(MODE_OFF);
				return;
			}
			
			if(getState()!=MODE_ON){
				connect();
			}
			
			if(scaleController == null) {
				scaleController = (SecondaryScreenScaleControllerAbstract) buildSecondaryScreenSceneController(getScaleController());
			}
			
			setMode(MODE_WEIGHT);
			scene.setRoot(scaleController.getSceneView().getViewNode());
        	scaleController.updateScaleStatus(status, basketItem);
		}catch(InitializeGuiException e) {
			log.error("scaleMode() - Ha habido un error al mostrar el modo balanza en la pantalla secundaria: " + e.getMessage(), e);
		}catch(Exception e) {
			log.error("scaleMode() - Ha habido un error al mostrar el modo balanza en la pantalla secundaria: " + e.getMessage(), e);
		}
	}
	
	protected SceneController buildSecondaryScreenSceneController(Class<? extends SecondaryScreenSceneController> clazz) throws InitializeGuiException {
		return ((SceneControllerBuilder)CoreContextHolder.getInstance("sceneControllerBuilder")).buildSceneController(clazz, null, null);
	}
	
	public static int getMode() {
		return mode;
	}

	public static void setMode(int modo) {
		DeviceLineDisplaySecondaryScreen.mode = modo;
		if (modo == MODE_WAIT) {
			basket = null;
		}
	}

	
	
	@Override
	public void loadConfiguration(DeviceConfiguration config) {
    	try {
    		XMLDocumentNode datos = config.getModelConfiguration().getConnectionConfig().getNode(TAG_CT_SCREEN_CONFIG, true);
    		
    		XMLDocumentNode auxNodo;
    		
    		if(datos!=null){
    			auxNodo = datos.getNode(TAG_CT_SCREEN_WAITSCREEN, true);
    			if (auxNodo != null) {
    				web = auxNodo.getValue();
    			}
    			auxNodo = datos.getNode(TAG_CT_SCREEN_NUMBER, true);
    			if (auxNodo != null) {
    				Integer auxVal = auxNodo.getValueAsInteger();
    				if(auxVal<1) {
    					log.warn("loadConfiguration() - La pantalla secundaria configurada debe ser la segunda o superior.");
    				}else {
    					screenNumber = auxVal-1;
    				}
    			}
    		}else{
    			log.error("Error: Los parámetros de configuración del fichero XML son correctos.");
    			Notifications.get().addNotification(new Notification(I18N.getText("Los parámetros de configuración de la pantalla no son correctos."), Notification.NotificationType.WARN, new Date()));
    		}
    	}
    	catch (XMLDocumentNodeNotFoundException ex) {
    		log.error("Error: Los parámetros de configuración del fichero XML son correctos.", ex);
    		Notifications.get().addNotification(new Notification(I18N.getText("Los parámetros de configuración de la pantalla no son correctos."), Notification.NotificationType.WARN, new Date()));
    	}
	}

	public static BasketPromotable<?> getBasket() {
		return basket;
	}

	public void setBasket(BasketPromotable<?> basket) {
		DeviceLineDisplaySecondaryScreen.basket = basket;
	}
	
	@Override
	public void write(String cad1, String cad2) {
	}

	@Override
	public void writeLineUp(String cadena) {
	}

	@Override
	public void writeLineDown(String cadena) {
	}

	@Override
	public void clear() {
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
		if(secondaryScreen!=null) {
			initializaWindow();
		}
	}

	@Override
	public Integer getNumColumns() {
		return null;
	}

	@Override
	public Integer getNumRows() {
		return null;
	}

	@Override
	public void writeRightUp(String cadena) {
	}

	@Override
	public void writeRightDown(String cadena) {
	}
	
	public Class<? extends SecondaryScreenSaleControllerAbstract> getSaleController(){
		return SecondaryScreenSaleController.class;
	}
	public Class<? extends SecondaryScreenTenderControllerAbstract> getTenderController(){
		return SecondaryScreenTenderController.class;
	}
	public Class<? extends SecondaryScreenScaleControllerAbstract> getScaleController(){
		return SecondaryScreenScaleController.class;
	}

	
}
