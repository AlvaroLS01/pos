package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.promociones.opciones;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionPromocionesDto;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionesPromocionService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

@Component
public class PantallaSecundariaOpcionesPromocionController extends Controller {
	
	private Logger log = Logger.getLogger(PantallaSecundariaOpcionesPromocionController.class);
	
	@FXML
	private Label lbTitulo;

	@FXML
	private HBox hbOpciones;
	
	@Autowired
	private OpcionesPromocionService opcionesPromocionService;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
	}

	public void refrescarDatosPantalla() {
		Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones = DinoVisorPantallaSecundaria.getResultadoOpcionesPromociones();
		pintarOpciones(resultadoOpciones);
	}
	
	private void pintarOpciones(Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones) {
		lbTitulo.setText(opcionesPromocionService.getTituloVisor());
		
		hbOpciones.getChildren().clear();
		
		try {			
			int i = 0;
			for(OpcionPromocionesDto opcion : resultadoOpciones.keySet()) {
				DocumentoPromocionable ticket = resultadoOpciones.get(opcion);
				VBox vbOpcion = crearVboxOpcion(i);
				
				addTituloOpcion(opcion, vbOpcion);
				
				BigDecimal ahorroTotal = addPromocionesOpcion(opcion, vbOpcion, ticket);
				
				addAhorroTotalLabel(vbOpcion, ahorroTotal);
				
				if(BigDecimalUtil.isMayorACero(ahorroTotal)) {
					addOpcion(vbOpcion);
				}
				
				i++;
			}
		}
		catch (Exception e) {
			log.error("copiarTicket() - No se ha podido realizar la copia del ticket: " + e.getMessage(), e);
		}
	}

	private void addOpcion(VBox vbOpcion) {
		HBox.setHgrow(vbOpcion, Priority.ALWAYS);
		hbOpciones.getChildren().add(vbOpcion);
	}

	private VBox crearVboxOpcion(int i) {
		VBox vbOpcion = new VBox(10.0);
		vbOpcion.setAlignment(Pos.CENTER);
		vbOpcion.setPadding(new Insets(2.0));
		if(i%2 == 0) {
			vbOpcion.setStyle("-fx-background-color: #e1ede9");
		}
		else {					
			vbOpcion.setStyle("-fx-background-color: #baccc6");
		}
		return vbOpcion;
	}

	private void addTituloOpcion(OpcionPromocionesDto opcion, VBox vbOpcion) {
		Label labelTitulo = new Label(opcion.getTitulo());
		labelTitulo.getStyleClass().add("lb-destacado");
		labelTitulo.getStyleClass().add("texto-grande");
		labelTitulo.setMinHeight(100.0);
		labelTitulo.setTextAlignment(TextAlignment.CENTER);
		labelTitulo.setAlignment(Pos.CENTER);
		vbOpcion.getChildren().add(labelTitulo);
	}

	private BigDecimal addPromocionesOpcion(OpcionPromocionesDto opcion, VBox vbOpcion, DocumentoPromocionable ticket) {
		VBox vbPromociones = new VBox(15.0);
		vbPromociones.setMinHeight(150.0);
		
		BigDecimal ahorroTotal = BigDecimal.ZERO;
		for(PromocionTicket promocion : ticket.getPromociones()) {
			List<String> promocionesOpcion = opcion.getPromociones();
			String idPromocionSap = promocion.getPromocion().getExtension(DinoCuponesService.ID_PROMOCION_SAP);
			BigDecimal ahorro = promocion.getImporteTotalAhorro();
			
			if(promocionesOpcion.contains(idPromocionSap) && BigDecimalUtil.isMayorACero(ahorro)) {
				String tituloPromocion = promocion.getPromocion().getDescripcion();
				Label labelTituloPromocion = new Label(tituloPromocion);
				labelTituloPromocion.setAlignment(Pos.CENTER);
				labelTituloPromocion.setMinWidth(350.0);
				labelTituloPromocion.setMaxWidth(350.0);
				labelTituloPromocion.getStyleClass().add("texto-mediano");
				
				String importeAhorro = promocion.getImporteTotalAhorroAsString() + " €";
				Label labelImporteAhorro = new Label(importeAhorro);
				labelImporteAhorro.setAlignment(Pos.CENTER_RIGHT);
				labelImporteAhorro.setMinWidth(80.0);
				labelImporteAhorro.setMaxWidth(80.0);
				labelImporteAhorro.getStyleClass().add("texto-mediano");
				
				HBox hbPromocion = new HBox(labelTituloPromocion, labelImporteAhorro);
				hbPromocion.setAlignment(Pos.CENTER);
				hbPromocion.setSpacing(10.0);
				
				vbPromociones.getChildren().add(hbPromocion);
				
				ahorroTotal = ahorroTotal.add(ahorro);					
			}			
		}
		
		vbOpcion.getChildren().add(vbPromociones);
		return ahorroTotal;
	}

	private void addAhorroTotalLabel(VBox vbOpcion, BigDecimal ahorroTotal) {		
		String importeAhorroTotal = FormatUtil.getInstance().formateaImporte(ahorroTotal) + " €";
		Label labelImporteAhorroTotal = new Label(I18N.getTexto("AHORRO TOTAL: ") + importeAhorroTotal);
		labelImporteAhorroTotal.setAlignment(Pos.CENTER);
		labelImporteAhorroTotal.getStyleClass().add("texto-negrita");
		labelImporteAhorroTotal.getStyleClass().add("texto-mediano");
		labelImporteAhorroTotal.setStyle("-fx-border-color: #85b037; -fx-border-width: 3px; -fx-font-size: 30px;");
		labelImporteAhorroTotal.setMinWidth(450.0);
		labelImporteAhorroTotal.setPrefWidth(450.0);
		labelImporteAhorroTotal.setMinHeight(50.0);
		
		HBox hbAhorroTotal = new HBox(labelImporteAhorroTotal);
		hbAhorroTotal.setSpacing(10.0);
		hbAhorroTotal.setAlignment(Pos.CENTER);
		
		vbOpcion.getChildren().add(hbAhorroTotal);
	}

}
