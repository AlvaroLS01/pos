package com.comerzzia.cardoso.pos.gui.ventas.devoluciones.articulos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesView;
import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.DevolucionArticulosController;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.LineaProvisionalDevolucion;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Primary
@Component
public class CardosoDevolucionArticulosController extends DevolucionArticulosController{

	private static final Logger log = Logger.getLogger(CardosoDevolucionArticulosController.class.getName());
	
    @Autowired
    private GiftCardService giftCardService;
	
    /**
     * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - LOTES
	 * 
	 * Incluimos los lotes en la parte de devoluciones para que los tenga en cuenta a la hora de facturar las lineas.
	 */
    
	@Override
	protected void facturarLineasADevolver(){
		log.debug("facturarLineasADevolver() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		for(LineaProvisionalDevolucion lineaProvisional : lineasProvisionales){
			LineaTicketAbstract linea = lineaProvisional.getLineaOriginal();
			linea.setCantidadADevolver(lineaProvisional.getCantADevolverOriginal());
			try{
				if(BigDecimalUtil.isMayor(lineaProvisional.getCantADevolver().add(linea.getCantidadADevolver()), linea.getCantidad())){
					lineaProvisional.setCantADevolver(linea.getCantidad().subtract(linea.getCantidadADevolver()));
				}
				LineaTicket lineaDevolucion = null;
				if(BigDecimalUtil.isMayorACero(lineaProvisional.getCantADevolver())){
					if(ticketManager.comprobarTarjetaRegalo(linea.getCodArticulo())){
						lineaDevolucion = ticketManager.nuevaLineaArticulo(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), lineaProvisional.getCantADevolver(), linea.getIdLinea());
						String numTarjeta = ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo();
						GiftCardBean tarjeta = giftCardService.getGiftCard(numTarjeta);

						if(tarjeta != null){
							if(numTarjeta.equals(ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo())){
								ticketManager.getTicket().getCabecera().agnadirTarjetaRegalo(tarjeta);
								ticketManager.getTicket().getCabecera().getTarjetaRegalo().setImporteRecarga(ticketManager.getTicket().getTotales().getTotalAPagar());
								ticketManager.setEsDevolucionTarjetaRegalo(true);
							}
							else{
								log.warn("facturarLineasADevolver() - " + I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
								throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
							}
						}
						else{
							log.error("facturarLineasADevolver() - " + I18N.getTexto("El número de tarjeta no es válido"));
							throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no es válido"));
						}
					}
					else{
						lineaDevolucion = ticketManager.nuevaLineaArticulo(linea.getCodArticulo(), linea.getDesglose1(), linea.getDesglose2(), lineaProvisional.getCantADevolver(), linea.getIdLinea());
					}
					lineaDevolucion.setAdmitePromociones(false);
					
					asignarNumerosSerie(lineaDevolucion);
					asignarLotes((CARDOSOLineaTicket) lineaDevolucion);
				}
			}
			catch(LineaTicketException e){
				log.error("facturarLineasADevolver() -Error facturando la línea a devolver: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(),
				        I18N.getTexto("La línea {0} no se ha podido insertar por el siguiente motivo: ", linea.getIdLinea()) + System.lineSeparator() + e.getMessage(), e);
			}
			catch(TarjetaRegaloException e){
				log.error("facturarLineasADevolver() - Error en el proceso de tarjeta regalo: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), e.getMessage(), e);
			}
			catch(Exception e){
				log.error("facturarLineasADevolver() - Ha habido un error al procesar la línea: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(this.getStage(), e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Realiza la acción de asignar un lote, pantalla "CardosoLotesView".
	 * @param linea
	 */
	public void asignarLotes(CARDOSOLineaTicket linea){
		if(((CardosoArticuloBean) ((LineaTicket) linea).getArticulo()).getAtributosAdicionalesArticulo() != null){
			log.info("asignarLotes() : GAP - PERSONALIZACIONES V3 - LOTES");
			if(((CardosoArticuloBean) ((LineaTicket) linea).getArticulo()).getAtributosAdicionalesArticulo().getLote()){
				if(lotesIguales((CARDOSOLineaTicket) ticketManager.getTicketOrigen().getLinea(((LineaTicket) linea).getLineaDocumentoOrigen()))){
					CardosoLote lote = new CardosoLote();
					lote.setId(((CARDOSOLineaTicket) ticketManager.getTicketOrigen().getLinea(((LineaTicket) linea).getLineaDocumentoOrigen())).getLotes().get(0).getId());
					lote.setCantidad(((LineaTicket) linea).getCantidad().abs());
					List<CardosoLote> lotes = new ArrayList<CardosoLote>();
					lotes.add(lote);
					((CARDOSOLineaTicket) linea).setLotes(lotes);
				}
				else{
					getDatos().put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
					getDatos().put(CardosoLotesController.PARAMETRO_LOTES_DOCUMENTO_ORIGEN,
					        ((CARDOSOLineaTicket) ticketManager.getTicketOrigen().getLinea(((LineaTicket) linea).getLineaDocumentoOrigen())).getLotes());
					getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
					linea = (CARDOSOLineaTicket) getDatos().get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
				}
			}
		}
	}

	/**
	 * Realiza comprobaciones para determinar si un lote es igual a otro.
	 * @param linea
	 * @return Boolean
	 */
	public Boolean lotesIguales(CARDOSOLineaTicket linea){
		log.debug("lotesIguales() : GAP - PERSONALIZACIONES V3 - LOTES");
		
		if(!((CARDOSOLineaTicket)linea).getLotes().isEmpty()){
			String sLoteAnterior = ((CARDOSOLineaTicket)linea).getLotes().get(0).getId();
			for(CardosoLote lote : ((CARDOSOLineaTicket)linea).getLotes()) {
				if(!sLoteAnterior.equals(lote.getId())){
				   return false;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}
	
}
