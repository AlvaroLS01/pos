package com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.ventas.tipooperacion.TipoOperacionVenta;
import com.comerzzia.bimbaylola.pos.persistence.ventas.tipooperacion.TipoOperacionVentaExample;
import com.comerzzia.bimbaylola.pos.persistence.ventas.tipooperacion.TipoOperacionVentaKey;
import com.comerzzia.bimbaylola.pos.persistence.ventas.tipooperacion.TipoOperacionVentaMapper;
import com.comerzzia.bimbaylola.pos.services.core.documentos.propiedades.ByLPropiedadesDocumentoService;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.PropiedadesDocumento;
import com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.exceptions.TipoOperacionVentaException;
import com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.exceptions.TipoOperacionVentaNotFoundException;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;

@Service
public class TipoOperacionVentaService {

	protected static final Logger log = Logger.getLogger(TipoOperacionVentaService.class);

	protected static TipoOperacionVentaService instance;

	public static TipoOperacionVentaService get() {
		if (instance == null) {
			instance = new TipoOperacionVentaService();
		}
		return instance;
	}

	@Autowired
	protected TipoOperacionVentaMapper tipoOperacionVentaMapper;

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected ByLPropiedadesDocumentoService propiedadesDocumentoService;

	public Integer insertarTipoOperacionVenta(TipoOperacionVenta tipoOperacionVenta) {
		log.debug("insertarTipoOperacionVenta() - Ticket : " + tipoOperacionVenta.getUidTicket());
		return tipoOperacionVentaMapper.insert(tipoOperacionVenta);
	}

	@SuppressWarnings("rawtypes")
	public Integer insertarTipoOperacionVenta(Ticket<ILineaTicket, IPagoTicket, ICabeceraTicket> ticket) {
		log.debug("insertarTipoOperacionVenta() - Ticket : " + ticket.getUidTicket());
		TipoOperacionVenta tipoOperacionVenta = new TipoOperacionVenta();
		tipoOperacionVenta.setUidActividad(ticket.getUidActividad());
		tipoOperacionVenta.setUidDiarioCaja(ticket.getUidDiarioCaja());
		tipoOperacionVenta.setUidTicket(ticket.getUidTicket());

		ByLCabeceraTicket cabeceraTicket = (ByLCabeceraTicket) ticket.getCabecera();
		
		PropiedadesDocumento propiedadesDocumento = null;
		if (cabeceraTicket.getPropiedadesDocumentoOrigen() != null && !ticket.getCabecera().getDatosDocOrigen().getCodTipoDoc().equals("RE")) {
			propiedadesDocumento = cabeceraTicket.getPropiedadesDocumentoOrigen();
		}
		else {
			propiedadesDocumento = cabeceraTicket.getPropiedadesDocumento();
		}
		
		if(StringUtils.isNotBlank(propiedadesDocumento.getTipoDocumento())) {
			tipoOperacionVenta.setTipoDocumento(propiedadesDocumento.getTipoDocumento());
		}
		if(StringUtils.isNotBlank(propiedadesDocumento.getSignoDocumento())) {
			tipoOperacionVenta.setSignoDocumento(propiedadesDocumento.getSignoDocumento());
		}
		
		tipoOperacionVenta.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());

		return tipoOperacionVentaMapper.insert(tipoOperacionVenta);
	}

	public TipoOperacionVenta consultarTipoOperacionVenta(String uidDiarioCaja, String uidTicket) throws TipoOperacionVentaNotFoundException {
		log.debug("consultarTipoOperacionVenta() - Ticket : " + uidTicket);
		TipoOperacionVentaKey tipoOperacionVentaKey = new TipoOperacionVentaKey();
		tipoOperacionVentaKey.setUidActividad(sesion.getAplicacion().getUidActividad());
		tipoOperacionVentaKey.setUidDiarioCaja(uidDiarioCaja);
		tipoOperacionVentaKey.setUidTicket(uidTicket);
		TipoOperacionVenta tipoOperacionVenta = tipoOperacionVentaMapper.selectByPrimaryKey(tipoOperacionVentaKey);

		if (tipoOperacionVenta == null) {
			throw new TipoOperacionVentaNotFoundException();
		}
		return tipoOperacionVenta;
	}

	@SuppressWarnings("rawtypes")
	public TipoOperacionVenta consultarTipoOperacionVenta(Ticket<ILineaTicket, IPagoTicket, ICabeceraTicket> ticket) throws TipoOperacionVentaNotFoundException, TipoOperacionVentaException {
		log.debug("consultarTipoOperacionVenta() - Ticket : " + ticket.getUidTicket());

		if (StringUtils.isBlank(ticket.getUidTicket()) || StringUtils.isBlank(ticket.getUidDiarioCaja())) {
			throw new TipoOperacionVentaException("El uidTicket y UidDiarioCaja deben tener valor");
		}

		TipoOperacionVentaKey tipoOperacionVentaKey = new TipoOperacionVentaKey();
		tipoOperacionVentaKey.setUidActividad(sesion.getAplicacion().getUidActividad());
		tipoOperacionVentaKey.setUidDiarioCaja(ticket.getUidDiarioCaja());
		tipoOperacionVentaKey.setUidTicket(ticket.getUidTicket());
		TipoOperacionVenta tipoOperacionVenta = tipoOperacionVentaMapper.selectByPrimaryKey(tipoOperacionVentaKey);

		if (tipoOperacionVenta == null) {
			throw new TipoOperacionVentaNotFoundException();
		}
		return tipoOperacionVenta;
	}

	public List<TipoOperacionVenta> consultarTipoOperacionVentaByExample(TipoOperacionVentaExample tipoOperacionVentaExample) {
		return tipoOperacionVentaMapper.selectByExample(tipoOperacionVentaExample);
	}

	public List<TipoOperacionVenta> filterTipoOperacionesBy(Caja caja, String tipoDocumento, String signoDocumento, long idDocumento, Boolean idDocumentoEqualTo, Boolean paisTieneTr) {
		TipoOperacionVentaExample example = new TipoOperacionVentaExample();
		TipoOperacionVentaExample.Criteria criteria = example.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());

		if (StringUtils.isNotBlank(tipoDocumento)) {
			criteria.andTipoDocumentoEqualTo(tipoDocumento);
		}
		if (StringUtils.isNotBlank(signoDocumento)) {
			criteria.andSignoDocumentoEqualTo(signoDocumento);
		}
		
		if(paisTieneTr) {
			if (idDocumentoEqualTo) {
				criteria.andIdTipoDocumentoEqualTo(idDocumento);
			}
			else {
				criteria.andIdTipoDocumentoNotEqualTo(idDocumento);
			}
		}
		
		return consultarTipoOperacionVentaByExample(example);
	}
	
	public Integer salvarTipoOperacionVenta(TipoOperacionVenta tipoOperacionVenta) {
		log.debug("salvarTipoOperacionVenta() - Ticket : " + tipoOperacionVenta.getUidTicket());
		return tipoOperacionVentaMapper.updateByPrimaryKey(tipoOperacionVenta);
	}

	public Integer eliminarTipoOperacionVenta(String uidDiarioCaja, String uidTicket) throws TipoOperacionVentaNotFoundException {
		log.debug("eliminarTipoOperacionVenta() - Ticket : " + uidTicket);
		TipoOperacionVenta tipoOperacionVenta = consultarTipoOperacionVenta(uidDiarioCaja, uidTicket);
		return tipoOperacionVentaMapper.deleteByPrimaryKey(tipoOperacionVenta);
	}

	public Integer eliminarTipoOperacionVenta(TipoOperacionVentaKey tipoOperacionVenta) {
		log.debug("eliminarTipoOperacionVenta() - Ticket : " + tipoOperacionVenta.getUidTicket());
		return tipoOperacionVentaMapper.deleteByPrimaryKey(tipoOperacionVenta);
	}

	@SuppressWarnings("rawtypes")
	public Integer eliminarTipoOperacionVenta(Ticket<ILineaTicket, IPagoTicket, ICabeceraTicket> ticket) throws TipoOperacionVentaNotFoundException, TipoOperacionVentaException {
		log.debug("eliminarTipoOperacionVenta() - Ticket : " + ticket.getUidTicket());

		if (StringUtils.isBlank(ticket.getUidTicket()) || StringUtils.isBlank(ticket.getUidDiarioCaja())) {
			throw new TipoOperacionVentaException("El uidTicket y UidDiarioCaja deben tener valor");
		}

		TipoOperacionVenta tipoOperacionVenta = consultarTipoOperacionVenta(ticket.getUidDiarioCaja(), ticket.getUidTicket());
		return tipoOperacionVentaMapper.deleteByPrimaryKey(tipoOperacionVenta);
	}
}
