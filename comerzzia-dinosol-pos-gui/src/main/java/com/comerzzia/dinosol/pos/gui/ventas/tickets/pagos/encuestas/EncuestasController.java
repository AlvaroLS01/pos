package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.encuestas;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.persistence.encuestas.Encuesta;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuesta;
import com.comerzzia.dinosol.pos.services.encuestas.EncuestaTicket;
import com.comerzzia.dinosol.pos.services.encuestas.RespuestaEncuestaTicket;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
@SuppressWarnings("rawtypes")
public class EncuestasController extends WindowController {
	
	public static final String PARAM_ENCUESTAS = "EncuestasController.Encuestas";
	
	public static final String PARAM_TICKET = "EncuestasController.Ticket";
	
	public static final String PARAM_RESPUESTAS = "EncuestasController.Respuestas";
	
	@FXML
	private Label lbDescripcionEncuesta;
	
	@FXML
	private VBox vbPreguntas;
	
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;
	
	private List<Encuesta> encuestas;
	
	private int indiceEncuesta;
	
	private int indicePregunta;
	
	private List<EncuestaTicket> encuestasHechas;
	
	private EncuestaTicket encuestaActual;
	
	private IVisor visor;
	
	private ITicket ticket;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		visor = Dispositivos.getInstance().getVisor();
	}

	@Override
	public void initializeFocus() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeForm() throws InitializeGuiException {
		encuestas = (List<Encuesta>) getDatos().get(PARAM_ENCUESTAS);
		ticket = (ITicket) getDatos().get(PARAM_TICKET);
		
		indiceEncuesta = 0;
		indicePregunta = -1;
		encuestasHechas = new ArrayList<EncuestaTicket>();
		
		refrescarDatosPantalla();
	}

	private void refrescarDatosPantalla() {
		Encuesta encuesta = encuestas.get(indiceEncuesta);
		
		vbPreguntas.getChildren().clear();
		
		if(indicePregunta == -1) {
			pintarPantallaInicialEncuesta(encuesta);
		}
		else if (indicePregunta == encuesta.getPreguntas().size()) {
			indicePregunta = -1;
			terminarEncuesta();
		}
		else {
			pintarPregunta(encuesta);
		}
	}

	private void pintarPregunta(Encuesta encuesta) {
		PreguntaEncuesta pregunta = encuesta.getPreguntas().get(indicePregunta);
		
		Label lbMigas = new Label();
		lbMigas.setText(I18N.getTexto("Pregunta {0} de {1}", indicePregunta+1, encuesta.getPreguntas().size()));
		vbPreguntas.getChildren().add(lbMigas);
		
		Label lbTextoPregunta = new Label(pregunta.getTexto());
		lbTextoPregunta.getStyleClass().add("textoResaltado");
		vbPreguntas.getChildren().add(lbTextoPregunta);
		
		String tipoPregunta = pregunta.getTipo();
		
		HBox hbContenedorPreguntas = new HBox();
		VBox.setVgrow(hbContenedorPreguntas, Priority.ALWAYS);
		
		VBox vbFormulario = new VBox();
		vbFormulario.setSpacing(15.0);
		hbContenedorPreguntas.getChildren().add(vbFormulario);
		HBox.setMargin(vbFormulario, new Insets(0, 300.0, 0, 300.0));
		
		Button btComenzar = new Button();
		btComenzar.getStyleClass().add("btAceptar");
		btComenzar.setText(I18N.getTexto("Continuar"));
		
		if(tipoPregunta.equals("Abierta")) {
			generarPreguntaAbierta(pregunta, vbFormulario, btComenzar);
		}
		else if (tipoPregunta.equals("Opciones")) {
			generarPreguntaOpciones(pregunta, vbFormulario, btComenzar);
		}
		else if (tipoPregunta.equals("Países")) {
			generarPreguntaPaises(pregunta, vbFormulario, btComenzar);
		}
		else if (tipoPregunta.equals("Satisfacción")) {
			generarPreguntaSatisfaccion(pregunta, vbFormulario, btComenzar);
		}
		
		vbPreguntas.getChildren().add(hbContenedorPreguntas);
		vbPreguntas.getChildren().add(btComenzar);
		
		if(encuesta.getMostrarEnVisor().equals("S") && visor instanceof DinoVisorPantallaSecundaria) {
			((DinoVisorPantallaSecundaria) visor).modoEncuesta(encuesta, pregunta);
		}
		else {
			visor.modoPago(visorConverter.convert((TicketVenta) ticket));
		}
	}

	private void generarPreguntaPaises(PreguntaEncuesta pregunta, VBox vbFormulario, Button btComenzar) {
		String[] opciones = {"Deutschland", "Éireann", "España", "France", "Italia", "Netherlands", "Sverige", "Norway", "United Kingdom", "Others"};
		String[] traduccion = {" - Alemania", " - Irlanda", "", " - Francia", "", " - Países Bajos", " - Suecia", " - Noruega", " - Reino Unido", " - Andere - Otros"};
		
		ToggleGroup toggleGroup = new ToggleGroup();
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			RadioButton rbOpcion = new RadioButton();
			rbOpcion.setToggleGroup(toggleGroup);
			
			Image image = POSApplication.getInstance().createImage("iconos/banderas/" + opcion.replace("/", "_") + ".png");
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(40.0);
			imageView.setPreserveRatio(true);
			rbOpcion.setText(opcion + traduccion[i]);
			rbOpcion.setGraphic(imageView);
			rbOpcion.setUserData(opcion);
			rbOpcion.setStyle("-fx-font-size:15px");
			
			vbFormulario.getChildren().add(rbOpcion);
		}
		
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old, Toggle newToggle) {
				if (toggleGroup.getSelectedToggle() != null) {
					String respuestaMarcada = toggleGroup.getSelectedToggle().getUserData().toString();
					((DinoVisorPantallaSecundaria) visor).setRespuestaMarcada(respuestaMarcada);
				}
			}
		});
		
		btComenzar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if(toggleGroup.getSelectedToggle() != null) {
					RespuestaEncuestaTicket respuesta = new RespuestaEncuestaTicket();
					respuesta.setOrden(pregunta.getOrden().longValue());
					String textoRespuesta = (String) ((RadioButton) toggleGroup.getSelectedToggle()).getUserData();
					respuesta.setRespuesta(textoRespuesta);
					encuestaActual.addRespuesta(respuesta);
				}
				
				indicePregunta++;
				refrescarDatosPantalla();
			}
		});
		
		vbFormulario.setSpacing(5.0);
	}

	private void generarPreguntaSatisfaccion(PreguntaEncuesta pregunta, VBox vbFormulario, Button btComenzar) {
		String[] opciones = {"1", "2", "3", "4", "5"};
		
		ToggleGroup toggleGroup = new ToggleGroup();
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			RadioButton rbOpcion = new RadioButton();
			rbOpcion.setToggleGroup(toggleGroup);
			
			Image image = POSApplication.getInstance().createImage("iconos/satisfaccion-" + opcion + ".png");
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(70.0);
			imageView.setPreserveRatio(true);
			rbOpcion.setText(opcion);
			rbOpcion.setGraphic(imageView);
			rbOpcion.setUserData(opcion);
			rbOpcion.setStyle("-fx-font-size:30px");
			
			vbFormulario.getChildren().add(rbOpcion);
		}
		
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old, Toggle newToggle) {
				if (toggleGroup.getSelectedToggle() != null) {
					String respuestaMarcada = toggleGroup.getSelectedToggle().getUserData().toString();
					((DinoVisorPantallaSecundaria) visor).setRespuestaMarcada(respuestaMarcada);
				}
			}
		});
		
		btComenzar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if(toggleGroup.getSelectedToggle() != null) {
					RespuestaEncuestaTicket respuesta = new RespuestaEncuestaTicket();
					respuesta.setOrden(pregunta.getOrden().longValue());
					String textoRespuesta = (String) ((RadioButton) toggleGroup.getSelectedToggle()).getUserData();
					respuesta.setRespuesta(textoRespuesta);
					encuestaActual.addRespuesta(respuesta);
				}
				
				indicePregunta++;
				refrescarDatosPantalla();
			}
		});
		
		vbFormulario.setSpacing(3.0);
	}

	private void generarPreguntaOpciones(PreguntaEncuesta pregunta, VBox vbFormulario, Button btComenzar) {
		String opcionesString = pregunta.getOpciones();
		
		String[] opciones = opcionesString.split("\\r?\\n");
		
		ToggleGroup toggleGroup = new ToggleGroup();
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			RadioButton rbOpcion = new RadioButton();
			rbOpcion.setToggleGroup(toggleGroup);
			rbOpcion.setText(opcion);
			rbOpcion.setUserData(opcion);
			
			vbFormulario.getChildren().add(rbOpcion);
		}
		
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old, Toggle newToggle) {
				if (toggleGroup.getSelectedToggle() != null) {
					String respuestaMarcada = toggleGroup.getSelectedToggle().getUserData().toString();
					((DinoVisorPantallaSecundaria) visor).setRespuestaMarcada(respuestaMarcada);
				}
			}
		});
		
		btComenzar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if(toggleGroup.getSelectedToggle() != null) {
					RespuestaEncuestaTicket respuesta = new RespuestaEncuestaTicket();
					respuesta.setOrden(pregunta.getOrden().longValue());
					String textoRespuesta = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
					respuesta.setRespuesta(textoRespuesta);
					encuestaActual.addRespuesta(respuesta);
				}
				
				indicePregunta++;
				refrescarDatosPantalla();
			}
		});
	}

	private void generarPreguntaAbierta(PreguntaEncuesta pregunta, VBox vbFormulario, Button btComenzar) {
		vbFormulario.setAlignment(Pos.TOP_CENTER);
		
		TextArea taRespuesta = new TextArea();
		taRespuesta.setMaxWidth(500.0);
		taRespuesta.setPrefHeight(100.0);

		vbFormulario.getChildren().add(taRespuesta);
		
		btComenzar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				String textoRespuesta = taRespuesta.getText();
				
				if(StringUtils.isNotBlank(textoRespuesta)) {
					RespuestaEncuestaTicket respuesta = new RespuestaEncuestaTicket();
					respuesta.setOrden(pregunta.getOrden().longValue());
					respuesta.setRespuesta(textoRespuesta);
					encuestaActual.addRespuesta(respuesta);
				}
				
				indicePregunta++;
				refrescarDatosPantalla();
			}
		});
	}

	private void terminarEncuesta() {
		indiceEncuesta++;
		
		if(indiceEncuesta == encuestas.size()) {
			cerrarPantalla();
		}
		else {
			refrescarDatosPantalla();
		}
	}

	private void pintarPantallaInicialEncuesta(Encuesta encuesta) {
		lbDescripcionEncuesta.setText(encuesta.getDescripcion());
		
		encuestaActual = new EncuestaTicket();
		encuestaActual.setIdEncuesta(encuesta.getIdEncuesta());
		encuestasHechas.add(encuestaActual);
		
		if(encuesta.getMostrarEnVisor().equals("S") && visor instanceof DinoVisorPantallaSecundaria) {
			((DinoVisorPantallaSecundaria) visor).modoEncuesta(encuesta, null);
		}
		else {
			visor.modoPago(visorConverter.convert((TicketVenta) ticket));
		}
		
		indicePregunta = 0;
		refrescarDatosPantalla();		
	}

	public void cerrarPantalla() {
		Iterator<EncuestaTicket> it = encuestasHechas.iterator();
		while(it.hasNext()) {
			EncuestaTicket encuesta = it.next();
			if(encuesta.getRespuestas().isEmpty()) {
				it.remove();
			}
		}
		
		getDatos().put(PARAM_RESPUESTAS, encuestasHechas);
		accionCancelar();
	}

}
