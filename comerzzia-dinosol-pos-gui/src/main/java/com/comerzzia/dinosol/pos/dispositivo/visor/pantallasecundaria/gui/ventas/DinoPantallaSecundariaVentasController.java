package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.ventas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.DocumentoIVisor;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.LineaDocumentoIVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.ventas.PantallaSecundariaVentasController;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

@Primary
@Component
public class DinoPantallaSecundariaVentasController extends PantallaSecundariaVentasController {

	@FXML
	public Label lbNArticulos;
	@FXML
	private ImageView imgQr;

	protected static int MAX = 3;

	protected List<DinoVentaRow> ventasRowCache = new ArrayList<>(MAX);

	@Override
	public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
		DocumentoIVisor documento = DinoVisorPantallaSecundaria.getTicket();
		List<LineaDocumentoIVisor> lineasVenta = new ArrayList<LineaDocumentoIVisor>(documento.getLineas());
		Collections.reverse(lineasVenta);

		container.getChildren().clear();
		if (lineasVenta.size() > MAX) {
			lineasVenta = lineasVenta.subList(0, MAX);
		}

		updateRows(lineasVenta);

		lineasVenta = null;
		calcularTotales(documento.getLineas());

		lbTotal.setText(FormatUtil.getInstance().formateaImporteMoneda(documento.getTotal()));
		
		boolean existeFidelizado = existeFidelizado(documento);
		imgQr.setVisible(existeFidelizado);

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
		imgQr.setVisible(false);
	}

	@Override
	protected void updateRows(List<LineaDocumentoIVisor> lineasVenta) {
		container.getChildren().clear();

		if (ventasRowCache.isEmpty()) {
			for (int i = 0; i < MIN; i++) {
				DinoVentaRow row = new DinoVentaRow();
				row.init(getScene());
				ventasRowCache.add(row);
				row = null;
			}
		}

		container.getChildren().clear();
		for (int i = 0; i < lineasVenta.size(); i++) {
			ventasRowCache.get(i).setVentaRow((LineaDocumentoIVisor) lineasVenta.get(i), usaDescuentoEnLinea);
			container.getChildren().add(ventasRowCache.get(i));
		}
	}

	@Override
	protected void calcularTotales(List<LineaDocumentoIVisor> list) {
		List<String> listaArticulos = new ArrayList<>();
		BigDecimal totalUnidades = BigDecimal.ZERO;

		for (LineaDocumentoIVisor linea : list) {
			BigDecimal cantidad = linea.getCantidad();
			totalUnidades = totalUnidades.add(cantidad);
			listaArticulos.add(linea.getCodArticulo().concat(linea.getDesglose1()).concat(linea.getDesglose2()));
		}
		listaArticulos = null;
		DocumentoIVisor documento = DinoVisorPantallaSecundaria.getTicket();
		if (documento.getDelegate() instanceof DinoTicketVentaAbono) {
			lbNArticulos.setText(I18N.getTexto("Articulos") + ": " + ((DinoTicketVentaAbono) documento.getDelegate()).getCabecera().getCantidadArticulosAsString());
		}
		else {
			lbNArticulos.setText("");
		}
	}
	
	private boolean existeFidelizado(DocumentoIVisor documento) {
		Object ticket = documento.getDelegate();
		if (ticket instanceof DinoTicketVentaAbono && ((DinoTicketVentaAbono) ticket).getCabecera().getDatosFidelizado() != null) {
			return true;
		}
		return false;
	}
}
