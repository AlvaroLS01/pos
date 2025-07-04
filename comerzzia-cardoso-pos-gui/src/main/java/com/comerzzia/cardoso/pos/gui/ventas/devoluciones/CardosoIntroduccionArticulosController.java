package com.comerzzia.cardoso.pos.gui.ventas.devoluciones;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesView;
import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("unchecked")
@Primary
@Controller
public class CardosoIntroduccionArticulosController extends IntroduccionArticulosController{
	
	private static final Logger log = Logger.getLogger(CardosoIntroduccionArticulosController.class.getName());
	
	@Override
	protected boolean validarTicket(){
		if(((CARDOSOTicketManager)ticketManager).getDatosOrigenDevolucionSinOrigen() != null){
			// GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN 
			CARDOSOCabeceraTicket cabecera = (CARDOSOCabeceraTicket) ticketManager.getTicket().getCabecera();
			cabecera.setDatosOrigenFalsos(((CARDOSOTicketManager)ticketManager).getDatosOrigenDevolucionSinOrigen());
			return true;
		}
		else{
			// GAP - PERSONALIZACIONES V3 - LOTES
			// Debemos asignar los lotes al introducir un artículo.
			Boolean resultEstandar = super.validarTicket();
			if(resultEstandar){
				log.debug("validarTicket() : GAP - PERSONALIZACIONES V3 - LOTES");
				for(CARDOSOLineaTicket linea : (List<CARDOSOLineaTicket>) ticketManager.getTicket().getLineas()){
					CardosoArticuloBean articulo = (CardosoArticuloBean) linea.getArticulo();
					if(articulo.getAtributosAdicionalesArticulo() != null && articulo.getAtributosAdicionalesArticulo().getLote() && quedanLotesPorAsignar(linea)){
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Quedan lotes por asignar. Asígnelos antes de seguir."), getStage());
						asignarLotes(linea);
						resultEstandar = false;
					}
				}
			}
			return resultEstandar;
		}
	}
	
	/**
	 * Realiza la comprobación de que todos los lotes están asignados correctamente.
	 * @param linea
	 * @return Boolean
	 */
	public Boolean quedanLotesPorAsignar(CARDOSOLineaTicket linea){
		log.debug("quedanLotesPorAsignar() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		BigDecimal cantidadActual = BigDecimal.ZERO;
		if(linea.getLotes() != null && !linea.getLotes().isEmpty()){
			for(CardosoLote lote : linea.getLotes()){
				cantidadActual = cantidadActual.add(lote.getCantidad());
			}
		}
		return cantidadActual.doubleValue() < ((LineaTicket) linea).getCantidad().doubleValue();
	}
	
	/**
	 * Realiza la acción de asignar un lote, pantalla "CardosoLotesView".
	 * @param linea
	 */
	public void asignarLotes(CARDOSOLineaTicket linea){
		log.debug("asignarLotes() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(((CardosoArticuloBean) ((LineaTicket) linea).getArticulo()).getAtributosAdicionalesArticulo().getLote()){
			getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
			getDatos().put(CardosoLotesController.PARAMETRO_LOTES_DOCUMENTO_ORIGEN,
			        ((CARDOSOLineaTicket) ticketManager.getTicketOrigen().getLinea(((LineaTicket) linea).getLineaDocumentoOrigen())).getLotes());
			getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
			linea = (CARDOSOLineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
		}
	}

	/**
	 * ########################################################################################
	 * GAP - CAJERO AUXILIAR
	 * 
	 * Pedimos autorización para poder eliminar un registro o cancelar una devolución.
	 */
	
	@Override
	protected void accionTablaEliminarRegistro(){
		if(realizarAutorizacion()) {
			super.accionTablaEliminarRegistro();
		}
	}

	@Override
	public void accionCancelarDevolucion(){		
		if(realizarAutorizacion()) {
			super.accionCancelarDevolucion();
		}
	}
	
	/**
	 * Realiza las mismas acciones que el método "accionCancelarDevolucion", pero realizando
	 * la autorización de nuestra personalización.
	 */
	public boolean realizarAutorizacion(){
		log.debug("realizarAutorizacion() : GAP - CAJERO AUXILIAR");
		
		if(((CARDOSOTicketManager) ticketManager).necesitaAutorizacion()){
			String msgConfirmacion = I18N.getTexto("Se anulará la devolución, ¿seguro que desea continuar?");
			if(VentanaDialogoComponent.crearVentanaConfirmacion(msgConfirmacion, this.getStage())){
				getDatos().put(AutorizarAccionesController.PANTALLA_ANULACION, true);
				getApplication().getMainView().showModalCentered(AutorizarAccionesView.class, getDatos(), getStage());
				if(getDatos().containsKey(AutorizarAccionesController.sUsuario)){
					return true;
				}
			}
		}
		else {
			return true;
		}
		
		return false;
	}
	
}
