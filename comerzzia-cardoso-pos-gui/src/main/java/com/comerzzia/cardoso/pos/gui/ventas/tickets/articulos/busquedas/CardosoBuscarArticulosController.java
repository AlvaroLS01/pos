package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.busquedas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultastock.ConsultaStockController;
import com.comerzzia.cardoso.pos.services.articulos.CardosoArticulosService;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

@Component
@Primary
public class CardosoBuscarArticulosController extends BuscarArticulosController{

	private static final Logger log = Logger.getLogger(CardosoBuscarArticulosController.class.getName());

	@Autowired
	private CardosoArticulosService articulosService;

	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
	 * 
	 * Pedimos autorización para poder negar o eliminar una linea y para cancelar la venta.
	 */
	
	@Override
	public void initializeForm() throws InitializeGuiException{
		log.debug("initializeForm() : GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK");
		
		super.initializeForm();
		
		if(getDatos().containsKey(ConsultaStockController.ORIGEN_CONSULTA_STOCK)){
			mostrarBoton(btCancelar);
		}
	}
	
	/**
	 * ########################################################################################
	 * GAP - DESCUENTO TARIFA
	 * 
	 * Cambiamos el proceso para calcular el descuento a partir del descuento de tarifa.
	 */
	
	@Override
	public void initializeComponents(){
		log.debug("initializeComponents() : GAP - DESCUENTO TARIFA");
		
		super.initializeComponents();

		tbArticulos.setRowFactory(new Callback<TableView<LineaResultadoBusqGui>, TableRow<LineaResultadoBusqGui>>(){
			@Override
			public TableRow<LineaResultadoBusqGui> call(TableView<LineaResultadoBusqGui> p){
				final TableRow<LineaResultadoBusqGui> row = new TableRow<LineaResultadoBusqGui>(){
					@Override
					protected void updateItem(LineaResultadoBusqGui linea, boolean empty){
						super.updateItem(linea, empty);
						getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
						if(linea != null && (((CardosoLineaResultadoBusqGui) linea).getPromocionado() 
								|| BigDecimalUtil.isMayorACero(((CardosoLineaResultadoBusqGui) linea).getDescuentoTarifa()))){
							getStyleClass().add("cell-renderer-lineaDocAjeno");
						}
					}
				};
				return row;
			}
		});
	}

	@Override
	public void accionBuscar(){
		log.debug("accionBuscar() : GAP - DESCUENTO TARIFA");

		lineas.clear();
		tfDetalleCodArticulo.setText("");
		tfDetalleDescripcion.setText("");
		tfDetallePrecio.setText("");
		tfDetalleDesglose1.setText("");
		tfDetalleDesglose2.setText("");

		frBusquedaArt.setCodArticulo(tfCodigoArt.getText());
		frBusquedaArt.setDescripcion(tfDescripcion.getText());

		if(validarFormularioBusqueda()){
			ArticulosParamBuscar parametrosArticulo = new ArticulosParamBuscar();
			parametrosArticulo.setCodigo(tfCodigoArt.getText());
			parametrosArticulo.setDescripcion(tfDescripcion.getText());
			parametrosArticulo.setCliente(clienteBusqueda);
			parametrosArticulo.setCodTarifa(codTarifaBusqueda);

			CardosoBuscarArticulosTask buscarArticulosTask = new CardosoBuscarArticulosTask(parametrosArticulo);
			buscarArticulosTask.start();
		}
	}

	protected class CardosoBuscarArticulosTask extends BackgroundTask<List<ArticuloBuscarBean>>{

		private final ArticulosParamBuscar parametrosArticulo;

		public CardosoBuscarArticulosTask(ArticulosParamBuscar parametrosArticulo){
			this.parametrosArticulo = parametrosArticulo;
		}

		@Override
		protected List<ArticuloBuscarBean> call() throws Exception{
			return articulosService.buscarArticulos(parametrosArticulo);
		}

		@Override
		protected void succeeded(){
			List<ArticuloBuscarBean> articulosF = getValue();
			List<LineaResultadoBusqGui> lineasRes = new ArrayList<>();
			if(articulosF.isEmpty()){
				tbArticulos.setPlaceholder(new Label(I18N.getTexto("No se han encontrado resultados")));
			}
			for(ArticuloBuscarBean articulo : articulosF){
				lineasRes.add(new CardosoLineaResultadoBusqGui(articulo));
			}
			lineas.addAll(lineasRes);
			tbArticulos.getSelectionModel().select(0);
			tbArticulos.getFocusModel().focus(0);
			super.succeeded();
		}

		@Override
		protected void failed(){
			super.failed();
			log.error("accionBuscar() - Error consultando artículos");
			VentanaDialogoComponent.crearVentanaError(getCMZException().getMessageI18N(), getStage());
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void evaluarPromocion(String sCodart, String sDesglose1, String sDesglose2){
		log.debug("evaluarPromocion() : GAP - DESCUENTO TARIFA");
		
		super.evaluarPromocion(sCodart, sDesglose1, sDesglose2);
		
		try{
			TicketVenta ticket = (TicketVenta) ticketManager.getTicket();
			CARDOSOLineaTicket linea = (CARDOSOLineaTicket) ticket.getLineas().get(0);
			if(linea.getDescuentoTarifa() != null && BigDecimalUtil.isMayorACero(linea.getDescuentoTarifa())){
				FlowPane flowPane = constuirFlowPanePromocion();
				Label lbDescripcionPromocion = new Label(I18N.getTexto("El precio lleva incluido un descuento del ") + linea.getDescuentoTarifaAsString() + "%");
				lbDescripcionPromocion.getStyleClass().add("textoResaltado");
				lbDescripcionPromocion.setAlignment(Pos.CENTER_RIGHT);
				flowPane.getChildren().add(lbDescripcionPromocion);
				panelPromociones.getChildren().add(flowPane);
			}
		}
		catch(Exception e){
			log.error("evaluarPromocion() - Ha habido un error al intentar pintar el descuento de tarifa : " + e.getMessage(), e);
		}
	}

}
