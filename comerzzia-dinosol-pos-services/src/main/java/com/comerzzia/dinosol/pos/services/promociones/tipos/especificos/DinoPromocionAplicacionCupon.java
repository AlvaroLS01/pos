package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.promociones.filtros.DinoFiltroLineasPromocionCabecera;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.cupones.PromoAvailableByCoupon;
import com.comerzzia.pos.services.cupones.CuponesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionAplicacionCupon;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
@Scope("prototype")
@Primary
public class DinoPromocionAplicacionCupon extends PromocionAplicacionCupon implements PromoAvailableByCoupon {
	
	private Logger log = Logger.getLogger(DinoPromocionAplicacionCupon.class);
	
	@Autowired
	private CuponesServices cuponesServices;
	
	private CustomerCouponDTO customerCoupon;
	
	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
    	boolean resultado = false;
    	if(getCustomerCoupon() != null) {
			log.debug("aplicarPromocion() - Aplicando promoción " + getIdPromocion() + " del cupón " + getCustomerCoupon().getCouponCode());
	
			resultado = aplicarPromocionDino(documento);
    	}

		return resultado;
	}

	protected void resetPromocion() {
		this.codigoCupon = null;
		this.customerCoupon = null;
	}

	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new DinoFiltroLineasPromocionCabecera();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}
	
	private boolean aplicarPromocionDino(DocumentoPromocionable documento) {
		log.trace("aplicarPromocion() - " + this);
		// Obtenemos las líneas aplicables según el filtro configurado
		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
		LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
		if (lineasAplicables.isEmpty()) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

		lineasAplicables = filtroLineas.getLineasAplicablesGrupo(lineasAplicacion);
		if (lineasAplicables.isEmpty()) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

		// Obtenemos el importe de descuento
		BigDecimal valorConfigurado = null;
		if (customerCoupon != null) {
			valorConfigurado = customerCoupon.getBalance();			
			codigoCupon = customerCoupon.getCouponCode();
		}
		else {
			valorConfigurado = cuponesServices.getImporteDescuentoCupon(codigoCupon);
		}

		if (valorConfigurado == null) {
			log.warn(this + " aplicarPromocion() - La promoción no se puede aplicar porque no se ha podido leer el importe del cupón.");
			return false;
		}
		BigDecimal importeLineasConDto = lineasAplicables.getImporteLineasConDto();
		BigDecimal totalesPromocionesCabecera = BigDecimal.ZERO;
		for(LineaDocumentoPromocionable lineaAplicable : lineasAplicables.getLineasAplicables()) {
			for(PromocionLineaTicket promocionLinea : lineaAplicable.getPromociones()) {
				if(promocionLinea.isDescuentoMenosIngreso()) {
					totalesPromocionesCabecera = totalesPromocionesCabecera.add(promocionLinea.getImporteTotalDtoMenosIngreso());
				}
			}
		}

		// Aplicamos la promoción sobre el ticket
		PromocionTicket promocionTicket = createPromocionTicket(codigoCupon);
		BigDecimal importePromocion = BigDecimal.ZERO;

		BigDecimal importeDespuesPromociones = importeLineasConDto.subtract(totalesPromocionesCabecera);
		if (BigDecimalUtil.isMayorOrIgual(valorConfigurado, importeDespuesPromociones)) {
			importePromocion = importeDespuesPromociones;
		}
		else {
			importePromocion = valorConfigurado;
		}
		
		// Desactivamos la introducción parcial de cupones
		if(BigDecimalUtil.isMenor(importePromocion, valorConfigurado)) {
			log.warn("aplicarPromocionDino() - El cupón " + codigoCupon + " no se ha podido aplicar porque se tendría que aplicar de forma parcial");
			return false;
		}
		
		// Esta es la personalización. Si el importe de la promoción es 0, porque ya no haya más importe 
		// disponible para descontar no aplicamos la promoción y damos el cupón por no válido.
		if(BigDecimalUtil.isIgualACero(importePromocion)) {
			log.warn("aplicarPromocionDino() - El cupón " + codigoCupon + " no se ha podido aplicar porque no queda importe disponible del que descontar");
			return false;
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

		return true;
	}

	@Override
	public CustomerCouponDTO getCustomerCoupon() {
		return customerCoupon;
	}

	@Override
	public void setCustomerCoupon(CustomerCouponDTO customerCoupon) {
		this.customerCoupon = customerCoupon;
	}

	@Override
	public boolean permiteCuponesAcumulables() {
		String extension = getExtension("CUPONES_ACUMULABLES");
		if(StringUtils.isNotBlank(extension) && "true".equals(extension)) {
			return true;
		}
		return false;
	}

}
