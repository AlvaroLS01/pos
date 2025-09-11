package com.comerzzia.iskaypet.pos.gui.canjeospuntos;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.ArticlePointsBean;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.ArticlePointsBeanExample;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.ArticlePointsBeanMapper;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions.ArticlePointsConditions;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions.ArticlePointsConditionsExample;
import com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions.ArticlePointsConditionsMapper;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos.ArticlesPointsXMLBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
@Service
public class ArticlesPointsManager {

	private static final Logger log = Logger.getLogger(ArticlesPointsManager.class);

	public static final String COD_COLOR_LINE = "-fx-background-color: #A6CE8F;";
	public static final String COD_COLOR_LINE_SELECTED = "-fx-background-color: #86947E;";

	public static final String ACCION_CANJEAR_PUNTOS_ART = "ACCION_CANJEAR_PUNTOS_ART";
	public static final String ICON_ACCION_CANJEAR_PUNTOS_ART = "iconos/canjear-puntos.png";

	public static final String ACTION_INSERT = "INSERT";
	public static final String ACTION_UPDATE = "UPDATE";
	public static final String ACTION_DELETE = "DELETE";

	@Autowired
	protected Sesion sesion;
	@Autowired
	protected ArticlePointsBeanMapper articlePointsMapper;
	@Autowired
	protected ArticlePointsConditionsMapper articlePointsConditionsMapper;
	private MetadataNamingStrategy metadataNamingStrategy;


	/* ########################################################################################################################### */
	/* ###################################################### VALIDACIONES ####################################################### */
	/* ########################################################################################################################### */

	public boolean isTicketWithArticlePointsRedeemed(List<LineaTicket> lineas) {
		if (lineas != null && !lineas.isEmpty()) {
			for (LineaTicket lineaTicket : lineas) {
				if (lineaTicket instanceof IskaypetLineaTicket) {
					IskaypetLineaTicket line = (IskaypetLineaTicket) lineaTicket;
					if (line.getArticlePoints() != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equalsIgnoreCase(line.getArticlePoints().getReedem())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public ArticlesPointsXMLBean isLineWithArticlePoints(TicketVentaAbono ticket, IskaypetLineaTicket line) {
		return line.getArticlePoints();
	}

	public ArticlesPointsXMLBean isLineWithArticlePointsUsed(TicketVentaAbono ticket, Integer idLine) {
		int linea = idLine;
		if (linea > 0) {
			ILineaTicket lineaTicket = ticket.getLinea(linea);
			if (lineaTicket != null && lineaTicket instanceof IskaypetLineaTicket) {
				ArticlesPointsXMLBean articlePoints = ((IskaypetLineaTicket) lineaTicket).getArticlePoints();
				if (articlePoints != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equalsIgnoreCase(articlePoints.getReedem())) {
					return articlePoints;
				}
			}
		}
		return null;
	}

	public boolean canUsedButtonArticlePoints(Stage stage, TicketVentaAbono ticket, IskaypetLineaTicket line, boolean mensajeAviso) {
		String msgAviso = "";

		boolean isRedeemed = false;
		ArticlesPointsXMLBean articlePoints = isLineWithArticlePoints(ticket, line);
		if (articlePoints != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equals(articlePoints.getReedem())) {
			isRedeemed = true;
		}

		if (!isRedeemed) {
			// VALIDACION01 : La linea tiene que estar añadida a la lista de artículos de puntos de la cabecera.
			if (articlePoints == null) {
				msgAviso = I18N.getTexto("La linea seleccionada no tiene disponible la funcionalidad de canjeo de puntos.");
				// VALIDACION02 : Tiene que existir un fidelizado cargado en el ticket.
			}
			else {
				if (ticket.getCabecera().getDatosFidelizado() == null) {
					msgAviso = I18N.getTexto("Se necesita un fidelizado cargado para utilizar la funcionalidad de canjeo de puntos.");
				}
				// VALIDACION03 : Si la linea tiene cambio de precio manual, no debemos continuar.
				if (line.tieneCambioPrecioManual()) {
					msgAviso = I18N.getTexto("La linea seleccionada tiene un cambio de precio manual, no se puede canjear por puntos.");
				}
				// VALIDACION04 : Comprobamos si el fidelizado tiene los puntos suficientes para canjear el artículo.		
				if (StringUtils.isBlank(msgAviso)) {
					BigDecimal articlePointValue = articlePoints.getPoints();
					BigDecimal loyalPointsValue = ticket.getCabecera().getDatosFidelizado().getSaldo() != null ? ticket.getCabecera().getDatosFidelizado().getSaldo() : BigDecimal.ZERO;
					if (BigDecimalUtil.isMenor(loyalPointsValue, articlePointValue)) {
						BigDecimal needPoints = articlePointValue.subtract(loyalPointsValue).setScale(2);
						msgAviso = I18N.getTexto("El fidelizado no dispone de saldo suficiente para realizar el canjeo. \nSaldo actual : {0} \nPuntos necesarios : {1}",loyalPointsValue, needPoints);
					}
				}
			}
		}

		if (mensajeAviso && StringUtils.isNotBlank(msgAviso)) {
			VentanaDialogoComponent.crearVentanaAviso(msgAviso, stage);
			return false;
		}

		return true;
	}

	public boolean canUsedProcessPoints(TicketVentaAbono ticket, String itemCode) throws Exception {
		boolean result = false;
		try {
			ArticlePointsConditionsExample example = new ArticlePointsConditionsExample();
			example.or().andActivityIdEqualTo(sesion.getAplicacion().getUidActividad()).andItemcodeEqualTo(itemCode);
			List<ArticlePointsConditions> resultSearch = articlePointsConditionsMapper.selectByExample(example);
			if (resultSearch != null && !resultSearch.isEmpty()) {
				for (ArticlePointsConditions condition : resultSearch) {

					String itemCodePoints = condition.getItemcodeConditional();
					BigDecimal quantityPoints = condition.getQuantity();

					List<LineaTicket> listLineTicket = ticket.getLineas();
					for (LineaTicket lineaTicket : listLineTicket) {

						String itemCodeLine = lineaTicket.getCodArticulo();
						BigDecimal quantityLine = getQuantityArticleTicket(ticket, lineaTicket.getCodArticulo());

						if (itemCodePoints.equals(itemCodeLine)) {
							if (BigDecimalUtil.isMayorOrIgual(quantityPoints, quantityLine)) {
								result = true;
								break;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("Error al consultar o comprobar las condiciones del artículo de puntos : ") + e.getMessage();
			throw new Exception(msgError, e);
		}
		return result;
	}

	public BigDecimal getQuantityArticleTicket(TicketVentaAbono ticket, String itemCode) {
		BigDecimal totalQuantityArticle = BigDecimal.ZERO;
		List<LineaTicket> listLineTicket = ticket.getLineas();
		for (LineaTicket lineaTicket : listLineTicket) {
			if (itemCode.equalsIgnoreCase(lineaTicket.getCodArticulo())) {
				totalQuantityArticle = totalQuantityArticle.add(lineaTicket.getCantidad());
			}
		}
		return totalQuantityArticle;
	}


	/* ########################################################################################################################### */
	/* ######################################################## CONSULTA ######################################################### */
	/* ########################################################################################################################### */

	public ArticlesPointsXMLBean getArticlePoints(LineaTicket line, String itemCode) throws Exception {
		log.debug("getArticlePoints() - Iniciando la comprobación de si el artículo '" + itemCode + "' puede ser canjeado por puntos...");
		ArticlesPointsXMLBean newArticleReedemPoints = null;
		try {
			// PASO01 : Consultamos en la tabla 'X_ITEMS_POINTS_TBL', para saber si el artículo que estamos consultando puede ser canjeado por puntos.
			BigDecimal points = null, value = null;

			ArticlePointsBeanExample example = new ArticlePointsBeanExample();
			example.or().andActivityIdEqualTo(sesion.getAplicacion().getUidActividad()).andItemcodeEqualTo(itemCode);
			List<ArticlePointsBean> result = articlePointsMapper.selectByExample(example);
			if (result != null && !result.isEmpty()) {
				ArticlePointsBean articlePoints = result.get(0);
				points = articlePoints.getPoints();
				value = articlePoints.getPrice();
			}

			// PASO02 : Si no encontramos el artículo, no debemos continuar con el proceso.
			if (points == null || value == null) {
				return null;
			}
			else {
				points = points.multiply(line.getCantidad()).setScale(2);
				value = value.multiply(line.getCantidad()).setScale(2);
			}

			// PASO03 : En caso de encontrarlo, generamos el objeto con los datos que necesitamos para el XML en la cabecera.
			newArticleReedemPoints = new ArticlesPointsXMLBean(points, value, ArticlesPointsXMLBean.VALUE_REEDEM_KO);

			log.debug("getArticlePoints() - El artículo '" + itemCode + "' " + (newArticleReedemPoints != null ? "" : "no") + " puede ser canjeado por puntos.");
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("Error al comprobar si el artículo '{0}' es canjeable por puntos : ", itemCode) + e.getMessage();
			throw new Exception(msgError, e);
		}
		return newArticleReedemPoints;
	}


	/* ########################################################################################################################### */
	/* ##################################################### PROCESOS LINEA ###################################################### */
	/* ########################################################################################################################### */

	public void processLineArticlePoints(Stage stage, TicketVentaAbono ticket, LineaTicket line, String action, boolean dialogosActivos) {
		try {
			if (line != null && line instanceof IskaypetLineaTicket) {
				// INSERT
				if (ACTION_INSERT.equalsIgnoreCase(action)) {
					insertArticlePoints(line, line.getCodArticulo());
				}
				// UPDATE
				else if (ACTION_UPDATE.equalsIgnoreCase(action)) {
					updateArticlePoints(stage, ticket, line, dialogosActivos);
				}
				// DELETE
				else if (ACTION_DELETE.equalsIgnoreCase(action)) {
					deleteArticlePoints(ticket, (IskaypetLineaTicket) line);
				}
				else {
					throw new Exception("La acción seleccionada para la linea de artículo de canjeo de puntos no esta disponible.");
				}
			}
		}
		catch (Exception e) {
			String msgError = e.getMessage();
			log.error("processLineArticlePoints() - " + msgError, e);
		}
	}

	public void insertArticlePoints(LineaTicket line, String itemCode) throws Exception {
		try {
			// Consultamos el artículo, en caso de existir en las tablas de canjeo, generamos el objeto.
			ArticlesPointsXMLBean newArticlePoints = getArticlePoints(line, itemCode);
			if (newArticlePoints != null) {
				((IskaypetLineaTicket) line).setArticlePoints(newArticlePoints);
			}
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("Error al insertar el artículo canjeable por puntos con código '{0}' en la linea {1} : ", itemCode, line.getIdLinea()) + e.getMessage();
			throw new Exception(msgError, e);
		}
	}

	public void updateArticlePoints(Stage stage, TicketVentaAbono ticket, LineaTicket line, boolean dialogosActivos) throws Exception {
		try {
			ArticlesPointsXMLBean articlePoints = ((IskaypetLineaTicket) line).getArticlePoints();

			// PASO01 : Validación para no poner en la misma linea dos veces el mismo proceso.
			checkIsReedem(stage, articlePoints, true);

			// PASO001.1 : Solicitamos la confirmación para canjear los puntos.
			String msgConfirm = I18N.getTexto("¿Está seguro que desea canjear por puntos el artículo '") + line.getCodArticulo() + " - " + line.getDesArticulo() + "'?" + "\n- " + I18N.getTexto(
					"Puntos a utilizar : ") + articlePoints.getPoints() + "\n- "
					+ I18N.getTexto("Precio total del artículo : ") + articlePoints.getValue();

			if (dialogosActivos && !VentanaDialogoComponent.crearVentanaConfirmacion(msgConfirm, stage)) {
				return;
			}

			// PASO02 : Sacamos el valor del artículo para ponerlo como precio en la linea.
			SesionImpuestos sesionImpuestos = SpringContext.getBean(Sesion.class).getImpuestos();
			BigDecimal priceArticlePointsWithoutTax = sesionImpuestos.getPrecioSinImpuestos(line.getCodImpuesto(), BigDecimalUtil.redondear(articlePoints.getValue()),
					ticket.getCabecera().getCliente().getIdTratImpuestos());
			line.setPrecioSinDto(priceArticlePointsWithoutTax);
			line.setPrecioTotalSinDto(articlePoints.getValue());

			// PASO03 : Quitamos las promociones para que no las tenga en cuenta.
			line.setPromociones(new ArrayList<>());
			//En los artículos de puntos no se permite la aplicación de promociones
			line.setAdmitePromociones(false);
			((IskaypetLineaTicket) line).setTextosPromociones(new ArrayList<>());
			line.setDescuento(BigDecimal.ZERO);
			line.setDescuentoManual(BigDecimal.ZERO);

			// PASO04 : Volvemos a recalcular el importe de la linea después del cambio de precio.
			line.recalcularImporteFinal();
			ticket.getTotales().recalcular();

			// PASO05 : Tenemos que poner el valor como 1 para indicar que se ha usado.
			articlePoints.setReedem(ArticlesPointsXMLBean.VALUE_REEDEM_OK);

			// PASO06 : Restamos el saldo al fidelizado para verlo por pantalla.
			ticket.getCabecera().getDatosFidelizado().setSaldo(ticket.getCabecera().getDatosFidelizado().getSaldo().subtract(articlePoints.getPoints()));

			// PASO07 : Mostramos el mensaje que indica que se han consumido puntos.
			if(dialogosActivos){
				String msgInfo = "Artículo canjeado (" + line.getCodArticulo() + " - " + line.getDesArticulo() + ")" + "\n- Puntos usados : " + articlePoints.getPoints() + "\n- Precio total del artículo : " + articlePoints.getValue() + "\n";
				VentanaDialogoComponent.crearVentanaInfo(msgInfo, stage);
			}

		}
		catch (Exception e) {
			String msgError = I18N.getTexto("Error al modificar de la lista de artículos canjeables por puntos la línea {0} :", + line.getIdLinea()) + e.getMessage();
			throw new Exception(msgError, e);
		}
	}

	public void deleteArticlePoints(TicketVentaAbono ticket, IskaypetLineaTicket line) throws Exception {
		log.debug("deleteArticlePoints() - Iniciando la eliminación de la linea del ticket " + line.getIdLinea() + " del artículo canjeable por puntos...");
		try {
			ArticlesPointsXMLBean articlePoints = line.getArticlePoints();

			// Volvemos a añadirle los puntos al fidelizado para visualizarlo.
			if (articlePoints != null && ArticlesPointsXMLBean.VALUE_REEDEM_OK.equalsIgnoreCase(articlePoints.getReedem())) {
				ticket.getCabecera().getDatosFidelizado().setSaldo(ticket.getCabecera().getDatosFidelizado().getSaldo().add(articlePoints.getPoints()));
			}
			line.setArticlePoints(null);

			log.debug("deleteArticlePoints() - Finalizada la eliminación de la linea del ticket " + line.getIdLinea() + " del artículo canjeable por puntos.");
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("Error al eliminar de la lista de artículos canjeables por puntos la línea {0} : ", line.getIdLinea()) + e.getMessage();
			throw new Exception(msgError, e);
		}
	}

	public boolean checkIsReedem(Stage stage, ArticlesPointsXMLBean articlePoints, boolean aviso){
		if (ArticlesPointsXMLBean.VALUE_REEDEM_OK.equalsIgnoreCase(articlePoints.getReedem())) {
			String msgAviso = I18N.getTexto("La linea seleccionada ya tiene aplicados puntos para ese artículo.");
			if(aviso){
				VentanaDialogoComponent.crearVentanaAviso(msgAviso, stage);
			}
			return false;
		}
		return true;
	}

}
 