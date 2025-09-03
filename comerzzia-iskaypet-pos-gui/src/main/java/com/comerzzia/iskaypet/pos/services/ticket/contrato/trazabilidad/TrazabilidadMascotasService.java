package com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticuloKey;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPetsKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticulo;
import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticuloMapper;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPets;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPetsMapper;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.detalles.DetallesTrazabilidadDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdent;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdentExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdentExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdentMapper;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class TrazabilidadMascotasService {

	public static final String X_REGION_ALMACEN = "X_REGION_ALMACEN";
	public static final String X_POS_IDENTIFICADOR_ANIMAL = "X.POS_IDENTIFICADOR_ANIMAL";
	public static final String X_POS_IDENTIFICADOR_REQUIERE_CLIENTE = "X.POS_IDENTIFICADOR_REQUIERE_CLIENTE";

	private static final Logger log = Logger.getLogger(TrazabilidadMascotasService.class);

	@Autowired
	protected Sesion sesion;

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	protected DetalleAnexoArticuloMapper anexoArticuloMapper;
	@Autowired
	protected DetailPetsMapper detailPetsMapper;
	@Autowired
	protected ItemsPetsIdentMapper identsMapper;

	/* ######################################################################################################################### */
	/* ###################### GAP 172 TRAZABILIDAD ANIMALES #################################################################### */
	/* ######################################################################################################################### */

	public DetalleAnexoArticulo consultarDetallesAnexo(ArticuloBean articulo) {
		log.debug("consultarDetallesAnexos() - Consultando detalles anexo a los articulos mascotas");

		DetalleAnexoArticuloKey detallesAnexosKey = new DetalleAnexoArticuloKey();
		detallesAnexosKey.setCodart(articulo.getCodArticulo());
		detallesAnexosKey.setUidActividad(sesion.getAplicacion().getUidActividad());

		DetalleAnexoArticulo detalleAnexoArticulo = anexoArticuloMapper.selectByPrimaryKey(detallesAnexosKey);

		log.debug("consultarDetallesAnexos() - Se termina consulta de detalles anexo  para el articulo " + articulo.getCodArticulo());
		return detalleAnexoArticulo;
	}

	public DetailPets consultarTrazabilidad(LineaTicket linea) {
		log.debug("consultarTrazabilidad() - Consultando trazabilidad de las mascotas para el articulo " + linea.getCodArticulo() + " - Con Importe: " + linea.getImporteTotalConDtoAsString());
		log.info("consultarTrazabilidad() - Consultando trazabilidad de las mascotas para el articulo " + linea.getCodArticulo() + " - Con Importe: " + linea.getImporteTotalConDtoAsString());

		// Verificamos si el articulo tiene anexo de mascotas
		DetalleAnexoArticulo detallesAnexo = consultarDetallesAnexo(linea.getArticulo());
		if (detallesAnexo == null) {
			return null;
		}

		DetailPets detailPets = consultarTrazabilidad(linea, detallesAnexo);
		log.debug("consultarTrazabilidad() - Se ha encontrado trazabilidad para las mascotas con codigo de articulo " + linea.getCodArticulo());
		return detailPets;
	}

	public DetailPets consultarTrazabilidad(LineaTicket linea, DetalleAnexoArticulo detallesAnexo) {
		log.debug("consultarTrazabilidad() - Consultando trazabilidad de las mascotas para el articulo " + linea.getCodArticulo());

		// Verificamos si la región esta configurada
		String codRegion = getRegion();
		if (StringUtils.isBlank(codRegion)) {
			return null;
		}

		DetailPetsKey key = new DetailPetsKey();
		key.setActivityId(sesion.getAplicacion().getUidActividad());
		key.setTipoMaterial(detallesAnexo.getTipoMaterial());
		key.setGrupoArticulos(linea.getArticulo().getCodFamilia());
		key.setValorCaracteristica(detallesAnexo.getValorCaracteristica());
		key.setPais(sesion.getAplicacion().getTienda().getCliente().getCodpais().toUpperCase());
		key.setRegion(codRegion);

		DetailPets detailPets = detailPetsMapper.selectByPrimaryKey(key);
		log.debug("consultarTrazabilidad() - Se ha encontrado trazabilidad para las mascotas con codigo de articulo " + linea.getCodArticulo());
		return detailPets;
	}

	public boolean cargarTrazabilidadLinea(LineaTicket linea, DetailPets trazabilidad) {

		log.debug("consultarTrazabilidad() - Consultando trazabilidad de las mascotas...");

		if (linea instanceof IskaypetLineaTicket) {
			// Si existe trazaibilidad se asigna a la linea
			((IskaypetLineaTicket) linea).setDetallesTrazabilidad(new ArrayList<>());
			// Se indica si requiere contrato o identificación
			((IskaypetLineaTicket) linea).setRequiereContrato(trazabilidad.getContratoAsBoolean());
			((IskaypetLineaTicket) linea).setRequiereIdentificacion(trazabilidad.getIdAnillaAsBoolean() || trazabilidad.getIdChipAsBoolean() || trazabilidad.getIdCitesAsBoolean());
		}

		log.debug("consultarTrazabilidad() - Se ha encontrado trazabilidad para las mascotas con codigo de articulo " + linea.getCodArticulo());
		return true;
	}

	public List<IskaypetLineaTicket> getLineasRequiereTrazabilidadNoAsignadas(List<IskaypetLineaTicket> lineas) {

		log.debug("consultarTrazabilidad() - Comprobando si requieren trazabilidad las mascotas...");
		List<IskaypetLineaTicket> lineasSinTrazabilidad = new ArrayList<>();

		for (IskaypetLineaTicket linea : lineas) {

			DetailPets detailPets = consultarTrazabilidad(linea);

			// Se comprueba si la linea requiere trazabilidad (alguno de los identificadores es S) y si la linea tiene trazabilidad asignada
			if (detailPets != null && linea.isRequiereIdentificacion()
					&& (detailPets.getIdChipAsBoolean() || detailPets.getIdAnillaAsBoolean() || detailPets.getIdCitesAsBoolean())
					&& (linea.getDetallesTrazabilidad() != null && linea.getDetallesTrazabilidad().isEmpty())) {
					// Se añaden las lineas que requieren trazabilidad y no tienen trazabilidad asignada
					lineasSinTrazabilidad.add(linea);
			}
		}
		return lineasSinTrazabilidad;
	}

	public List<LineaTicket> consultarLineasRequierenTrazabilidad(List<LineaTicket> listLines) {
		log.debug("consultarTrazabilidad() - Control lineas con trazabilidad de mascotas.");
		List<LineaTicket> lineasControlTrazabilidad = listLines.stream()
				.filter(linea -> ((IskaypetLineaTicket) linea).getDetallesTrazabilidad() != null && ((IskaypetLineaTicket) linea).isRequiereIdentificacion())
				.collect(Collectors.toList());
		log.debug("consultarTrazabilidad() - Se han encontrado " + lineasControlTrazabilidad.size() + " lineas con trazabilidad de mascotas.");
		return lineasControlTrazabilidad;

	}

	public String getRegion() {
		log.debug("getRegion() - Obteniendo región del almacén...");
		String codRegion = variablesServices.getVariableAsString(X_REGION_ALMACEN);
		log.debug("getRegion() - Región del almacén: " + codRegion);
        return codRegion;
    }

	public void validarConfiguracion(List<LineaTicket> listLines, TicketManager ticketManager)
			throws LineaTicketException {

		log.debug("validarConfiguracion() - Validando configuración de trazabilidad.");
		// Comprobar la región configurada sino lo está no podemos insertar las lineas
		// al ticket y las eliminamos
		String codRegion = getRegion();
		if (StringUtils.isBlank(codRegion)) {
			String msgError = "No existe una región configurada para este almacén";
			eliminarLineasTicket(listLines, ticketManager, msgError);
		}

		// Comprobar si requiere trazabilidad la cantidad de lineas introducidas, sino
		// debemos eliminarlas
		for (LineaTicket lineaTicket : listLines) {

			DetailPets trazabilidad = consultarTrazabilidad(lineaTicket);
			if (trazabilidad == null) {
				String msgError = "No existe trazabildad para las mascotas en este almacén";
				eliminarLineasTicket(listLines, ticketManager, msgError);
			}
			cargarTrazabilidadLinea(lineaTicket, trazabilidad);
		}
	}
	/**
	 * Elimina las lineas del ticket que no cumplen con las condiciones
	 * @param listLines
	 * @param ticketManager
	 * @param msgError
	 * @throws LineaTicketException
	 */
	public void eliminarLineasTicket(List<LineaTicket> listLines, TicketManager ticketManager, String msgError)	throws LineaTicketException {
		log.debug("eliminarLineasTicket() - Eliminando lineas del ticket...");
		for (LineaTicket linea : listLines) {
			ticketManager.eliminarLineaArticulo(linea.getIdLinea());
		}
		throw new LineaTicketException(I18N.getTexto(msgError));
	}
	
	public List<ItemsPetsIdent> getIdentificadores (IskaypetLineaTicket iskaypetLineaTicket){
		log.debug("getIdentificadores() - Obteniendo identificadores del articulos "+iskaypetLineaTicket.getCodArticulo());
		ItemsPetsIdentExample example = new ItemsPetsIdentExample();
		Criteria criteria = example.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andCodalmEqualTo(sesion.getAplicacion().getCodAlmacen());
		criteria.andCodartEqualTo(iskaypetLineaTicket.getCodArticulo());
		criteria.andDesglose1EqualTo(iskaypetLineaTicket.getDesglose1());
		criteria.andDesglose2EqualTo(iskaypetLineaTicket.getDesglose2());
		criteria.andActivoEqualTo("N");
		
		List<ItemsPetsIdent> lstIdents = identsMapper.selectByExample(example);
		if(lstIdents.isEmpty()) {
			log.info("getIdentificadores() - No exiten identificadores para el artículo " + iskaypetLineaTicket.getCodArticulo() + " .Se deberá introducir manualmente.");
		}
		return lstIdents ;
	}
	
	public void cambiarEstadoIdentificador(IskaypetLineaTicket linea, List<DetallesTrazabilidadDto> listIdentificador, String estado) {
		ItemsPetsIdentExample example = new ItemsPetsIdentExample();
		Criteria criteria = example.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andCodalmEqualTo(sesion.getAplicacion().getCodAlmacen());
		criteria.andCodartEqualTo(linea.getCodArticulo());
		criteria.andDesglose1EqualTo(linea.getDesglose1());
		criteria.andDesglose2EqualTo(linea.getDesglose2());
		ItemsPetsIdent identificadorUsado = new ItemsPetsIdent();
		for (DetallesTrazabilidadDto identificadores : listIdentificador) {
			if ("CHIP".equalsIgnoreCase(identificadores.getFldName())) {
				if (StringUtils.isNotBlank(identificadores.getIdentificacionTrazabilidad())) {
					identificadorUsado.setChip(identificadores.getIdentificacionTrazabilidad());
					criteria.andChipEqualTo(identificadores.getIdentificacionTrazabilidad());
				}
			}
			if ("ANILLA".equalsIgnoreCase(identificadores.getFldName())) {
				if (StringUtils.isNotBlank(identificadores.getIdentificacionTrazabilidad())) {
					identificadorUsado.setAnilla(identificadores.getIdentificacionTrazabilidad());
					criteria.andAnillaEqualTo(identificadores.getIdentificacionTrazabilidad());
				}
			}
			if ("CITES".equalsIgnoreCase(identificadores.getFldName())) {
				if (StringUtils.isNotBlank(identificadores.getIdentificacionTrazabilidad())) {
					identificadorUsado.setCites(identificadores.getIdentificacionTrazabilidad());
					criteria.andCitesEqualTo(identificadores.getIdentificacionTrazabilidad());
				}
			}
		}
		identificadorUsado.setCodart(linea.getCodArticulo());
		identificadorUsado.setDesglose1(linea.getDesglose1());
		identificadorUsado.setDesglose2(linea.getDesglose2());
		identificadorUsado.setActivo(estado);

		identsMapper.updateByExampleSelective(identificadorUsado, example);
	}

	@SuppressWarnings("unchecked")
	public void actualizaEstadoIdentificador(TicketManager ticketManager) {
		String estado = "S";
		if(ticketManager.isEsDevolucion()) {
			estado = "N";
		}
		for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>)ticketManager.getTicket().getLineas()) {
			if(linea.getDetallesTrazabilidad() != null && !linea.getDetallesTrazabilidad().isEmpty()) {
				cambiarEstadoIdentificador(linea, linea.getDetallesTrazabilidad(), estado);
			}
		}
	}

	public boolean requiereClienteIdentificado(LineaTicket linea) {

		// Si no tiene trazabilidad no se requiere cliente identificado
		DetailPets detailPets = consultarTrazabilidad(linea);
		if (detailPets == null) {
			log.debug("requiereClienteIdentificado() - No se ha encontrado trazabilidad para la mascota con codigo de articulo " + linea.getCodArticulo());
			return false;
		}

		if (detailPets.getContratoAsBoolean() || detailPets.getIdAnillaAsBoolean() || detailPets.getIdChipAsBoolean() || detailPets.getIdCitesAsBoolean()) {
			log.debug("requiereClienteIdentificado() - Se requiere cliente identificado para la mascota con codigo de articulo " + linea.getCodArticulo());
			return true;
		}

		log.debug("requiereClienteIdentificado() - No se requiere cliente identificado para la mascota con codigo de articulo " + linea.getCodArticulo());
		return false;
	}
}
