package com.comerzzia.dinosol.pos.services.core.sesion;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;

@Component
@Primary
public class DinoSesion extends Sesion {
	
	@Autowired
	private AuditoriasService auditoriasService;
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	protected SipayService sipayService;
	
	@Override
	public void closeUsuarioSesion() {
		String usuarioCaja = sesionCaja.getCajaAbierta() != null ? sesionCaja.getCajaAbierta().getUsuario() : null;
		String usuario = sesionUsuario.getUsuario().getUsuario();
		
		super.closeUsuarioSesion();
		
		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("LOGOUT");
		auditoria.setCajeroCaja(usuarioCaja);
		auditoria.setCajeroOperacion(usuario);
		auditoriasService.guardarAuditoria(auditoria);
	}
	
	@Override
	public void initAplicacion() throws SesionInitException {
		super.initAplicacion();
		
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getControlClass().equals("tefSipayManager")) {
				try {
					sipayService.cargarClavesCajones();
				} catch (Exception e) {
					log.error("initAplicacion() - Error al inicializar dispositivo y cargar las claves de los cajones - " + e.getMessage());
				}
				
			}
		}
		
	}
}
