package com.comerzzia.pos.gui.ventas.profesional.venta;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.ventas.identificada.venta.VentaIdentificadaController;
import com.comerzzia.pos.gui.ventas.profesional.articulos.busqueda.BuscarArticulosProfesionalView;
import com.comerzzia.pos.gui.ventas.profesional.pagos.PagosProfesionalView;
import com.comerzzia.pos.gui.ventas.tickets.VentaProfesionalManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class VentaProfesionalController extends VentaIdentificadaController {

	@FXML
	protected TableColumn<LineaTicketProfesionalGui, BigDecimal> tcPrecio, tcImporte, tcPrecioIva, tcImporteIva;

	@FXML
	protected Label lbBase, lbIva, lbRecargo;

	@Override
	public void initializeManager() throws SesionInitException {
		ticketManager = SpringContext.getBean(VentaProfesionalManager.class);
		ticketManager.init();
	}

	@SuppressWarnings("unchecked")
    @Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		tcPrecio.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcPrecio", 4, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporte.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcImporte", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcPrecioIva.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcPrecioIva", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImporteIva.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcImporteIva", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcPrecio.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketProfesionalGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketProfesionalGui, BigDecimal> cdf) {
				return cdf.getValue().getPrecioProperty();
			}
		});

		tcImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketProfesionalGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketProfesionalGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteProperty();
			}
		});

		tcPrecioIva.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketProfesionalGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketProfesionalGui, BigDecimal> cdf) {
				return cdf.getValue().getPrecioConIvaProperty();
			}
		});

		tcImporteIva.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketProfesionalGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketProfesionalGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteConIvaProperty();
			}
		});
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		lbTitulo.setText(I18N.getTexto("Venta Profesional"));

		if(cliente != null) {
			DatosFactura datosFactura = new DatosFactura();
			datosFactura.setCif(cliente.getCif());
			datosFactura.setCp(cliente.getCp());
			datosFactura.setDomicilio(cliente.getDomicilio());
			datosFactura.setProvincia(cliente.getProvincia());
			datosFactura.setTelefono(cliente.getTelefono1());
			datosFactura.setNombre(cliente.getDesCliente());
			datosFactura.setPoblacion(cliente.getPoblacion());
			datosFactura.setLocalidad(cliente.getLocalidad());
			datosFactura.setPais(cliente.getCodpais());
			datosFactura.setTipoIdentificacion(cliente.getTipoIdentificacion());
	
			((TicketVentaProfesional) ticketManager.getTicket()).setDatosFacturacion(datosFactura);
		}
	}

	@Override
	public void refrescarDatosPantalla() {
		super.refrescarDatosPantalla();

		lbBase.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getBase()));
		lbIva.setText(FormatUtil.getInstance().formateaImporte(((TotalesTicketProfesional) ticketManager.getTicket().getTotales()).getIvaTotal()));
		lbRecargo.setText(FormatUtil.getInstance().formateaImporte(((TotalesTicketProfesional) ticketManager.getTicket().getTotales()).getRecargoTotal()));
	}

	@Override
	protected LineaTicketGui createLineaGui(CuponAplicadoTicket cupon) {
		return new LineaTicketProfesionalGui(cupon);
	}

	@Override
	protected LineaTicketGui createLineaGui(LineaTicket lineaTicket) {
		return new LineaTicketProfesionalGui(lineaTicket);
	}

	@Override
	protected void abrirVentanaBusquedaArticulos() {
		getApplication().getMainView().showModal(BuscarArticulosProfesionalView.class, getDatos());
	}
	
	@Override
	protected void abrirVentanaPagos() {
	    getApplication().getMainView().showModal(PagosProfesionalView.class, getDatos());
	}
	
	@Override
	protected void cambiarClienteVenta(ClienteBean cliente) {
		((TicketVentaAbono) ticketManager.getTicket()).setCliente(cliente);
	}

	@Override
	protected void obtenerCantidadTotal() {
	}
}
