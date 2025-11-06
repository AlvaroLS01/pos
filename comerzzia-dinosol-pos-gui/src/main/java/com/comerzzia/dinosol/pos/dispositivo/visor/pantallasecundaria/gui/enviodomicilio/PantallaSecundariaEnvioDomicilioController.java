package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.enviodomicilio;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.FechaException;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasFechasDisponiblesBean;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;

@Component
public class PantallaSecundariaEnvioDomicilioController extends Controller implements Initializable {

	protected Logger log = Logger.getLogger(getClass());

	@FXML
	public VBox containerFechaDisponibleUno, containerFechaDisponibleDos, containerFechaDisponibleTres;

	@FXML
	public VBox containerTramoDisponibleUno, containerTramoDisponibleDos, containerTramoDisponibleTres;


	protected List<PantallaSecundariaEnvioDomicilioView> rutasRowCache = new ArrayList<>();

	@Override
	public void initializeFocus() {

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		refrescarDatosPantalla();
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
	}

	public void refrescarDatosPantalla() {
		List<RutasFechasDisponiblesBean> servicioDisponible = DinoVisorPantallaSecundaria.getDisponibilidad();
		updateRow(servicioDisponible);
	}

	protected void updateRow(List<RutasFechasDisponiblesBean> servicioDisponible) {

		containerFechaDisponibleUno.getChildren().clear();
		containerFechaDisponibleDos.getChildren().clear();
		containerFechaDisponibleTres.getChildren().clear();
		containerTramoDisponibleUno.getChildren().clear();
		containerTramoDisponibleDos.getChildren().clear();
		containerTramoDisponibleTres.getChildren().clear();

		int cont = 0;
		for (int i = 0; i < servicioDisponible.size(); i++) {
			/****/
			String diaSemana = calculaDiaSemana(servicioDisponible, i);
			String diaMes = calculaDiaMes(servicioDisponible, i);
			String mes = calculaMes(servicioDisponible, i);
			String fecha = diaSemana + " " + diaMes +"/" + mes;
			/****/

			Label fechaDisponible = new Label();
			Label tramoDisponible = new Label();

			fechaDisponible.setText(fecha);
			fechaDisponible.getStyleClass().add(".letra");
			tramoDisponible.setText(servicioDisponible.get(i).getTramoHorario().substring(0, servicioDisponible.get(i).getTramoHorario().length() - 3));
			tramoDisponible.getStyleClass().add(".linea-tramos");
			tramoDisponible.getStyleClass().add(".letra");
			
			

			if (!containerFechaDisponibleUno.getChildren().isEmpty()) {
				if (!((Label) containerFechaDisponibleUno.getChildren().get(cont)).getText().equals(fechaDisponible.getText())) {
					if (!containerFechaDisponibleDos.getChildren().isEmpty()) {
						if (!((Label) containerFechaDisponibleDos.getChildren().get(cont)).getText().equals(fechaDisponible.getText())) {
							if (containerFechaDisponibleTres.getChildren().isEmpty()) {
								containerFechaDisponibleTres.getChildren().add(fechaDisponible);
								containerTramoDisponibleTres.getChildren().add(tramoDisponible);
							}
							else {
								containerTramoDisponibleTres.getChildren().add(tramoDisponible);
							}
						}
						else {
							containerTramoDisponibleDos.getChildren().add(tramoDisponible);
						}
					}
					else {
						containerFechaDisponibleDos.getChildren().add(fechaDisponible);
						containerTramoDisponibleDos.getChildren().add(tramoDisponible);
					}
				}
				else {
					containerTramoDisponibleUno.getChildren().add(tramoDisponible);
				}
			}
			else {
				containerFechaDisponibleUno.getChildren().add(fechaDisponible);
				containerTramoDisponibleUno.getChildren().add(tramoDisponible);
			}
		}
	}

	private String calculaDiaSemana(List<RutasFechasDisponiblesBean> servicioDisponible, int i) {
		String diaSemana = "";
	    try {	    	
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(Fechas.getFecha(servicioDisponible.get(i).getFecha()));
	    	diaSemana = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,getApplication().getLocale());
	    	diaSemana = Character.toUpperCase(diaSemana.charAt(0)) + diaSemana.substring(1,diaSemana.length());
	    }
	    catch (FechaException e) {
	    }
		return diaSemana;
    }
	
	private String calculaDiaMes(List<RutasFechasDisponiblesBean> servicioDisponible, int i) {
		String diaMes = "";
	    try {	    	
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(Fechas.getFecha(servicioDisponible.get(i).getFecha()));
	    	diaMes = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	    }
	    catch (FechaException e) {
	    }
		return diaMes;
    }
	
	private String calculaMes(List<RutasFechasDisponiblesBean> servicioDisponible, int i) {
		String mes = "";
	    try {	    	
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(Fechas.getFecha(servicioDisponible.get(i).getFecha()));
	    	mes = String.valueOf(calendar.get(Calendar.MONTH));
	    	//mes =  calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,getApplication().getLocale());
	    	//mes = Character.toUpperCase(mes.charAt(0)) + mes.substring(1,mes.length());
	    }
	    catch (FechaException e) {
	    }
		return mes;
    }

}
