package com.comerzzia.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class LineasTicketServices {
    
    protected static final Logger log = Logger.getLogger(LineasTicketServices.class.getName());
    
    @Autowired
    protected Sesion sesion;
    @Autowired
    protected Documentos documentos;
    
    @Autowired
    protected ArticulosService articulosService;
    
    @Autowired
    protected ArticulosTarifaService articulosTarifaService;
    
    /**
     * Permite crear una línea en el ticket de cualquier clase derivada de LineaTicketAbstract
     * @param ticket
     * @param codigo
     * @param desglose1
     * @param desglose2
     * @param cantidad
     * @param precio
     * @param lineaTicket
     * @return
     * @throws LineaTicketException
     * @throws ArticuloNotFoundException
     */
    public LineaTicketAbstract createLineaArticulo(TicketVenta ticket, String codigo, String desglose1, String desglose2, BigDecimal cantidad, BigDecimal precio, LineaTicketAbstract lineaTicket) throws LineaTicketException, ArticuloNotFoundException {
    	return createLineaArticulo(ticket, codigo, desglose1, desglose2, cantidad, precio, lineaTicket, true);
    }
    public LineaTicketAbstract createLineaArticulo(TicketVenta ticket, String codigo, String desglose1, String desglose2, BigDecimal cantidad, BigDecimal precio, LineaTicketAbstract lineaTicket, boolean applyDUN14Factor) throws LineaTicketException, ArticuloNotFoundException {
        log.debug("createLineaArticulo() - Creando nueva línea de artículo...");
        SqlSession sqlSession = new SqlSession();
        try {
            sqlSession.openSession(SessionFactory.openSession());
            
            ConsultaCodigoArticuloResponse codigoArticuloResponse = consultarLineaArticulo(codigo, desglose1, desglose2, documentos.isDocumentoAbono(ticket.getCabecera().getCodTipoDocumento()), sqlSession);
            
            ArticuloCodBarraBean codigoBarras = codigoArticuloResponse.getCodigoBarras();
            ArticuloBean articulo = codigoArticuloResponse.getArticulo();
            desglose1 = codigoArticuloResponse.getDesglose1();
            desglose2 = codigoArticuloResponse.getDesglose2();
            
            // Consultamos tarifa del artículo
            TarifaDetalleBean tarifa = null;
            TarifaArticuloDto tarifaArticuloDto = null;
			try {
				tarifaArticuloDto = articulosTarifaService.consultarArticuloTarifa(sqlSession, articulo.getCodArticulo(), ticket.getCliente(), desglose1, desglose2, new Date());
				if(tarifaArticuloDto != null) {
					tarifa = tarifaArticuloDto.getDetalle();
				}
				if(tarifa.getVersion() == null) { //Si el artículo en base de datos tiene tarifa a null
					tarifa.setVersion(-1l); //Ponemos versión a distinto de null para que no interfiera con el caso de artículo no tarificado
				}
			} catch (ArticuloTarifaNotFoundException e) {
				tarifa = new TarifaDetalleBean();
				tarifa.setFactorMarcaje(BigDecimal.ZERO);
				tarifa.setPrecioCosto(BigDecimal.ZERO);
				tarifa.setPrecioTotal(BigDecimal.ZERO);
				tarifa.setPrecioVenta(BigDecimal.ZERO);
				tarifa.setVersion(null); //Ponemos versión a null para diferenciar que no viene de BD
			}
            
			// Comprobamos si se trata de un código de barras con factor de conversión
			if (codigoBarras != null && codigoBarras.getFactorConversion() != null && BigDecimalUtil.isMayorACero(codigoBarras.getFactorConversion()) && applyDUN14Factor){
				cantidad = BigDecimalUtil.redondear(cantidad.multiply(codigoBarras.getFactorConversion()), 3);
			}
			
         // Construimos línea de ticket
            lineaTicket.setCabecera(ticket.getCabecera());
            lineaTicket.setEditable(true);
            
            if(precio != null && tarifa != null){
            	tarifa.setPrecioVenta(precio);
            	tarifa.setPrecioTotal(precio);
            	lineaTicket.setIvaIncluido(true);
            }   
            
            if(tarifaArticuloDto != null && tarifaArticuloDto.getCabecera() != null) {
            	boolean isPrecioConImpuestos = tarifaArticuloDto.getCabecera().getPrecioConImpuestos() != null && tarifaArticuloDto.getCabecera().getPrecioConImpuestos().equals("S");
            	lineaTicket.setIvaIncluido(isPrecioConImpuestos);
            }
            
            lineaTicket.setArticulo(articulo);
            lineaTicket.setDesglose1(desglose1);
            lineaTicket.setDesglose2(desglose2);
            lineaTicket.setCantidad(cantidad);
            lineaTicket.setTarifa(tarifa);
            lineaTicket.setTarifaOriginal(tarifa);
            lineaTicket.recalcularPreciosImportes();
            lineaTicket.setVendedor(sesion.getSesionUsuario().getUsuario());
            
            if(codigoArticuloResponse.hasCodigoBarras()) {
            	lineaTicket.setCodigoBarras(codigoArticuloResponse.getCodigoBarras().getCodigoBarras());
            }
            
            return lineaTicket;
        }
        catch (LineaTicketException ex) {
            throw ex;
        }
        catch (ArticuloTarifaServiceException | ArticulosServiceException ex) {
            throw new LineaTicketException(ex.getMessageI18N());
		}
        finally{
            sqlSession.close();
        }
    }

    public ConsultaCodigoArticuloResponse consultarLineaArticulo(String codigo, String desglose1, String desglose2, boolean noFiltrarActivo) throws ArticuloNotFoundException, ArticulosServiceException, LineaTicketException {
		SqlSession sqlSession = new SqlSession();
        try {
            sqlSession.openSession(SessionFactory.openSession());
            return consultarLineaArticulo(codigo, desglose1, desglose2, noFiltrarActivo, sqlSession);
    	} finally{
            sqlSession.close();
        }
    }
    
    public ConsultaCodigoArticuloResponse consultarLineaArticulo(String codigo, String desglose1, String desglose2, boolean noFiltrarActivo, SqlSession sqlSession) throws ArticuloNotFoundException, ArticulosServiceException, LineaTicketException {
    	ArticuloCodBarraBean codigoBarras = null;
        ArticuloBean articulo;
        try{
            if(desglose1 == null && desglose2 == null){
                codigoBarras = articulosService.consultarCodigoBarras(sqlSession, codigo);
                desglose1 = codigoBarras.getDesglose1();
                desglose2 = codigoBarras.getDesglose2();
                codigo = codigoBarras.getCodArticulo();
            }
        }catch(ArticuloNotFoundException ex){
        }
        
        // Si el ticket viene de una devolución no bloqueamos en la búsqueda los artículos inactivos
        articulo = articulosService.consultarArticulo(sqlSession, codigo, noFiltrarActivo);
        
        if (articulo.getDesglose1() && (desglose1 == null || desglose1.isEmpty() || desglose1.equals("*"))) {
            throw new LineaTicketException(I18N.getTexto("Para este artículo se deben especificar sus desgloses."));
        }
        if (articulo.getDesglose2() && (desglose2 == null || desglose2.isEmpty() || desglose2.equals("*"))) {
            throw new LineaTicketException(I18N.getTexto("Para este artículo se deben especificar sus desgloses."));
        }
        
        return new ConsultaCodigoArticuloResponse(codigoBarras, articulo, desglose1, desglose2);
    }
    
	public BigDecimal tratarSignoDocumento(BigDecimal valor, String codTipoDoc, boolean esDevolucion) {

		BigDecimal valorRetorno = valor;

		try {
			TipoDocumentoBean doc = sesion.getAplicacion().getDocumentos().getDocumento(codTipoDoc);

			if (doc.isSignoPositivo()) {
				valorRetorno = valorRetorno.abs();
			}
			else if (doc.isSignoNegativo()) {
				valorRetorno = valorRetorno.abs().negate();
			}
			else if (esDevolucion) {
				valorRetorno = valorRetorno.abs().negate();
			}
		}
		catch (DocumentoException ex) {
			log.error("No se pudo obtener el documento para establecer el signo del documentod", ex);
		}
		return valorRetorno;
	}
    
}
