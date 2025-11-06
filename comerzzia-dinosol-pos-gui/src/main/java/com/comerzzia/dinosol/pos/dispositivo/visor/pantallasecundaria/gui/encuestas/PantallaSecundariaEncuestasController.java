package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.encuestas;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.persistence.encuestas.Encuesta;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuesta;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Component
public class PantallaSecundariaEncuestasController extends Controller {

	@FXML
	private Label lbDescripcionEncuesta;

	@FXML
	private VBox vbPregunta;
	
	@FXML
	private FlowPane fpRespuestas;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
	}

	public void refrescarDatosPantalla() {
		Encuesta encuesta = DinoVisorPantallaSecundaria.getEncuesta();
		PreguntaEncuesta pregunta = DinoVisorPantallaSecundaria.getPreguntaEncuesta();
		
		lbDescripcionEncuesta.setText(encuesta.getDescripcion());

		vbPregunta.getChildren().clear();
		vbPregunta.setSpacing(15.0);
		FlowPane fpRespuestas = new FlowPane();
		fpRespuestas.setAlignment(Pos.CENTER);
		VBox.setVgrow(fpRespuestas, Priority.ALWAYS);
		
		if(pregunta != null) {
			Label lbMigas = new Label();
			lbMigas.setText(I18N.getTexto("Pregunta {0} de {1}", pregunta.getOrden().intValue()+1, encuesta.getPreguntas().size()));
			lbMigas.setStyle("-fx-font-size:20px");
			vbPregunta.getChildren().add(lbMigas);
			
			Label lbTextoPregunta = new Label(pregunta.getTexto());
			lbTextoPregunta.getStyleClass().add("textoResaltado");
			lbTextoPregunta.setStyle("-fx-font-size:30px;-fx-text-fill: #006342");
			lbTextoPregunta.setWrapText(true);
			VBox.setMargin(lbTextoPregunta, new Insets(0, 20.0, 0, 20.0));
			VBox.setVgrow(lbTextoPregunta, Priority.ALWAYS);
			vbPregunta.getChildren().add(lbTextoPregunta);
			
			String tipoPregunta = pregunta.getTipo();
			
			if (tipoPregunta.equals("Opciones")) {
				pintarOpciones(pregunta, fpRespuestas);
			}
			else if(tipoPregunta.equals("Satisfacción")) {
				pintarSatisfaccion(pregunta, fpRespuestas);
			}
			else if(tipoPregunta.equals("Países")) {
				pintarPaises(pregunta, fpRespuestas);
			}
		}
		else {
			Label lbInfoCastellano = new Label("· Tus respuestas nos ayudan a mejorar");
			lbInfoCastellano.setStyle("-fx-font-size: 40px;-fx-text-fill: #006342;");
			vbPregunta.getChildren().add(lbInfoCastellano);
			Label lbInfoIngles = new Label("· Your answers help us to improve");
			lbInfoIngles.setStyle("-fx-font-size: 40px;-fx-text-fill: #006342;");
			vbPregunta.getChildren().add(lbInfoIngles);
			Label lbInfoAleman = new Label("· Ihre Antworten helfen us, uns zu verbessern");
			lbInfoAleman.setStyle("-fx-font-size: 40px;-fx-text-fill: #006342;");
			vbPregunta.getChildren().add(lbInfoAleman);
		}
	}

	private void pintarPaises(PreguntaEncuesta pregunta, FlowPane fpRespuestas) {
		String[] opciones = {"Deutschland", "Éireann", "España", "France", "Italia", "Netherlands", "Sverige", "Norway", "United Kingdom", "Otros/Others/Andere"};
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			VBox vbox = new VBox(5.0);
			vbox.setAlignment(Pos.CENTER);
			
			Image image = POSApplication.getInstance().createImage("iconos/banderas/" + opcion.replace("/", "_") + ".png");
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(40.0);
			imageView.setPreserveRatio(true);
			vbox.getChildren().add(imageView);
			
			Label label = new Label(opcion);
			label.setWrapText(true);
			label.setStyle("-fx-font-size:30px");
			vbox.getChildren().add(label);
			
			vbox.setUserData(opcion);

			FlowPane.setMargin(vbox, new Insets(30.0, 30.0, 30.0, 30.0));
			fpRespuestas.getChildren().add(vbox);
		}
		
		vbPregunta.getChildren().add(fpRespuestas);
	}

	private void pintarSatisfaccion(PreguntaEncuesta pregunta, FlowPane fpRespuestas) {
		String[] opciones = {"1", "2", "3", "4", "5"};
		String[] colores = {"#ff2442", "#ff7a28", "#ffc400", "#8ac73e", "#0cb853"};
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			VBox vbox = new VBox(5.0);
			vbox.setAlignment(Pos.CENTER);
			
			Image image = POSApplication.getInstance().createImage("iconos/satisfaccion-" + opcion + ".png");
			ImageView imageView = new ImageView(image);
			vbox.getChildren().add(imageView);
			
			Label label = new Label(opcion);
			label.setStyle("-fx-text-fill: " + colores[i]);
			vbox.getChildren().add(label);
			
			vbox.setUserData(opcion);

			FlowPane.setMargin(vbox, new Insets(30.0, 30.0, 30.0, 30.0));
			fpRespuestas.getChildren().add(vbox);
		}
		
		vbPregunta.getChildren().add(fpRespuestas);
	}

	private void pintarOpciones(PreguntaEncuesta pregunta, FlowPane fpRespuestas) {
		String opcionesString = pregunta.getOpciones();
		
		String[] opciones = opcionesString.split("\\r?\\n");
		
		for(int i = 0 ; i < opciones.length ; i++) {
			String opcion = opciones[i];
			
			Label lbOpcion = new Label(opcion);
			lbOpcion.setStyle("-fx-font-size:20px");
			FlowPane.setMargin(lbOpcion, new Insets(30.0, 30.0, 30.0, 30.0));
			lbOpcion.setUserData(opcion);
			
			fpRespuestas.getChildren().add(lbOpcion);
		}
		vbPregunta.getChildren().add(fpRespuestas);
	}

	public void marcarOpcion(String respuestaMarcada) {
		if(StringUtils.isNotBlank(respuestaMarcada)) {
			FlowPane panelPreguntas = (FlowPane) vbPregunta.getChildren().get(2);
			
			for(Node node : panelPreguntas.getChildren()) {
				String opcion = (String) node.getUserData();
				if(respuestaMarcada.equals(opcion)) {
					node.getStyleClass().add("opcion-marcada");
				}
				else {
					node.getStyleClass().remove("opcion-marcada");
				}
			}
		}
	}

}
