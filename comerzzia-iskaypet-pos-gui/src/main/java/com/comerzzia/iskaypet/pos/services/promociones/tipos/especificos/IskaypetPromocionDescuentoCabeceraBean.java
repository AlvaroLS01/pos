package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;
import com.comerzzia.iskaypet.pos.services.promociones.filtros.IskaypetFiltroLineasPromocion;
import com.comerzzia.iskaypet.pos.services.promociones.utils.PromocionConstants;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoCabeceraBean;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Primary
@Component
@Scope("prototype")
public class IskaypetPromocionDescuentoCabeceraBean extends PromocionDescuentoCabeceraBean {

    @Autowired
    private SesionImpuestos sesionImpuestos;

    private boolean excluirSiExistenPromocionesLinea;

    @Override
    public boolean aplicarPromocion(DocumentoPromocionable documento) {
        log.trace("aplicarPromocion() - " + this);
        // Obtenemos las líneas aplicables según el filtro configurado
        IskaypetFiltroLineasPromocion filtroLineas = (IskaypetFiltroLineasPromocion) createFiltroLineasPromocion(documento);
        filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
        LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
        if (lineasAplicables.isEmpty()) {
            log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
            return false;
        }

        if (lineasAplicacion != null) {
            lineasAplicables = filtroLineas.getLineasAplicablesGrupo(lineasAplicacion);
            if (lineasAplicables.isEmpty()) {
                log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
                return false;
            }
        }

        lineasAplicables = filtroLineas.getLineasAplicablesExcluyendoLineasConPromocionesAplicadas(lineasAplicables, isExcluirSiExistenPromocionesLinea());
        if (lineasAplicables.isEmpty()) {
            log.trace("aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento.");
            return false;
        }

        // Obtenemos el importe de descuento 
        BigDecimal valorConfigurado = getImporteDescuento(lineasAplicables);

        if (valorConfigurado == null && !isIVA()) {
            log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar porque no se ha definido un intervalo de importe aplicable compatible. Importe: " + lineasAplicables.getImporteLineasConDto());
            return false;
        }
        BigDecimal importeLineasConDto = lineasAplicables.getImporteLineasConDto();
        BigDecimal totalesPromocionesCabecera = BigDecimal.ZERO;
        for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
            totalesPromocionesCabecera = totalesPromocionesCabecera.add(((LineaTicket) linea).getImporteTotalPromocionesMenosIngreso());
        }


        // Aplicamos la promoción sobre el ticket
        PromocionTicket promocionTicket = createPromocionTicket(customerCoupon);
        BigDecimal importePromocion = BigDecimal.ZERO;

        // Si el tipo de filtro es importe 
        if (isImporte()) {
            BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
            if (BigDecimalUtil.isMayorOrIgual(valorConfigurado, importeDespuesPromociones)) {
                importePromocion = importeDespuesPromociones;
            } else {
                importePromocion = valorConfigurado;
            }


        }
        // Si el tipo de filtro es porcentaje
        else if (isPorcentaje()) {
            BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
            importePromocion = BigDecimalUtil.porcentaje(importeDespuesPromociones, valorConfigurado);

        } else if (isIVA()) {
            BigDecimal ahorroAplicable = BigDecimal.ZERO;
            for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
                ahorroAplicable = ahorroAplicable.add(BigDecimalUtil.redondear(sesionImpuestos.getImpuestos(((LineaTicket) linea).getCodImpuesto(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos(), ((LineaTicket) linea).getPrecioSinDto())));
                //	ahorroAplicable = ahorroAplicable.add(((LineaTicket) linea).getImporteTotalConDto().subtract(((LineaTicket) linea).getImporteConDto()));
            }
            BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(ahorroAplicable);

            if (BigDecimalUtil.isMayorOrIgual(ahorroAplicable, importeDespuesPromociones)) {
                importePromocion = importeDespuesPromociones;
            } else {
                importePromocion = ahorroAplicable;
            }
        }

        promocionTicket.setImporteTotalAhorro(importePromocion);
        documento.addPromocion(promocionTicket);

        if (isImporte()) {
            BigDecimal porcentajeDescuento = BigDecimal.ZERO;
            BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
            if (!BigDecimalUtil.isIgualACero(importePromocion)) {
                porcentajeDescuento = BigDecimalUtil.getTantoPorCiento(importeDespuesPromociones, importePromocion);
            }

            lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, porcentajeDescuento);
        } else if (isPorcentaje()) {
            lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, valorConfigurado);
        } else if (isIVA()) {
            BigDecimal porcentajeDescuento = BigDecimal.ZERO;
            BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
            if (!BigDecimalUtil.isIgualACero(importePromocion)) {
                porcentajeDescuento = BigDecimalUtil.getTantoPorCiento(importeDespuesPromociones, importePromocion);
            }

            lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, porcentajeDescuento);
        }

        if (customerCoupon != null) {
            CuponAplicadoTicket cupon = documento.getCuponAplicado(customerCoupon.getCouponCode());
            if (cupon != null) {
                cupon.setTextoPromocion(promocionTicket.getTextoPromocion());
            }
        }

        return true;
    }

    @Override
    public void leerDatosPromocion(byte[] datosPromocion) {
        try {
            XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
            condiciones = new CondicionPrincipalPromoBean(xmlPromocion.getNodo(PromocionConstants.CONDICION_LINEAS));
            aplicacion = new GrupoComponentePromoBean(xmlPromocion.getNodo(PromocionConstants.APLICACION));
            XMLDocumentNode nodoLineasAplicacion = xmlPromocion.getNodo(PromocionConstants.LINEAS_APLICACION, true);
            if (nodoLineasAplicacion != null) {
                lineasAplicacion = new GrupoComponentePromoBean(nodoLineasAplicacion);
            }

            Collections.sort(aplicacion.getReglas());

            String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();

            String textPromo = null;
            String textPromoDefault = null;
            List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos(PromocionConstants.TEXTO_PROMOCION);
            for (XMLDocumentNode textPromoNode : textPromoNodes) {
                String textPromoLanguageCode = textPromoNode.getAtributoValue(PromocionConstants.LANG, true);
                if (StringUtils.isNotBlank(textPromoLanguageCode)) {
                    if (textPromoLanguageCode.equals(storeLanguageCode)) {
                        textPromo = textPromoNode.getValue();
                        break;
                    }
                } else {
                    textPromoDefault = textPromoNode.getValue();
                }
            }

            if (StringUtils.isBlank(textPromo)) {
                textPromo = textPromoDefault;
            }
            setTextoPromocion(textPromo);

            XMLDocumentNode nodoExclusionLineas = xmlPromocion.getNodo(PromocionConstants.EXCLUIR_SI_EXISTEN_PROMOCIONES_LINEA, true);
            if (nodoExclusionLineas == null) {
                this.excluirSiExistenPromocionesLinea = false;
            } else {
                this.excluirSiExistenPromocionesLinea = nodoExclusionLineas.getValueAsBoolean();
            }

            try {
                tipoFiltro = xmlPromocion.getNodo(PromocionConstants.TIPO_FILTRO).getValue();
            } catch (XMLDocumentNodeNotFoundException ignore) {
            }
            if (tipoFiltro == null) {
                tipoFiltro = PromocionConstants.TIPO_FILTRO_IMPORTE;
            }
        } catch (XMLDocumentException e) {
            log.error("Error al leer los datos de la promoción de tipo descuento  cabecera: " + e.getMessage(), e);
        }
    }


    public boolean isIVA() {
        return tipoFiltro.equals("IVA");
    }

    public boolean isExcluirSiExistenPromocionesLinea() {
        return excluirSiExistenPromocionesLinea;
    }

    public void setExcluirSiExistenPromocionesLinea(boolean excluirSiExistenPromocionesLinea) {
        this.excluirSiExistenPromocionesLinea = excluirSiExistenPromocionesLinea;
    }
}
