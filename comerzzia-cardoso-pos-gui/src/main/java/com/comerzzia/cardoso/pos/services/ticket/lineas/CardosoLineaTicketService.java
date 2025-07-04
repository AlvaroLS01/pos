package com.comerzzia.cardoso.pos.services.ticket.lineas;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.persistence.tarifas.TarifaDetalleAnexaBean;
import com.comerzzia.cardoso.pos.services.tarifas.TarifaDetalleService;
import com.comerzzia.cardoso.pos.services.tarifas.exception.TarifaDetalleException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;

@SuppressWarnings({ "rawtypes", "deprecation" })
@Service
@Primary
public class CardosoLineaTicketService extends LineasTicketServices{

	private static final Logger log = Logger.getLogger(CardosoLineaTicketService.class.getName());

	@Autowired
	private Sesion sesion;
	@Autowired
	private TarifaDetalleService tarifaDetalleService;
	
	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - DESCUENTO TARIFA
	 * 
	 * Al generar la linea insertamos los datos del descuento de tarifa.
	 */
	@Override
	public LineaTicketAbstract createLineaArticulo(TicketVenta ticket, String codigo, String desglose1, String desglose2, BigDecimal cantidad, BigDecimal precio, LineaTicketAbstract lineaTicket,
	        boolean applyDUN14Factor) throws LineaTicketException, ArticuloNotFoundException{
		log.debug("createLineaArticulo() : GAP - DESCUENTO TARIFA");
		
		log.debug("createLineaArticulo() - Creando nueva línea de artículo...");
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			lineaTicket = super.createLineaArticulo(ticket, codigo, desglose1, desglose2, cantidad, precio, lineaTicket, true);

			CardosoArticuloBean articulo = (CardosoArticuloBean) lineaTicket.getArticulo();
			TarifaDetalleAnexaBean tarifa = (TarifaDetalleAnexaBean) tarifaDetalleService.getTarifaDetalleAnexa(sesion.getAplicacion().getUidActividad(), articulo.getCodArticulo(), lineaTicket.getTarifa());
			if(tarifa != null){
				((CARDOSOLineaTicket) lineaTicket).setPrecioVentaSinDtoTarifa(tarifa.getPrecioVentaSinDto());
				((CARDOSOLineaTicket) lineaTicket).setPrecioVentaTotalSinDtoTarifa(tarifa.getPrecioTotalSinDto());
				((CARDOSOLineaTicket) lineaTicket).setImporteDescuentoTarifa(tarifa.getPrecioTotalSinDto().subtract(lineaTicket.getTarifa().getPrecioTotal()));
				((CARDOSOLineaTicket) lineaTicket).setDescuentoTarifa(tarifa.getDescuentoTarifa());
			}
			else{
				((CARDOSOLineaTicket) lineaTicket).setDescuentoTarifa(BigDecimal.ZERO.setScale(2));
			}

			return lineaTicket;
		}
		catch(LineaTicketException ex){
			throw ex;
		}
		catch(ArticuloNotFoundException | TarifaDetalleException ex){
			throw new LineaTicketException(ex);
		}
		finally{
			sqlSession.close();
		}
	}

}
