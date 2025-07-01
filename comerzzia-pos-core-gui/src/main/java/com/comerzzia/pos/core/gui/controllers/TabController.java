package com.comerzzia.pos.core.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.util.events.CzzTabEvent;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class TabController extends AnchorPane implements Initializable {
	
	@FXML
	protected Node tabroot;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		addEventsListeners();
		initializeTab(arg0, arg1);
	}
	
	protected void addEventsListeners() {
		tabroot.addEventHandler(CzzTabEvent.TAB_EVENT, new EventHandler<CzzTabEvent>() {
			@Override
			public void handle(CzzTabEvent arg0) {
				log.debug("addEventHandlers() - CzzTabEvent.TAB_EVENT: Tab Selected");
				try {
					selected();
				} catch (InitializeGuiException e) {
					log.error("addEventHandlers() - CzzTabEvent.TAB_EVENT Error: ", e);
					DialogWindowBuilder.getBuilder(tabroot.getScene().getWindow()).simpleThrowableDialog(I18N.getText("Se ha lanzado un error cargando los datos de la pestaña."),e);
				}
			}
		});
	}
	
	public abstract void initializeTab(URL arg0, ResourceBundle arg1);

	public abstract void selected() throws InitializeGuiException;

}