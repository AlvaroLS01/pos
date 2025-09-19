package com.comerzzia.bimbaylola.pos.services.core.sesion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.core.impuestos.grupos.ByLGrupoImpuestosService;
import com.comerzzia.pos.persistence.core.impuestos.grupos.GrupoImpuestoBean;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosNotFoundException;
import com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosServiceException;
import com.comerzzia.pos.services.core.impuestos.porcentajes.PorcentajesImpuestosService;
import com.comerzzia.pos.services.core.impuestos.porcentajes.PorcentajesImpuestosServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
public class ByLSesionImpuestos {

    protected static final Logger log = Logger.getLogger(ByLSesionImpuestos.class.getName());

    protected GrupoImpuestoBean grupoImpuestos;
    protected final Map<Long, Map<String, PorcentajeImpuestoBean>> porcentajes;
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected ByLGrupoImpuestosService grupoImpuestosService;
    @Autowired
    protected PorcentajesImpuestosService porcentajesImpuestosService;
    
    public ByLSesionImpuestos(){
        porcentajes = new HashMap<>();
    }
    
    public void init(int idGrupoImpuestos) throws SesionInitException {
		try {
			log.info("init() - Consultando impuestos...");

			// Consultamos grupo de impuestos
			GrupoImpuestoBean grupoImpuestos = grupoImpuestosService.consultarGrupoImpuestosID(idGrupoImpuestos);
			this.grupoImpuestos = grupoImpuestos;

			// Consultamos los porcentajes de impuestos vigentes
			List<PorcentajeImpuestoBean> porcentajesVigentes = porcentajesImpuestosService.consultarPorcentajesImpuestosActual(grupoImpuestos.getIdGrupoImpuestos());
			for (PorcentajeImpuestoBean porcentaje : porcentajesVigentes) {
				Long idTratamiento = porcentaje.getIdTratamientoImpuestos();
				Map<String, PorcentajeImpuestoBean> porcentajesTratamiento = porcentajes.get(idTratamiento);
				if (porcentajesTratamiento == null) {
					porcentajesTratamiento = new HashMap<>();
					porcentajes.put(idTratamiento, porcentajesTratamiento);
				}
				porcentajesTratamiento.put(porcentaje.getCodImpuesto(), porcentaje);
			}
			log.info("init() - Porcentajes de impuestos vigentes cargados correctamente.");
		}
		catch (GrupoImpuestosNotFoundException | PorcentajesImpuestosServiceException | GrupoImpuestosServiceException ex) {
			log.error("init() - Error inicializando sesión de impuestos " + ex.getMessage(), ex);
			throw new SesionInitException(ex.getMessageI18N(), ex);
		}
	}


    public GrupoImpuestoBean getGrupoImpuestos() {
        return grupoImpuestos;
    }

    public Map<Long, Map<String, PorcentajeImpuestoBean>> getPorcentajes() {
        return porcentajes;
    }

    public PorcentajeImpuestoBean getPorcentaje(Long idTratImpuestos, String codImpuesto) {
        Map<String, PorcentajeImpuestoBean> porcentajesCliente = porcentajes.get(idTratImpuestos);
        if (porcentajesCliente == null){
            return null;
        }
        return porcentajesCliente.get(codImpuesto);
    }

    /** Devuelve los impuestos para según el tipo de impuestos y precio indicado. El tratamiento de impuestos
     * se toma del cliente del ticket en sesión.
     * @param codImpuesto
     * @param precio
     * @return 
     */
    public BigDecimal getImpuestos(String codImpuesto, Long idTratImpuestos, BigDecimal precio){
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        return BigDecimalUtil.porcentaje(precio, porcentajeImpuesto.getPorcentajeMasRecargo());
    }

    /** Devuelve el precio total redondeado a dos decimales (con impuestos) del precio venta indicado.
     * @param codImpuesto
     * @param precio
     * @return 
     */
    public BigDecimal getPrecioTotal(String codImpuesto, Long idTratImpuestos, BigDecimal precio){
        return BigDecimalUtil.redondear(getImpuestos(codImpuesto, idTratImpuestos, precio).add(precio));
    }

    
    /** Devuelve el precio venta redondeado a cuatro decimales (con impuestos) del precio total indicado.
     * @param codImpuesto
     * @param precioTotal
     * @return 
     */
    public BigDecimal getPrecioVenta(String codImpuesto, Long idTratImpuestos, BigDecimal precioTotal){
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        return BigDecimalUtil.getAntesDePorcentaje(precioTotal, porcentajeImpuesto.getPorcentajeMasRecargo());
    }
    
    /** Devuelve el precio venta redondeado a cuatro decimales (con impuestos) del precio total indicado.
     * @param codImpuesto
     * @param precioTotal
     * @param idTratImpuestos 
     * @return 
     */
    public BigDecimal getPrecioSinImpuestos(String codImpuesto, BigDecimal precioTotal, Long idTratImpuestos){
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        return BigDecimalUtil.getAntesDePorcentaje(precioTotal, porcentajeImpuesto.getPorcentajeMasRecargo());
    }

    /** Devuelve los impuestos para según el tipo de impuestos y precio indicado como la suma del porcentaje de impuesto
     * más el porcentaje de recargo. El tratamiento de impuestos se toma del cliente del ticket en sesión.
     * @param codImpuesto
     * @param precio
     * @return 
     */
    public BigDecimal getImpuestosProfesional(String codImpuesto, Long idTratImpuestos, BigDecimal precio) {
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        BigDecimal iva = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precio, porcentajeImpuesto.getPorcentaje()), 2);
        BigDecimal recargo = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precio, porcentajeImpuesto.getPorcentajeRecargo()), 2);
        return iva.add(recargo);
    }
    
    /**
     * Devuelve los impuestos para según el tipo de impuestos y precio indicado, sin el recargo. El tratamiento de impuestos
     * se toma del cliente del ticket en sesión.
     * @param codImpuesto
     * @param idTratImpuestos
     * @param precio
     * @return
     */
    public BigDecimal getPorcentajeIva(String codImpuesto, Long idTratImpuestos, BigDecimal precio) {
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        return BigDecimalUtil.porcentaje(precio, porcentajeImpuesto.getPorcentaje());
    }
    
    /**
     * Devuelve el recargo para según el tipo de impuestos y precio indicado. El tratamiento de impuestos
     * se toma del cliente del ticket en sesión.
     * @param codImpuesto
     * @param idTratImpuestos
     * @param precio
     * @return
     */
    public BigDecimal getPorcentajeRecargo(String codImpuesto, Long idTratImpuestos, BigDecimal precio) {
        PorcentajeImpuestoBean porcentajeImpuesto = getPorcentaje(idTratImpuestos, codImpuesto);
        return BigDecimalUtil.porcentaje(precio, porcentajeImpuesto.getPorcentajeRecargo());
    }
    
}
