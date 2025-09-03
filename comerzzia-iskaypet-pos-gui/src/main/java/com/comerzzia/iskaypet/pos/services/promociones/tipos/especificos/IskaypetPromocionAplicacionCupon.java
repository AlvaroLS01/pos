package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;
import com.comerzzia.iskaypet.pos.services.promociones.filtros.IskaypetFiltroLineasPromocion;
import com.comerzzia.iskaypet.pos.services.promociones.utils.PromocionConstants;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.services.cupones.CuponesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionAplicacionCupon;
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
public class IskaypetPromocionAplicacionCupon extends PromocionAplicacionCupon {

    @Autowired
    private CuponesServices cuponesServices;

    private boolean excluirSiExistenPromocionesLinea;

    @Override
    public boolean aplicarPromocion(DocumentoPromocionable documento) {
        if (StringUtils.isBlank(getCupon())) {
            return false;
        }
        log.trace("aplicarPromocion() - " + this);

        List<CustomerCouponDTO> coupons = documento.getCouponsAppliyingPromotion(getIdPromocion());
        if (coupons != null) {
            int i = 0;
            for (CustomerCouponDTO coupon : coupons) {
                log.debug("aplicarPromocion() - Aplicando cupón " + i + " de " + coupons.size() + ": " + coupon.getCouponCode());
                this.setCustomerCoupon(coupon);
                // Obtenemos las líneas aplicables según el filtro configurado
                IskaypetFiltroLineasPromocion filtroLineas = (IskaypetFiltroLineasPromocion) createFiltroLineasPromocion(documento);
                filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
                LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
                if (lineasAplicables.isEmpty()) {
                    log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
                    continue;
                }

                lineasAplicables = filtroLineas.getLineasAplicablesGrupo(lineasAplicacion);
                if (lineasAplicables.isEmpty()) {
                    log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
                    continue;
                }

                lineasAplicables = filtroLineas.getLineasAplicablesExcluyendoLineasConPromocionesAplicadas(lineasAplicables, isExcluirSiExistenPromocionesLinea());
                if (lineasAplicables.isEmpty()) {
                    log.trace("aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento.");
                    continue;
                }


                // Obtenemos el importe de descuento
                BigDecimal valorConfigurado = customerCoupon.getBalance();
                if (valorConfigurado == null) {
                    valorConfigurado = cuponesServices.getImporteDescuentoCupon(customerCoupon.getCouponCode());
                }

                if (valorConfigurado == null) {
                    log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar porque no se ha podido leer el importe del cupón.");
                    continue;
                }
                BigDecimal importeLineasConDto = lineasAplicables.getImporteLineasConDto();
                BigDecimal totalesPromocionesCabecera = BigDecimal.ZERO;
                for (LineaDocumentoPromocionable linea : documento.getLineasDocumentoPromocionable()) {
                    totalesPromocionesCabecera = totalesPromocionesCabecera.add(((LineaTicket) linea).getImporteTotalPromocionesMenosIngreso());
                }

                // Aplicamos la promoción sobre el ticket
                PromocionTicket promocionTicket = createPromocionTicket(customerCoupon);
                BigDecimal importePromocion = BigDecimal.ZERO;

                BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
                if (BigDecimalUtil.isMayorOrIgual(valorConfigurado, importeDespuesPromociones)) {
                    importePromocion = importeDespuesPromociones;
                } else {
                    importePromocion = valorConfigurado;
                }

                promocionTicket.setImporteTotalAhorro(importePromocion);
                documento.addPromocion(promocionTicket);

                BigDecimal porcentajeDescuento = BigDecimal.ZERO;
                importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
                if (!BigDecimalUtil.isIgualACero(importePromocion)) {
                    porcentajeDescuento = BigDecimalUtil.getTantoPorCiento(importeDespuesPromociones, importePromocion);
                }

                lineasAplicables.aplicaDescuentoPorcentajeGeneral(promocionTicket, porcentajeDescuento);

                CuponAplicadoTicket cupon = documento.getCuponAplicado(customerCoupon.getCouponCode());
                if (cupon != null) {
                    cupon.setTextoPromocion(promocionTicket.getTextoPromocion());
                }
                i++;
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
            lineasAplicacion = new GrupoComponentePromoBean(xmlPromocion.getNodo(PromocionConstants.LINEAS_APLICACION));

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

    public boolean isExcluirSiExistenPromocionesLinea() {
        return excluirSiExistenPromocionesLinea;
    }

    public void setExcluirSiExistenPromocionesLinea(boolean excluirSiExistenPromocionesLinea) {
        this.excluirSiExistenPromocionesLinea = excluirSiExistenPromocionesLinea;
    }
}
