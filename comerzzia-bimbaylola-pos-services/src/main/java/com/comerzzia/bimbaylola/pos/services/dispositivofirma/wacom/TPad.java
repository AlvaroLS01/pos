package com.comerzzia.bimbaylola.pos.services.dispositivofirma.wacom;

import com.comerzzia.pos.util.i18n.I18N;
import com.florentis.signature.WizCtl;

public class TPad {

	protected String model;
	protected WizCtl.Font textFont;
	protected WizCtl.Font buttonFont;
	protected WizCtl.Font sigLineFont;
	protected int buttonWidth;
	protected int signatureLineY;
	protected int whoY;
	protected int whyY;
	
	public static final String MENSAJE_INFO = I18N.getTexto("Bimba y Lola es Responsable del Tratamiento de sus datos. Para más info consulte en tienda");
	public static final String BOTON_OK = I18N.getTexto("OK");
	public static final String BOTON_SIGUIENTE = I18N.getTexto("Siguiente");
	public static final String BOTON_CANCEL = I18N.getTexto("Cancelar");
	public static final String MENSAJE_FIRMAR_DEBAJO = I18N.getTexto("Por favor firme debajo ...");
	public static final String BOTON_LIMPIAR = I18N.getTexto("Limpiar");

	public TPad(String model, int signatureLineY, int whoY, int whyY, int textFontSize, int buttonFontSize, int signLineSize, int buttonWidth) {

		this.model = model;
		this.signatureLineY = signatureLineY;
		this.whoY = whoY;
		this.whyY = whyY;
		this.buttonWidth = buttonWidth;

		textFont = new WizCtl.Font();
		textFont.name = "Verdana";
		textFont.size = textFontSize;

		buttonFont = new WizCtl.Font();
		buttonFont.name = "Verdana";
		buttonFont.size = buttonFontSize;

		sigLineFont = new WizCtl.Font();
		sigLineFont.name = "Verdana";
		sigLineFont.size = signLineSize;

	}
}
