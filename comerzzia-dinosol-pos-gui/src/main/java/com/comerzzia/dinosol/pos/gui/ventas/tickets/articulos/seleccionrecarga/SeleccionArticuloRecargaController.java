package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.seleccionrecarga;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.RecargasService;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos.ArticulosRecargaService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.recargamovil.RecargaMovilNoConfig;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class SeleccionArticuloRecargaController extends WindowController {

	private Logger log = Logger.getLogger(SeleccionArticuloRecargaController.class);

	public static final String COD_ART_RECARGA = "CODIGO_ARTICULO_RECARGA";
	public static final String CANTIDAD_RECARGA = "CANTIDAD_RECARGA";
	
	private static final String TIPO_RECARGA_MOVIL = "SeleccionArticuloRecargaController.RecargaMovil";
	private static final String TIPO_POSA_CARD = "SeleccionArticuloRecargaController.PosaCard";
	private static final String TIPO_PIN_PRINTING = "SeleccionArticuloRecargaController.PinPrinting";

	@FXML
	protected TextField tfImporte;
	
	@FXML
	protected Label lbTipoImporte;
	
	@FXML
	private Button btCancelar, btPinPrinting, btRecargaMovil, btPosaCard, btVolver;
	
	@FXML
	private FlowPane fpBotones;
	
	@FXML
	private HBox hbImporte;
	
	@Autowired
	private ArticulosRecargaService articulosRecargaService;
	
	@Autowired
	private ArticulosService articulosService;
	
	@Autowired
	private RecargasService recargasService;
	
	private List<ArticuloBean> articulosRecargaMovil;
	private List<ArticuloBean> articulosPosaCard;
	private List<ArticuloBean> articulosPinPrinting;
	
	private String tipoRecarga;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfImporte.setUserData(keyboardDataDto);
		addSeleccionarTodoEnFoco(tfImporte);

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfImporte.setText("");
		if (Dispositivos.getInstance().getRecargaMovil().getClass().equals(RecargaMovilNoConfig.class)) {
			InitializeGuiException initializeGuiException = new InitializeGuiException(I18N.getTexto("No hay ningún dispositivo configurado"));
			throw initializeGuiException;
		}
		
		mostrarBotonesPrincipales();
	}

	@Override
	public void initializeFocus() {
		tfImporte.requestFocus();
	}

	@FXML
	public void accionCancelar() {
		getStage().close();
	}

	public void cargaPinPrinting() throws InitializeGuiException {
		try {
			tfImporte.setText("1");
			
	        ocultarBotonesPrincipales();
	        
	        cargarArticulosPinPrinting();
	        
	        mostrarBotonesArticulos(articulosPinPrinting);
	        
	        tipoRecarga = TIPO_PIN_PRINTING;
        }
        catch (Exception e) {
        	log.error("cargaPinPrinting() - Ha habido un error al cargar la botonera de PIN printing: " + e.getMessage(), e);
        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al mostrar los operadores disponibles. Contacte con el administrador"), e);
        }
	}

	public void cargaRecargaMovil() throws InitializeGuiException {
		try {
			hbImporte.setVisible(true);
			
	        ocultarBotonesPrincipales();
	        
	        cargarArticulosRecargaMovil();
	        
	        mostrarBotonesArticulos(articulosRecargaMovil);
	        
	        tipoRecarga = TIPO_RECARGA_MOVIL;
        }
        catch (Exception e) {
        	log.error("cargaRecargaMovil() - Ha habido un error al cargar la botonera de recarga de móvil: " + e.getMessage(), e);
        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al mostrar los operadores disponibles. Contacte con el administrador"), e);
        }
	}

	public void cargaPosaCard() throws InitializeGuiException {
		try {
			tfImporte.setText("1");
			
	        ocultarBotonesPrincipales();
	        
	        cargarArticulosPosaCard();
	        
	        mostrarBotonesArticulos(articulosPosaCard);
	        
	        tipoRecarga = TIPO_POSA_CARD;
        }
        catch (Exception e) {
        	log.error("cargaPosaCard() - Ha habido un error al cargar la botonera de POSA Card: " + e.getMessage(), e);
        	VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al mostrar los operadores disponibles. Contacte con el administrador"), e);
        }
	}

	private void mostrarBotonesArticulos(List<ArticuloBean> articulos) {
        for(ArticuloBean articulo : articulos) {
        	Button boton = new Button();
        	
        	ImageView imageView = new ImageView();
        	URL urlImagen = articulosRecargaService.getUrlImage(articulo.getCodArticulo());
        	if(urlImagen != null) {
	        	Image image = new Image(urlImagen.toString());
	        	imageView.setImage(image);
	        	boton.setGraphic(imageView);
        	}
        	else {
        		boton.setText(articulo.getDesArticulo());
        	}
        	
        	boton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					seleccionarOperador(articulo);
				}
			});
        	
        	fpBotones.getChildren().add(boton);
        }
        btVolver.setVisible(true);
    }

	private void cargarArticulosRecargaMovil() throws Exception {
		if(articulosRecargaMovil == null) {
			List<String> codigosArticuloRecargaMovil = articulosRecargaService.getConfiguracion().getArticulosRecargaMovil();
			articulosRecargaMovil = new ArrayList<ArticuloBean>();
			for(String codart : codigosArticuloRecargaMovil) {
				try {
					ArticuloBean articulo = articulosService.consultarArticulo(codart);
					articulosRecargaMovil.add(articulo);
				}
				catch(ArticuloNotFoundException e) {
					log.error("cargarArticulosRecargaMovil() - El artículo con código " + codart + " no existe en el sistema.");
				}
			}
		}
    }

	private void cargarArticulosPinPrinting() throws Exception {
		if(articulosPinPrinting == null) {
			List<String> codigosArticuloPinPrinting = articulosRecargaService.getConfiguracion().getArticulosPinPrinting();
			articulosPinPrinting = new ArrayList<ArticuloBean>();
			for(String codart : codigosArticuloPinPrinting) {
				try {
					ArticuloBean articulo = articulosService.consultarArticulo(codart);
					articulosPinPrinting.add(articulo);
				}
				catch(ArticuloNotFoundException e) {
					log.error("cargarArticulosPinPrinting() - El artículo con código " + codart + " no existe en el sistema.");
				}
			}
		}
    }
	
	private void cargarArticulosPosaCard() throws Exception {
		if(articulosPosaCard == null) {
			List<String> codigosArticuloPosaCard = articulosRecargaService.getConfiguracion().getArticulosPosaCard();
			articulosPosaCard = new ArrayList<ArticuloBean>();
			for(String codart : codigosArticuloPosaCard) {
				try {
					ArticuloBean articulo = articulosService.consultarArticulo(codart);
					articulosPosaCard.add(articulo);
				}
				catch(ArticuloNotFoundException e) {
					log.error("cargarArticulosPosaCard() - El artículo con código " + codart + " no existe en el sistema.");
				}
			}
		}
    }

	private void ocultarBotonesPrincipales() {
		ocultarBoton(btPosaCard);
		ocultarBoton(btPinPrinting);
		ocultarBoton(btRecargaMovil);
	}
	
	public void mostrarBotonesPrincipales() {
		mostrarBoton(btPosaCard);
		mostrarBoton(btPinPrinting);
		mostrarBoton(btRecargaMovil);
		
		Iterator<Node> it = fpBotones.getChildren().iterator();
		while(it.hasNext()) {
			Node node = it.next();
			if(!node.equals(btPosaCard) && !node.equals(btPinPrinting) && !node.equals(btRecargaMovil)) {
				it.remove();
			}
		}
		
		btVolver.setVisible(false);
		tfImporte.clear();
		hbImporte.setVisible(false);
		
		tipoRecarga = null;
	}
	
	private void ocultarBoton(Button boton) {
		boton.setVisible(false);
		boton.setManaged(false);
	}
	
	private void mostrarBoton(Button boton) {
		boton.setVisible(true);
		boton.setManaged(true);
	}
	


	protected void seleccionarOperador(ArticuloBean articulo) {
		String importe = tfImporte.getText();
		String codArticulo = articulo.getCodArticulo();
		
		if (StringUtils.isBlank(importe)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe introducir un importe"), getStage());
			tfImporte.requestFocus();
		}
		else if (BigDecimalUtil.isIgualACero(FormatUtil.getInstance().desformateaImporte(importe))) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El importe 0 no está permitido"), getStage());
			tfImporte.requestFocus();
		}
		else if (tipoRecarga != null && tipoRecarga.equals(TIPO_RECARGA_MOVIL) && recargasService.getOperador(codArticulo, FormatUtil.getInstance().desformateaImporte(importe), true) == null) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El importe indicado no está permitido para este operador. Pruebe con otro distinto."), getStage());
			tfImporte.requestFocus();
		}
		else {
			getDatos().put(SeleccionArticuloRecargaController.CANTIDAD_RECARGA, importe);
			getDatos().put(SeleccionArticuloRecargaController.COD_ART_RECARGA, codArticulo);
			getStage().close();
		}
	}

}
