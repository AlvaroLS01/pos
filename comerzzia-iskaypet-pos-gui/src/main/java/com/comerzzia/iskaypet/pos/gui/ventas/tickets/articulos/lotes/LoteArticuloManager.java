package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjeto;
import com.comerzzia.core.servicios.clases.parametros.valoresobjeto.ValoresParametrosObjetosService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.stage.Stage;

@Service
public class LoteArticuloManager {

	public static final String CLASE_PARAMETRO_CODART = "D_ARTICULOS_TBL.CODART";
	
	public static final String VARIABLE_DEVOLUCION_MEDICAMENTOS = "X_POS.PERMITE_DEVOLUCION_MEDICAMENTOS";
	public static final String VARIABLE_PROPIEDAD_MED_CON_RECETA = "X_POS.PROPIEDAD_MEDICAMENTO_CON_RECETA";
	public static final String VARIABLE_PROPIEDAD_MED_SIN_RECETA = "X_POS.PROPIEDAD_MEDICAMENTO_SIN_RECETA";
	public static final String VALOR_DEFECTO_PROPIEDAD_MED_CON_RECETA = "medicamentoConReceta";
	public static final String VALOR_DEFECTO_PROPIEDAD_MED_SIN_RECETA = "medicamentoSinReceta";
	public static final String VARIABLE_REQUIERE_CLIENTE_RECETA = "X_POS.REQUIERE_CLIENTE_RECETA";
	public static final String VARIABLE_REQUIERE_CLIENTE_SIN_RECETA = "X_POS.REQUIERE_CLIENTE_SIN_RECETA";
	
	public static final String VARIABLE_PEDIR_LOTE_MEDICAMENTO = "X_POS.PEDIR_LOTE_MEDICAMENTOS";
	
	private static final Logger log = Logger.getLogger(LoteArticuloManager.class);
	
	@Autowired
	private ValoresParametrosObjetosService valoresParametrosObjetosService;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private Sesion sesion;
	
	
	public boolean esArticuloMedicamento(String codArt, FidelizacionBean fidelizado, boolean esDevolucion) throws LineaTicketException {
		
		boolean esMedicamento = false;
		
		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			
			String propiedadMedConReceta = getValorVariablePropiedadMedicamento(VARIABLE_PROPIEDAD_MED_CON_RECETA, VALOR_DEFECTO_PROPIEDAD_MED_CON_RECETA);
			String propiedadMedSinReceta = getValorVariablePropiedadMedicamento(VARIABLE_PROPIEDAD_MED_SIN_RECETA, VALOR_DEFECTO_PROPIEDAD_MED_SIN_RECETA);
			
			//solo comprobamos si es medicamento, si la tienda está configurada para pedir lote.
			if(tiendaPideLote()) {
				List<ValorParametroObjeto> listaParametros = valoresParametrosObjetosService.consultarValoresParametrosPorClaseYObjeto(CLASE_PARAMETRO_CODART, codArt, datosSesion);
				for(ValorParametroObjeto param : listaParametros) {
					if (propiedadMedConReceta.equalsIgnoreCase(param.getParametro())) {
						if("S".equalsIgnoreCase(param.getValor())) {
							if (!"N".equalsIgnoreCase(getValorVariableRequiereCliente(VARIABLE_REQUIERE_CLIENTE_RECETA, esDevolucion))) {
								if (fidelizado == null) {
									throw new LineaTicketException(I18N.getTexto("Es necesario identificar al cliente para la venta del medicamento con receta"));
								} else {
									esMedicamento = true;
									break;
								}
							} else {
								esMedicamento = true;
								break;
							}
						}
					} else if (propiedadMedSinReceta.equalsIgnoreCase(param.getParametro())) {
						if("S".equalsIgnoreCase(param.getValor())) {
							if (!"N".equalsIgnoreCase(getValorVariableRequiereCliente(VARIABLE_REQUIERE_CLIENTE_SIN_RECETA, esDevolucion))) {
								if (fidelizado == null) {
									throw new LineaTicketException(I18N.getTexto("Es necesario identificar al cliente para la venta del medicamento sin receta"));
								} else {
									esMedicamento = true;
									break;
								}
							} else {
								esMedicamento = true;
								break;
							}
						}
					}
				}
			}
		} catch (LineaTicketException e) {
			log.error("esArticuloMedicamento() - " + e.getMessage(), e);
			throw new LineaTicketException(I18N.getTexto(e.getMessage()));
		} catch (Exception e) {
			log.error("esArticuloMedicamento() - Error en la comprobación de medicamentos", e);
			throw new LineaTicketException(I18N.getTexto("Error en la comprobación de medicamentos."));
		}
			
		return esMedicamento;
	}

	public boolean esArticuloMedicamento(String codArt) {
		log.debug("esArticuloMedicamento() - Comprobando si el artículo es un medicamento: " + codArt);

		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());

			String propiedadMedConReceta = getValorVariablePropiedadMedicamento(VARIABLE_PROPIEDAD_MED_CON_RECETA, VALOR_DEFECTO_PROPIEDAD_MED_CON_RECETA);
			String propiedadMedSinReceta = getValorVariablePropiedadMedicamento(VARIABLE_PROPIEDAD_MED_SIN_RECETA, VALOR_DEFECTO_PROPIEDAD_MED_SIN_RECETA);

			if (tiendaPideLote()) {
				List<ValorParametroObjeto> listaParametros = valoresParametrosObjetosService.consultarValoresParametrosPorClaseYObjeto(CLASE_PARAMETRO_CODART, codArt, datosSesion);
				for (ValorParametroObjeto param : listaParametros) {
					if ((propiedadMedConReceta.equalsIgnoreCase(param.getParametro()) || propiedadMedSinReceta.equalsIgnoreCase(param.getParametro()))
							&& "S".equalsIgnoreCase(param.getValor())) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error("esArticuloMedicamento() - Error en la comprobación de medicamentos", e);
			return false;
		}
	}

	public boolean tiendaPideLote() {
		//Habilitamos la edicion de lote en base a la configuración de la tienda por la variable  X_POS_USA_LOTE_MEDICAMENTO;
		//Solo se debe pedir lote si su valor S o no existe o no tiene valor, si su valor es N no se pide lote
		boolean pedirLote = true;
		
		if(StringUtils.isNotBlank(variablesServices.getVariableAsString(VARIABLE_PEDIR_LOTE_MEDICAMENTO))) {
			pedirLote = variablesServices.getVariableAsBoolean(VARIABLE_PEDIR_LOTE_MEDICAMENTO);
		}
		
		return pedirLote;

	}
	
	/**
	 * Devuelve la sublista de líneas que están añadidas al ticket de ticketManager
	 * @param listLines
	 * @param ticketManager
	 * @return
	 */
	public List<LineaTicket> devuelveLineasAnadidasATicket(List<LineaTicket> listLines, TicketManager ticketManager){
		List<LineaTicket> lineasMedicamentos = new ArrayList<LineaTicket>(listLines.size());
		for(LineaTicket linea : listLines) {
			if(ticketManager.getTicket().getLineas().contains(linea)) {
				lineasMedicamentos.add(linea);
			}
		}
		return lineasMedicamentos;
	}
	
	/**
	 * Devuelve la sublista de líneas que son medicamentos y no tienen lote 
	 */
	public List<IskaypetLineaTicket> devuelveLineasSinLote(List<IskaypetLineaTicket> lineas, FidelizacionBean fidelizado, boolean esDevolucion) throws LineaTicketException{
		List<IskaypetLineaTicket> lineasSinLote = new ArrayList<>();
		if(lineas==null) {
			return null;
		}
		if(lineas.isEmpty()) {
			return lineasSinLote;
		}
		
		for (IskaypetLineaTicket linea : lineas) {
			if(esArticuloMedicamento(linea.getCodArticulo(), fidelizado, esDevolucion)) {
				if(linea.getLote()==null) {
					lineasSinLote.add(linea);
				}
			}
		}
		
		return lineasSinLote;
	}
	
	/**
	 * Si codart es medicamento, comprueba si se permite la devolción de medicamentos
	 */
	public void comprobarPermisoDevolucionMedicamentos(TicketManager ticketManager, String codart, Stage stage, FidelizacionBean fidelizado, boolean esDevolucion) throws LineaTicketException {
		try {
			
			Boolean permiteDevolucionMedicamentos = variablesServices.getVariableAsBoolean(VARIABLE_DEVOLUCION_MEDICAMENTOS, false);
			boolean esMedicamento = esArticuloMedicamento(codart, fidelizado, esDevolucion);
			
			if(esMedicamento) {
				if(permiteDevolucionMedicamentos) {
					
					if(ticketManager.isTicketVacio()) {
						boolean confirmaDevolucion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("La devolución de medicamentos solo está permitida para casos de alerta sanitaria y otras excepciones ver Procedimiento xxx. ¿Desea continuar?"), stage);
						if(!confirmaDevolucion) {
							throw new LineaTicketException(I18N.getTexto("Devolución de medicamentos cancelada."));
						}
						
					}
					
				}
				else {
					throw new LineaTicketException(I18N.getTexto("Devolución de medicamentos no autorizada."));
				}
			}
			
		} catch (LineaTicketException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error procesando devolución de medicamento: " + codart, e);
			throw new LineaTicketException(I18N.getTexto("Error procesando devolución de medicamento. Revise configuración."));
		}
	}
	
	public String getValorVariablePropiedadMedicamento(String idVariable, String valorDefecto) {
		String valor = "";
		try {
			valor = variablesServices.getVariableAsString(idVariable);
			if (StringUtils.isBlank(valor)) {
				throw new Exception();
			}
		} catch (Exception e) {
			log.info("La variable " + idVariable + " no tiene valor o no está definida, se asignará el valor por defecto: " + valorDefecto);
			return valorDefecto;
		}
		return valor;
	}
	
	public String getValorVariableRequiereCliente(String idVariable, boolean esDevolucion) {
		String valor = "";
		
		//En devoluciones no se valida la condición del cliente
		if(esDevolucion) {
			return "N";
		}
		
		try {
			valor = variablesServices.getVariableAsString(idVariable);
			if (StringUtils.isBlank(valor)) {
				throw new Exception();
			}
		} catch (Exception e) {
			log.info("La variable " + idVariable + " no tiene valor o no está definida, se asignará el valor por defecto: S");
			return "S";
		}
		return valor;
	}

	public boolean esMedicamentoConReceta(String codArt) {
		log.debug("esMedicamentoConReceta() - Comprobando si el artículo es un medicamento CON receta: " + codArt);

		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());

			String propiedadMedConReceta = getValorVariablePropiedadMedicamento(
					VARIABLE_PROPIEDAD_MED_CON_RECETA,
					VALOR_DEFECTO_PROPIEDAD_MED_CON_RECETA
			);


			List<ValorParametroObjeto> listaParametros = valoresParametrosObjetosService
					.consultarValoresParametrosPorClaseYObjeto(CLASE_PARAMETRO_CODART, codArt, datosSesion);

			for (ValorParametroObjeto param : listaParametros) {
				if (propiedadMedConReceta.equalsIgnoreCase(param.getParametro())
						&& "S".equalsIgnoreCase(param.getValor())) {
					return true;
				}
			}


			return false;
		} catch (Exception e) {
			log.error("esMedicamentoConReceta() - Error en la comprobación de medicamentos con receta", e);
			return false;
		}
	}


}
