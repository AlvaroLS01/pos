package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.util.i18n.I18N;

public interface IFirma {

	public static final String IMAGEN_FIRMA = "IMAGEN_FIRMA";
	public static final String RESPUESTA_CONSENTIMIENTO_USO_DATOS = "RESPUESTA_CONSENTIMIENTO_USO_DATOS";
	public static final String RESPUESTA_CONSENTIMIENTO_NOTIFICACIONES = "RESPUESTA_CONSENTIMIENTO_NOTIFICACIONES";

	public static final String MENSAJE_CHECK_NOTIFICACIONES = I18N.getTexto("Quiero recibir comunicaciones comerciales de BIMBA Y LOLA.");
	public static final String MENSAJE_CHECK_USODATOS = I18N.getTexto("Acepto que mis datos sean usados para la elaboración de perfiles basados en hábitos de compra.");

	public String getManejador();

	public void getMetodosConexion();

	public HashMap<String, ByLConfiguracionModelo> getListaConfiguracion();

	public String getModelo();

	public void setModelo(String modelo);

	public String getModoConexion();

	public void setModoConexion(String modoConexion);

	public ByLConfiguracionModelo getConfiguracionActual();

	public void setConfiguracionActual(ByLConfiguracionModelo configuracionActual);

	public Map<String, Object> firmar() throws DispositivoFirmaException;

	public void iniciarDispositivoFirma();
}
