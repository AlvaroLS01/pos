package com.comerzzia.pos.gui.mantenimientos.clientes.codPostales;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalBean;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class SeleccionPoblacionController extends WindowController {

	public static final String PARAMETRO_LISTA_CODIGOS_POSTALES = "CodigosPostales"; 
	public static final String PARAMETRO_CODIGO_POSTAL_SELECCIONADO = "CodigosPostalSeleccionado"; 
	
    @FXML
	protected TableView tbCodPostales;
    @FXML
	protected TableColumn clProvincia, clLocalidad, clPoblacion;
    
    protected ObservableList<CodigoPostalBean> codigosPostales;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
    	registrarAccionCerrarVentanaEscape();
    	
		clProvincia.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodPostales", "clProvincia", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		clProvincia.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodPostales", "clLocalidad", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		clProvincia.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodPostales", "clPoblacion", null,CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		clProvincia.setCellValueFactory(new PropertyValueFactory<CodigoPostalBean, String>("provincia"));
		clLocalidad.setCellValueFactory(new PropertyValueFactory<CodigoPostalBean, String>("localidad"));
		clPoblacion.setCellValueFactory(new PropertyValueFactory<CodigoPostalBean, String>("poblacion"));
    }

	@Override
    public void initializeForm() throws InitializeGuiException {
		tbCodPostales.getSelectionModel().clearSelection();
		codigosPostales = FXCollections.observableList((List<CodigoPostalBean>) getDatos().get(PARAMETRO_LISTA_CODIGOS_POSTALES));
		tbCodPostales.setItems(codigosPostales);
    }

	@Override
    public void initializeFocus() {
		tbCodPostales.requestFocus();
    }
	
	@FXML
	public void accionAceptar() {
		CodigoPostalBean codigoPostal = (CodigoPostalBean) tbCodPostales.getSelectionModel().getSelectedItem();
		if(codigoPostal != null) {
			getDatos().put(PARAMETRO_CODIGO_POSTAL_SELECCIONADO, codigoPostal);
			getStage().close();
		} else {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Seleccione una población antes de continuar."), getStage());
		}
	}

}
