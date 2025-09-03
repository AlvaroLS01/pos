package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email;

import java.util.regex.Pattern;

public class EmailConstants {

	public static final String PARAM_EMAIL_FIDELIZADO_API = "PARAM_EMAIL_FIDELIZADO_API";
	public static final String PARAM_EMAIL_FIDELIZADO_MODAL = "PARAM_EMAIL_FIDELIZADO_MODAL";
	public static final Pattern EMAIL_REGEX = Pattern.compile("^(?!.*([.-])\\1)(?!.*[.-]{2})[\\w.-]+@[\\w.-]+\\.\\w{2,10}$");

	public static final String PARAM_EMAIL_CONFIRMACION = "PARAM_EMAIL_CONFIRMACION";
	public static final String ACCION_SELECCIONADA = "accionSeleccionada";
	public static final String ACCION_CANCELAR ="accionCancelar";
	public static final String ACCION_IMPRIMIR = "accionImprimir";
	public static final String ACCION_EMAIL ="accionEmail";
	public static final String ACCION_IMPRIMIR_EMAIL ="accionImprimirEmail";
	public static final String ACCION_ACEPTAR_CONFIRMACION = "accionAceptarConfirmacion";
	public static final String ACCION_VOLVER_CONFIRMACION = "accionVolverConfirmacion";

}
