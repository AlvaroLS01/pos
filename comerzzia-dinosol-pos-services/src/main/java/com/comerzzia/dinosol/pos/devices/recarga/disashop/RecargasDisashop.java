package com.comerzzia.dinosol.pos.devices.recarga.disashop;

import org.apache.log4j.Logger;

import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.librerias.disashop.client.DisashopClient;
import com.comerzzia.dinosol.librerias.disashop.client.conexion.DatosConexionDisashop;
import com.comerzzia.dinosol.librerias.disashop.client.devolucion.PeticionDevolucionDisashop;
import com.comerzzia.dinosol.librerias.disashop.client.devolucion.RespuestaDevolucionDisashop;
import com.comerzzia.dinosol.librerias.disashop.client.recarga.PeticionRecargaDisashop;
import com.comerzzia.dinosol.librerias.disashop.client.recarga.RespuestaRecargaDisashop;
import com.comerzzia.dinosol.pos.devices.recarga.DispositivoRecargaAbstract;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosPeticionCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosRespuestaCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosPeticionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosRespuestaRecargaDto;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;

public class RecargasDisashop extends DispositivoRecargaAbstract {
	
	private Logger log = Logger.getLogger(RecargasDisashop.class);
	
	private DisashopClient client;
	
	@Override
	protected void cargaConfiguracion(ConfiguracionDispositivo configuracion) throws DispositivoException {
		super.cargaConfiguracion(configuracion);
		try {
			XMLDocumentNode configNode = configuracion.getConfiguracionModelo().getConfigConexion();
			
			datosConexion = new DatosConexionDisashopDto();
		    					    
			datosConexion.setUrl(configNode.getNodo("url").getValue());
			datosConexion.setUsuario(configNode.getNodo("username").getValue());
			datosConexion.setPassword(configNode.getNodo("password").getValue());
			
			((DatosConexionDisashopDto) datosConexion).setEntidad(configNode.getNodo("entidad").getValue());
			((DatosConexionDisashopDto) datosConexion).setIdTerminal(configNode.getNodo("idTerminal").getValue());
			((DatosConexionDisashopDto) datosConexion).setIdCliente(configNode.getNodo("idCliente").getValue());
			
			log.debug("cargaConfiguracion() - Datos de conexión a Disashop: " + datosConexion.toString());
		}
		catch (Exception ex) {
			log.error("cargaConfiguracion() - Error recuperando la información de configuración de recargas móviles:" + ex.getMessage(), ex);
		}
		
		client = new DisashopClient();
	}

	@Override
	public DatosRespuestaRecargaDto recargar(DatosPeticionRecargaDto datosPeticionRecargaDto) throws DispositivoException {
		try {
			DatosConexionDisashop datosConexionDisashop = getDatosConexionDisashop();
			
			PeticionRecargaDisashop peticion = crearPeticionDisashop(datosPeticionRecargaDto);
			
			RespuestaRecargaDisashop responseDisashop = client.recargar(datosConexionDisashop, peticion);
			
			DatosRespuestaRecargaDisashopDto respuesta = new DatosRespuestaRecargaDisashopDto(responseDisashop);
			respuesta.setTelefono(datosPeticionRecargaDto.getTelefono());
			
			return respuesta;
		}
		catch (Exception e) {
			log.error("recargar() - Ha habido un error al realizar la operación de recarga: " + e.getMessage(), e);
			throw new DispositivoException(e);
		}
	}

	private PeticionRecargaDisashop crearPeticionDisashop(DatosPeticionRecargaDto datosPeticionRecargaDto) {
		PeticionRecargaDisashop peticion = new PeticionRecargaDisashop();
		peticion.setEntidad(((DatosConexionDisashopDto) datosConexion).getEntidad());
		peticion.setIdTerminal(((DatosConexionDisashopDto) datosConexion).getIdTerminal());
		peticion.setIdCliente(((DatosConexionDisashopDto) datosConexion).getIdCliente());
		peticion.setImporte(datosPeticionRecargaDto.getImporte());
		peticion.setProducto(datosPeticionRecargaDto.getNumReferenciaProveedor());
		peticion.setTelefono(datosPeticionRecargaDto.getTelefono());
		peticion.setReferenciaCliente(formatearCodTicket(datosPeticionRecargaDto));
		peticion.setVendedor(datosPeticionRecargaDto.getUsuario());
		return peticion;
	}

	private String formatearCodTicket(DatosPeticionRecargaDto datosPeticionRecargaDto) {
		return datosPeticionRecargaDto.getCodTicket().substring(5).replaceAll("-", "--");
	}

	private DatosConexionDisashop getDatosConexionDisashop() {
		DatosConexionDisashop datosConexionDisashop = new DatosConexionDisashop();
		datosConexionDisashop.setUrl(datosConexion.getUrl());
		datosConexionDisashop.setUsername(datosConexion.getUsuario());
		datosConexionDisashop.setPassword(datosConexion.getPassword());
		return datosConexionDisashop;
	}

	@Override
	public DatosRespuestaCancelacionRecargaDto cancelarRecarga(DatosPeticionCancelacionRecargaDto datosPeticionCancelacionRecarga) throws DispositivoException {
		try {
			DatosConexionDisashop datosConexionDisashop = getDatosConexionDisashop();
			
			PeticionDevolucionDisashop peticion = crearPeticionCancelacionDisashop(datosPeticionCancelacionRecarga);
			
			RespuestaDevolucionDisashop responseDisashop = client.devolver(datosConexionDisashop, peticion);
			
			return new DatosRespuestaCancelacionRecargaDisashopDto(responseDisashop);
		}
		catch (Exception e) {
			log.error("recargar() - Ha habido un error al realizar la operación de recarga: " + e.getMessage(), e);
			throw new DispositivoException(e);
		}
	}

	private PeticionDevolucionDisashop crearPeticionCancelacionDisashop(DatosPeticionCancelacionRecargaDto datosPeticionCancelacionRecarga) {
		PeticionDevolucionDisashop peticion = new PeticionDevolucionDisashop();
		peticion.setEntidad(((DatosConexionDisashopDto) datosConexion).getEntidad());
		peticion.setIdTerminal(((DatosConexionDisashopDto) datosConexion).getIdTerminal());
		peticion.setIdCliente(((DatosConexionDisashopDto) datosConexion).getIdCliente());
		peticion.setProducto(datosPeticionCancelacionRecarga.getOperador());
		peticion.setTelefono(datosPeticionCancelacionRecarga.getTelefono());
		peticion.setReferenciaProveedorRecarga(datosPeticionCancelacionRecarga.getNumReferenciaProveedor());
		peticion.setImporte(datosPeticionCancelacionRecarga.getImporte());
		peticion.setVendedor(datosPeticionCancelacionRecarga.getUsuario());
		peticion.setReferenciaCliente(datosPeticionCancelacionRecarga.getNumReferenciaProveedor());
		return peticion;
	}

}
