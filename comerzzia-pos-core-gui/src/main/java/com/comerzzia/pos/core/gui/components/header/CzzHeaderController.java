package com.comerzzia.pos.core.gui.components.header;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.omnichannel.facade.model.store.StorePosBusinessData;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.components.ComponentController;
import com.comerzzia.pos.core.gui.notifications.NotificationsWindow;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;
import com.comerzzia.pos.core.services.notifications.Notifications;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.POSUserSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.events.CzzCloseEvent;
import com.comerzzia.pos.util.events.CzzHeaderEvent;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CzzHeaderController extends ComponentController implements Initializable , Observer{
	@FXML
	protected Label lbDate, lbTime, lbPOSData, lbCashier, lbCloseButton, lbNotifications;
	
	@FXML
	protected HBox hbClose, hbMenu;
	
	@FXML
	protected ImageView ivCustomerLogo;
	
	protected NotificationsWindow notificationsWindow;
	
	@Autowired
	protected ApplicationSession sesionAplicacion;
	@Autowired
	protected POSUserSession sesionUsuario;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		updateWarehoueLabels();
		updateCashierLabel();
		updateNotificationsLabel();
		
		log.debug("initialize() - Creando el timeline de refresco para el reloj");
		updateClock();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int segundos = 60 - calendar.get(Calendar.SECOND);

		Timeline delayTimeline = new Timeline();
		delayTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(segundos), new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				Timeline cadaMinuto = new Timeline();
				cadaMinuto.setCycleCount(Timeline.INDEFINITE);
				cadaMinuto.getKeyFrames().add(new KeyFrame(Duration.seconds(60), new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent event) {
						updateNotificationsLabel();
						updateClock();
					}
				}));
				cadaMinuto.play();
			}
		}));
		delayTimeline.play();
		
	}
	
	@Override
	public void initializeComponents() {
		notificationsWindow = new NotificationsWindow(((MainStageManager)CoreContextHolder.getInstance("mainStageManager")).getStage());
		notificationsWindow.getNotificationsPopup().setOpenerButton(lbNotifications);
		Image imageCustomerLogo = ((CzzImageManager)CoreContextHolder.getInstance("czzImageManager")).getLogo(CzzImageManager.IMAGES_CUSTOMER_LOGO_NAME);
		ivCustomerLogo.setImage(imageCustomerLogo);
	}

	protected void updateNotificationsLabel() {
		lbNotifications.setText(String.valueOf(Notifications.get().count()));
	}
	
	protected void updateCashierLabel() {
		Session session = SpringContext.getBean(Session.class);
		if (session.getPOSUserSession().getUser() != null) {
			lbCashier.setText(session.getPOSUserSession().getUser().getUserDes());
		}
	}
	
	protected void updateWarehoueLabels() {
		Session session = SpringContext.getBean(Session.class);
		StorePosBusinessData storePosData = session.getApplicationSession().getStorePosBusinessData();
		String tillData = storePosData.getWarehouse().getWhCode() + " · " + storePosData.getWarehouse().getWhDes() + " · " + storePosData.getStorePos().getTillCode();
		lbPOSData.setText(tillData);
	}
	
	protected void updateClock() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		lbDate.setText(sdfDate.format(new Date()));

		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		lbTime.setText(sdfTime.format(new Date()));
	}
	
	public void showMenuAction() {
		hbMenu.fireEvent(new CzzHeaderEvent(CzzHeaderEvent.OPEN_MENU_EVENT));
	}
	
	public void openNotifications() {
		notificationsWindow.showWindow();
	}

	public void close() {
		hbClose.fireEvent(new CzzCloseEvent(CzzCloseEvent.CLOSE_EVENT));
	}
	
	@Override
	public void addEventFilters() {
		Session session = SpringContext.getBean(Session.class);
		session.getApplicationSession().addObserver(this);
		session.getPOSUserSession().addObserver(this);
		getScene().addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.M && keyEvent.isControlDown()) {
					showMenuAction();
				}
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		updateCashierLabel();
		updateWarehoueLabels();
	}
	
}
