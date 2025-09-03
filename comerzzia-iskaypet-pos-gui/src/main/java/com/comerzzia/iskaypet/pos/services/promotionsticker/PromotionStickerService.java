package com.comerzzia.iskaypet.pos.services.promotionsticker;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalSticker;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalStickerExample;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalStickerMapper;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalStickerXML;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class PromotionStickerService{

	private static final Logger log = Logger.getLogger(PromotionStickerService.class);
	
	@Autowired
	protected Sesion sesion;
	@Autowired
	protected ArticulosService articulosService;
	@Autowired
	protected PromotionalStickerMapper promotionalStickerMapper;
	
	/**
	 * Realiza el proceso de uso de una pegatina de marca. Poniendo el campo 'available' a N para que no se pueda usar mas.
	 * Devuelve el objeto de pegatina de marca para insertarlo en la linea.
	 * @param barCodePromotionSticker
	 * @return PromotionalStickerXML
	 * @throws Exception
	 */
	public PromotionalStickerXML processPromotionStickerArticleInsert(String barCodePromotionSticker) throws Exception{
		PromotionalStickerXML pegatinaPromocional = null;
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			// PASO01 : Comprobamos si el artículo indicado es un artículo de pegatina promocional.
			PromotionalStickerExample example = new PromotionalStickerExample();
			example.or().andActivityIdEqualTo(sesion.getAplicacion().getUidActividad())
						.andBarcodeEqualTo(barCodePromotionSticker)
						.andAvailableEqualTo("S");
						
			List<PromotionalSticker> listPromotionalSticker = promotionalStickerMapper.selectByExample(example);
			PromotionalSticker isPromotionStickerArticle = null;
			if(listPromotionalSticker != null && !listPromotionalSticker.isEmpty()){
				isPromotionStickerArticle = listPromotionalSticker.get(0);
			}
			
			// PASO02 : En caso de ser pegatina promocional, debemos realizar una acción. Usado.
			if(isPromotionStickerArticle != null){
				
				log.debug("processPromotionStickerArticleInsert() - El artículo indicado es una pegatina promocional '" + barCodePromotionSticker + "', lo marcamos como usado.");
				
				isPromotionStickerArticle.setAvailable("N");
				
				int result = promotionalStickerMapper.updateByPrimaryKey(isPromotionStickerArticle);
				if(result == 0){
					String msgError = I18N.getTexto("No se ha podido actualizar el registro de uso de la pegatina especial '{0}'.",barCodePromotionSticker);
					throw new Exception(msgError);
				}
				else{
					pegatinaPromocional = new PromotionalStickerXML();
					pegatinaPromocional.setEan(isPromotionStickerArticle.getBarcode());
					pegatinaPromocional.setDiscount(isPromotionStickerArticle.getDiscount());
					pegatinaPromocional.setMotived(isPromotionStickerArticle.getReasonCode());
					pegatinaPromocional.setCodArticle(isPromotionStickerArticle.getItemCode());
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al comprobar/actualizar los datos de la pegatina promocional '{0}' : ", barCodePromotionSticker) + e.getMessage();
			log.error("processPromotionStickerArticleInsert() - " + msgError, e);
			throw new Exception(msgError, e);
		}
		return pegatinaPromocional;
	}
	
	/**
	 * Realiza el proceso de marcar como no usado una pegatina de marca. Poniendo el campo 'available' a S para que se pueda usar mas.
	 * @param barCodePromotionSticker
	 * @throws Exception
	 */
	public void processPromotionStickerArticleDelete(String barCodePromotionSticker) throws Exception{
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			// PASO01 : Comprobamos si el artículo indicado es un artículo de pegatina promocional.
			PromotionalStickerExample example = new PromotionalStickerExample();
			example.or().andActivityIdEqualTo(sesion.getAplicacion().getUidActividad())
						.andBarcodeEqualTo(barCodePromotionSticker)
						.andAvailableEqualTo("N");
						
			List<PromotionalSticker> listPromotionalSticker = promotionalStickerMapper.selectByExample(example);
			PromotionalSticker isPromotionStickerArticle = null;
			if(listPromotionalSticker != null && !listPromotionalSticker.isEmpty()){
				isPromotionStickerArticle = listPromotionalSticker.get(0);
			}
			
			// PASO02 : En caso de ser pegatina promocional, debemos realizar una acción. No usado.
			if(isPromotionStickerArticle != null){
				
				log.debug("processPromotionStickerArticleDelete() - El artículo indicado es una pegatina promocional '" + barCodePromotionSticker + "', lo marcamos como no usado.");
				
				isPromotionStickerArticle.setAvailable("S");
				
				int result = promotionalStickerMapper.updateByPrimaryKey(isPromotionStickerArticle);
				if(result == 0){
					String msgError = I18N.getTexto("No se ha podido actualizar el registro de uso de la pegatina especial '{0}'.", barCodePromotionSticker);
					throw new Exception(msgError);
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al comprobar/actualizar los datos de la pegatina promocional '{0}' : ", barCodePromotionSticker) + e.getMessage();
			log.error("processPromotionStickerArticleDelete() - " + msgError, e);
			throw new Exception(msgError, e);
		}
	}
	
	public void applyDiscountPromotionalSticker(IskaypetLineaTicket line, Long idTratamientoImpuestos) throws Exception{
		try{
			// PASO01 : En caso de tener un descuento especial, debemos aplicarlo.
			if(line.getPegatinaPromocional() != null){
				/*
				// PASO02 : Debemos aplicar el descuento indicado en el objeto a la linea.
				BigDecimal totalPriceWithDiscount = BigDecimalUtil.menosPorcentajeR4(line.getPrecioSinDto(), line.getPegatinaPromocional().getDiscount());
				
				SesionImpuestos sesionImpuestos = sesion.getImpuestos();
				BigDecimal priceArticlePointsWithoutTax = sesionImpuestos.getPrecioSinImpuestos(line.getCodImpuesto(), BigDecimalUtil.redondear(totalPriceWithDiscount), idTratamientoImpuestos);
				line.setPrecioSinDto(priceArticlePointsWithoutTax);
				line.setPrecioTotalSinDto(priceArticlePointsWithoutTax);
				
				// PASO03 : Debemos quitar los datos de promociones de la linea.
				line.setPromociones(new ArrayList<PromocionLineaTicket>());
				line.setTextosPromociones(new ArrayList<TextoPromocion>());
				line.setDescuento(BigDecimal.ZERO);
				*/
				line.setAdmitePromociones(false);
				line.setDescuentoManual(line.getPegatinaPromocional().getDiscount());
			}
			
			// PASO5 : Debemos recalcular despues de realizar el cambio de precio.
			line.recalcularImporteFinal();
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al aplicar el precio de descuento especial a la linea : ")+ e.getMessage();
			log.error("applyDiscountPromotionalSticker() - " + msgError, e);
			throw new Exception(msgError, e);
		}
	}
	
	public void deletePromotionalSticker(TicketManager ticketManager) throws Exception{
		try{
			if(ticketManager.getTicket().getLineas() != null && !ticketManager.getTicket().getLineas().isEmpty()){
				List<IskaypetLineaTicket> listLines = (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas();
				for(IskaypetLineaTicket line : listLines){
					
					if(line.getPegatinaPromocional() != null){
						
						PromotionalStickerExample example = new PromotionalStickerExample();
						example.or().andActivityIdEqualTo(sesion.getAplicacion().getUidActividad())
									.andBarcodeEqualTo(line.getPegatinaPromocional().getEan());
						promotionalStickerMapper.deleteByExample(example);
					}
				}
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("Error al aplicar el precio de descuento especial a la linea : " + e.getMessage());
			log.error("deletePromotionalSticker() - " + msgError, e);
			throw new Exception(msgError, e);
		}
	}
	
}
