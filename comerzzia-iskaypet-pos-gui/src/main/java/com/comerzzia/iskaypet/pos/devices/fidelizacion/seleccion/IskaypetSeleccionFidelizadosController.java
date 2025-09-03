package com.comerzzia.iskaypet.pos.devices.fidelizacion.seleccion;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.iskaypet.pos.devices.fidelizacion.busqueda.IskaypetBusquedaFidelizadoController;
import com.comerzzia.iskaypet.pos.devices.fidelizacion.gestion.GestionTicketsFidelizadoView;
import com.comerzzia.iskaypet.pos.gui.ventas.devoluciones.IskaypetDevolucionesController;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController;
import com.comerzzia.pos.dispositivo.fidelizacion.seleccion.SeleccionFidelizadoController;
import com.comerzzia.pos.dispositivo.fidelizacion.seleccion.SeleccionFidelizadoGui;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;

@Primary
@Component
public class IskaypetSeleccionFidelizadosController extends SeleccionFidelizadoController {
		
	private Boolean esDevolucion;
	
	private FidelizadoBean fidelizado;

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		esDevolucion = (Boolean) getDatos().get(IskaypetDevolucionesController.ES_DEVOLUCION);
		fidelizado = (FidelizadoBean) getDatos().get(BusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO);
	}

	@FXML
	 public void accionAceptar(){
		  if(esDevolucion!= null && esDevolucion) {	  
			if(fidelizado!=null) {
				getApplication().getMainView().showModalCentered(GestionTicketsFidelizadoView.class, datos, this.getStage());
				getStage().close();
				
			}
			else if(tbFidelizados.getSelectionModel().getSelectedItem()!=null){
	        	
	            getDatos().put(BusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO, ((SeleccionFidelizadoGui)tbFidelizados.getSelectionModel().getSelectedItem()).getFidelizado());
	            getApplication().getMainView().showModalCentered(GestionTicketsFidelizadoView.class, datos, getStage());
				getStage().close();

	        }
	        else{
	            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un fidelizado de la lista."), getStage());
	        }
	        
		  }
		  else{
			  super.accionAceptar();
		  }
	  }

	@FXML
    public void accionCancelar(){
		//"limpiamos" los datos para que no interfiera con ventas
		getDatos().put(IskaypetDevolucionesController.ES_DEVOLUCION, false);
		getDatos().put(IskaypetBusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO,null);
        getStage().close();
    }

	@Override
	public void setTitulo() {
		lbTitulo.setText(I18N.getTexto("Selección de Cliente"));
	}
}