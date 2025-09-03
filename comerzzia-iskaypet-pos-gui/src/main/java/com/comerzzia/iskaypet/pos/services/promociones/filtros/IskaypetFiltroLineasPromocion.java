package com.comerzzia.iskaypet.pos.services.promociones.filtros;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@Scope("prototype")
public class IskaypetFiltroLineasPromocion extends FiltroLineasPromocion {

    @Autowired
    private SesionPromociones sesionPromociones;

    public LineasAplicablesPromoBean getLineasAplicablesGrupo(GrupoComponentePromoBean filtro, boolean filtroLineasCantidadDecimales) {
        LineasAplicablesPromoBean aplicables = createLineasAplicables();
        aplicables.setFiltroLineasCantidadDecimales(filtroLineasCantidadDecimales);

        // Si el filtro no define reglas ni grupos, asumimos que la promoción puede aplicarse sobre todas las líneas del
        // documento
        if (filtro.isVacio()) {
            aplicables.setLineasAplicables(documento.getLineasDocumentoPromocionable());
        } else { // Si tenemos reglas y grupos, obtenemos las líneas aplicables
            List<LineaDocumentoPromocionable> lineasAplicables = getLineasAplicablesGrupo(filtro, documento.getLineasDocumentoPromocionable());
            aplicables.setLineasAplicables(lineasAplicables);
        }

        return aplicables;
    }


    public LineasAplicablesPromoBean getLineasAplicablesExcluyendoLineasConPromocionesAplicadas(LineasAplicablesPromoBean lineasAplicables, boolean isExcluirSiExistenPromocionesLinea) {
        log.debug("getLineasAplicablesExcluyendoLineasConPromocionesAplicadas() - Excluir si existen promociones en línea: " + isExcluirSiExistenPromocionesLinea);
        if (!isExcluirSiExistenPromocionesLinea) {
            log.debug("getLineasAplicablesExcluyendoLineasConPromocionesAplicadas() - Se devuelve las lineasAplicables originales");
            return lineasAplicables;
        }

        LineasAplicablesPromoBean aplicables = createLineasAplicables();
        List<LineaDocumentoPromocionable> lstLineasCandidatas = lineasAplicables.getLineasAplicables();
        List<LineaDocumentoPromocionable> lstlineasAplicables = new ArrayList<>();

        for (LineaDocumentoPromocionable linea : lstLineasCandidatas) {
            boolean sinPromociones = linea.getPromociones().isEmpty();
            boolean lineaConDescuentoAplicado = linea instanceof IskaypetLineaTicket && BigDecimalUtil.isIgualACero(((IskaypetLineaTicket) linea).getImporteDescuento());
            boolean noTienePromocionesAplicadasLinea = !contieneLineaPromocionesDeLineaAplicadas(linea);

            if (sinPromociones || (lineaConDescuentoAplicado && noTienePromocionesAplicadasLinea)) {
                log.debug("getLineasAplicablesExcluyendoLineasConPromocionesAplicadas() - Linea aplicable" + linea.getIdLinea() + " no tiene promociones");
                lstlineasAplicables.add(linea);
            }
        }

        aplicables.setLineasAplicables(lstlineasAplicables);
        log.debug("getLineasAplicablesExcluyendoLineasConPromocionesAplicadas() - Se devuelve las lineasAplicables filtradas, tamaño: " + aplicables.getLineasAplicables().size());
        return aplicables;
    }

    private boolean contieneLineaPromocionesDeLineaAplicadas(LineaDocumentoPromocionable linea) {
        log.debug("contieneLineaPromocionesDeLinea() - Comprobando si la linea " + linea.getIdLinea() + " contiene promociones de linea aplicadas");
        for (PromocionLineaTicket promoLinea : linea.getPromociones()) {
            BigDecimal dtoAplicado = promoLinea.getImporteTotalDtoMenosIngreso().add(promoLinea.getImporteTotalDtoMenosMargen());
            if (BigDecimalUtil.isIgualACero(dtoAplicado)) {
                log.debug("contieneLineaPromocionesDeLinea() - La linea " + linea.getIdLinea() + " con promocion " + promoLinea.getIdPromocion() + " no tiene importe de descuento aplicado");
                continue;
            }

            Promocion promocion = sesionPromociones.getPromocionActiva(promoLinea.getIdPromocion());
            if (promocion != null && promocion.isAplicacionLinea()) {
                log.debug("contieneLineaPromocionesDeLinea() - La linea " + linea.getIdLinea() + " contiene promociones de linea aplicadas");
                return true;
            }
        }
        log.debug("contieneLineaPromocionesDeLinea() - La linea " + linea.getIdLinea() + " no tiene promociones de linea aplicadas");
        return false;
    }
}
