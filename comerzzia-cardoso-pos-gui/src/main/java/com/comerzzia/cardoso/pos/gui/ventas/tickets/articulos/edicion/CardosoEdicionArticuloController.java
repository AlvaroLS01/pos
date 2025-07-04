package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.edicion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.cardoso.pos.gui.promociones.monograficas.PromocionesEspeciales;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.CardosoLotesView;
import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloBean;
import com.comerzzia.cardoso.pos.services.lotes.CardosoLoteService;
import com.comerzzia.cardoso.pos.services.lotes.exception.CardosoLoteException;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CardosoLote;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Controller
@Primary
public class CardosoEdicionArticuloController extends EdicionArticuloController{

	private static final Logger log = Logger.getLogger(CardosoEdicionArticuloController.class.getName());

	@Autowired
	protected Sesion sesion;
	
	/* GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS */
	@Autowired
	private PromocionesEspeciales promocionesEspecialesServicio;
	
	/**
     * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - LOTES
	 * GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE
	 * 
	 * Personalizaciones necesarias para poder editar una linea y tener compatibilidad con los lotes y los números de serie.
	 */
	
	@FXML
	private Button btLotes;
	@Autowired
	private CardosoLoteService loteService;

	private BigDecimal cantidadOriginal;
	
	@Override
	public void initializeComponents(){
		log.debug("initializeComponents() : GAP - PERSONALIZACIONES V3 - LOTES // GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE");
		
		super.initializeComponents();

		tfCantidad.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(!oldValue.equals(newValue)){
					btNumerosSerie.setDisable(true);
					btLotes.setDisable(true);
				}
			}
		});
	}

	@Override
	public void initializeForm(){
		log.debug("initializeComponents() : GAP - PERSONALIZACIONES V3 - LOTES // GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE");
		
		super.initializeForm();
		
		cantidadOriginal = linea.getCantidad();

		btNumerosSerie.setDisable(false);
		btLotes.setDisable(false);

		if(linea.getArticulo().getNumerosSerie()){
			if(!panelLotesSerie.getChildren().contains(btNumerosSerie)){
				panelLotesSerie.getChildren().add(btNumerosSerie);
			}
		}
		else{
			if(panelLotesSerie.getChildren().contains(btNumerosSerie)){
				panelLotesSerie.getChildren().remove(btNumerosSerie);
			}
		}
		CardosoArticuloBean articulo = (CardosoArticuloBean) linea.getArticulo();
		if(articulo.getAtributosAdicionalesArticulo() != null && articulo.getAtributosAdicionalesArticulo().getLote()){
			if(!panelLotesSerie.getChildren().contains(btLotes)){
				panelLotesSerie.getChildren().add(btLotes);
			}
		}
		else{
			if(panelLotesSerie.getChildren().contains(btLotes)){
				panelLotesSerie.getChildren().remove(btLotes);
			}
		}
	}

	public void editarLotes(){
		log.debug("initializeComponents() : GAP - PERSONALIZACIONES V3 - LOTES // GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE");
		
		/* Actualizamos la cantidad antes de abrir la pantalla */
		BigDecimal nuevaCantidad = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText());
		if(nuevaCantidad != null){
			linea.setCantidad(nuevaCantidad);

			getDatos().put(CLAVE_PARAMETRO_ARTICULO, linea);
			getApplication().getMainView().showModalCentered(CardosoLotesView.class, getDatos(), getStage());
			linea = (LineaTicket) getDatos().get(CLAVE_PARAMETRO_ARTICULO);
		}
	}

	@Override
	public void accionAceptar(){
		log.debug("initializeComponents() : GAP - PERSONALIZACIONES V3 - LOTES // GAP - PERSONALIZACIONES V3 - NÚMEROS DE SERIE");
		
		log.debug("accionAceptar()");
		BigDecimal precio;
		try{
			precio = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 3);
			tfCantidad.setText(FormatUtil.getInstance().formateaNumero(precio, 3));
		}
		catch(Exception e){}
		
		boolean aplicarCambiosEdicion = true;
		BigDecimal nuevaCantidad = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText());
		if(!cantidadOriginal.equals(nuevaCantidad)){
			/* Realizamos las comprobaciones de los números de serie */
			sonNumerosSerieValidos();

			CardosoArticuloBean articulo = (CardosoArticuloBean) linea.getArticulo();
			if(articulo.getAtributosAdicionalesArticulo() != null && articulo.getAtributosAdicionalesArticulo().getLote()){
				BigDecimal cantidadActual = BigDecimal.ZERO;
				if(((CARDOSOLineaTicket) linea).getLotes() != null && !((CARDOSOLineaTicket) linea).getLotes().isEmpty()){
					for(CardosoLote lote : ((CARDOSOLineaTicket) linea).getLotes()){
						cantidadActual = cantidadActual.add(lote.getCantidad());
					}
				}
				if(((CARDOSOLineaTicket) linea).getLotes() != null && BigDecimalUtil.isMayor(cantidadActual, nuevaCantidad)){
					/* Comprobamos si se ha modificado la línea una vez insertados lotes y se ha bajado la cantidad.
					 *  En	ese caso, se borráran los lotes registrados y se incluirá un lote con toda la cantidad de 
					 *  la línea si se encuentra en la tabla de los lotes o se mostrará la pantalla de lotes en el caso
					 *  de que no haya un registro en la tabla para ese artículo */
					aplicarCambiosEdicion = false;
					if(VentanaDialogoComponent.crearVentanaConfirmacion(
					        I18N.getTexto("Esta linea tiene asociados lotes, la cantidad indicada es inferior a la anterior, los lotes serán borrados, ¿está seguro?"), getStage())){
						((CARDOSOLineaTicket) linea).getLotes().clear();
						try{
							CardosoLoteArticuloBean lote = loteService.getLoteArticulo(sesion.getAplicacion().getUidActividad(), articulo.getCodArticulo());
							if(lote != null && StringUtils.isNotBlank(lote.getLote())){
								CardosoLote nuevoLote = new CardosoLote();
								nuevoLote.setId(lote.getLote());
								nuevoLote.setCantidad(linea.getCantidad());
								List<CardosoLote> listaLote = new ArrayList<CardosoLote>();
								listaLote.add(nuevoLote);
								((CARDOSOLineaTicket) linea).setLotes(listaLote);
							}
							else{
								editarLotes();
							}
						}
						catch(CardosoLoteException e){
							log.error("accionAceptar() - Ha habido un error al buscar en la tabla de lotes: " + e.getMessage());
						}
					}
				}
			}
		}
		
		if(aplicarCambiosEdicion){
			frEdicionArticulo.setCantidad(tfCantidad.getText());
			if(!bVentaProfesional){
				try{
					precio = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 2);
					tfPrecio.setText(FormatUtil.getInstance().formateaImporte(precio));
				}
				catch(Exception e){}
				frEdicionArticulo.setPrecioFinalProfesional("0");
				frEdicionArticulo.setPrecioFinal(tfPrecio.getText());
			}
			else{
				try{
					precio = FormatUtil.getInstance().desformateaBigDecimal(tfPrecio.getText(), 4);
					tfPrecio.setText(FormatUtil.getInstance().formateaNumero(precio, 4));
				}
				catch(Exception e){}
				frEdicionArticulo.setPrecioFinal("0");
				frEdicionArticulo.setPrecioFinalProfesional(tfPrecio.getText());
			}
			frEdicionArticulo.setDescuento(tfDescuento.getText());
			frEdicionArticulo.setVendedor((UsuarioBean) cbVendedor.getSelectionModel().getSelectedItem());
			frEdicionArticulo.setDesArticulo(tfDescripcion.getText());
			if(validarFormularioEdicionArticulo() && sonNumerosSerieValidos()){
				BigDecimal nuevaCantidad2 = frEdicionArticulo.getCantidadAsBD();
				BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), linea.getPrecioTotalSinDto(), ticketManager.getTicket().getIdTratImpuestos());
				lineaOriginal.setPrecioSinDto(precioSinImpuestos);
				lineaOriginal.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaOriginal.setDescuentoManual(linea.getDescuentoManual());
				lineaOriginal.setCantidad(ticketManager.tratarSignoCantidad(nuevaCantidad2, linea.getCabecera().getCodTipoDocumento()));
				lineaOriginal.setVendedor(frEdicionArticulo.getVendedor());
				lineaOriginal.setDesArticulo(frEdicionArticulo.getDesArticulo());
				lineaOriginal.setNumerosSerie(linea.getNumerosSerie());
				lineaOriginal.recalcularImporteFinal();
				
				((CARDOSOLineaTicket) lineaOriginal).setNumerosSerie(((CARDOSOLineaTicket) linea).getNumerosSerie());
				((CARDOSOLineaTicket) lineaOriginal).setLotes(((CARDOSOLineaTicket) linea).getLotes());
				
				if(aplicarPromociones){
					ticketManager.recalcularConPromociones();
				}
				if(lineaOriginal.tieneCambioPrecioManual()){
					cambioPrecioManual();
				}
				if(lineaOriginal.tieneDescuentoManual()){
					cambioDescuentoManual();
				}
				getStage().close();
			}	
		}
		
		// GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS
		if(BigDecimalUtil.isMayorACero(linea.getDescuentoManual()) 
				&& ((CARDOSOLineaTicket) lineaOriginal).getPromocionMonograficaAplicada() != null){
			((CARDOSOLineaTicket) lineaOriginal).setImporteDtoMonografica(BigDecimal.ZERO);
			((CARDOSOLineaTicket) lineaOriginal).setImporteSinDtoMonogafica(BigDecimal.ZERO);
			((CARDOSOLineaTicket) lineaOriginal).setImporteTotalDtoMonografica(BigDecimal.ZERO);
			((CARDOSOLineaTicket) lineaOriginal).setImporteTotalSinDtoMonogafica(BigDecimal.ZERO);
			((CARDOSOLineaTicket) lineaOriginal).setPromocionMonograficaAplicada(null);
			((CARDOSOLineaTicket) lineaOriginal).setPorcentajeDtoMonografica(BigDecimal.ZERO);
			
			promocionesEspecialesServicio.procesarDescuentosPromocionesMonograficas((CARDOSOTicketManager) ticketManager);
		}
		
	}

}
