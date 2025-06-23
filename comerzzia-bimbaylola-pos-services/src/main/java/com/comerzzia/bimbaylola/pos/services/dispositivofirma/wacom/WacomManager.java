package com.comerzzia.bimbaylola.pos.services.dispositivofirma.wacom;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.dispositivofirma.ByLConfiguracionModelo;
import com.comerzzia.bimbaylola.pos.services.dispositivofirma.DispositivoFirmaException;
import com.comerzzia.bimbaylola.pos.services.dispositivofirma.IFirma;
import com.comerzzia.pos.util.i18n.I18N;
import com.florentis.signature.SigCtl;
import com.florentis.signature.SigObj;
import com.florentis.signature.WizCtl;

@Component
public class WacomManager implements IFirma {

	private static Logger log = Logger.getLogger(WacomManager.class);

	protected String modelo;
	protected String modoConexion;
	protected HashMap<String, ByLConfiguracionModelo> listaConfiguracion;
	protected ByLConfiguracionModelo configuracionActual;

	private BufferedImage signatureImage;
	private Thread tWizard;
	private MyWizardScript myWizardScript;
	private boolean scriptIsRunning = false;

	private static boolean firmaFinalizada = false;
	private static boolean firmaCancelada = false;
	private static boolean errorConexion = false;

	protected Map<String, Object> mapaRespuestas = new HashMap<String, Object>();
	protected byte[] imagenFirma;

	@Override
	public String getModelo() {
		return modelo;
	}

	@Override
	public String getManejador() {
		return listaConfiguracion.get(modelo).getManejador();
	}

	@Override
	public void getMetodosConexion() {
	}

	@Override
	public String getModoConexion() {
		return modoConexion;
	}

	@Override
	public HashMap<String, ByLConfiguracionModelo> getListaConfiguracion() {
		return listaConfiguracion;
	}

	public void setListaConfiguracion(HashMap<String, ByLConfiguracionModelo> listaConfiguracion) {
		this.listaConfiguracion = listaConfiguracion;
	}

	@Override
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Override
	public void setModoConexion(String modoConexion) {
		this.modoConexion = modoConexion;
	}

	@Override
	public ByLConfiguracionModelo getConfiguracionActual() {
		return configuracionActual;
	}

	@Override
	public void setConfiguracionActual(ByLConfiguracionModelo configuracionActual) {
		this.configuracionActual = configuracionActual;

	}

	@Override
	public Map<String, Object> firmar() throws DispositivoFirmaException {
		firmaFinalizada = false;
		firmaCancelada = false;
		errorConexion = false;

		if (!scriptIsRunning) {
			myWizardScript = new MyWizardScript();
			tWizard = new Thread(myWizardScript);
			
			tWizard.start();
			
		}

		while (!firmaFinalizada) {
		}

		if (firmaCancelada) {
			throw new DispositivoFirmaException(I18N.getTexto("El usuario ha cancelado el proceso de firma."));
		}
		else if (errorConexion) {
			throw new DispositivoFirmaException(I18N.getTexto("El dispositivo no se encuentra disponible. Revise la conexión."));
		}
		return mapaRespuestas;
	}

	class MyWizardScript implements Runnable {

		private TPad pad;
		private WizCtl wizCtl;
		private SigCtl sigCtl;
		private SigObj sigObj;
		private boolean isCheckNotificaciones;
		private boolean isCheckUsoDatos;
		protected Map<String, Object> mapaRespuestasTask = new HashMap<String, Object>();

		public void run() {
			try {
				wizCtl = new WizCtl();

				wizCtl.licence("eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI3YmM5Y2IxYWIxMGE0NmUxODI2N2E5MTJkYTA2ZTI3NiIsImV4cCI6MjE0NzQ4MzY0NywiaWF0IjoxNTYwOTUwMjcyLCJyaWdodHMiOlsiU0lHX1NES19DT1JFIiwiU0lHQ0FQVFhfQUNDRVNTIl0sImRldmljZXMiOlsiV0FDT01fQU5ZIl0sInR5cGUiOiJwcm9kIiwibGljX25hbWUiOiJTaWduYXR1cmUgU0RLIiwid2Fjb21faWQiOiI3YmM5Y2IxYWIxMGE0NmUxODI2N2E5MTJkYTA2ZTI3NiIsImxpY191aWQiOiJiODUyM2ViYi0xOGI3LTQ3OGEtYTlkZS04NDlmZTIyNmIwMDIiLCJhcHBzX3dpbmRvd3MiOltdLCJhcHBzX2lvcyI6W10sImFwcHNfYW5kcm9pZCI6W10sIm1hY2hpbmVfaWRzIjpbXX0.ONy3iYQ7lC6rQhou7rz4iJT_OJ20087gWz7GtCgYX3uNtKjmnEaNuP3QkjgxOK_vgOrTdwzD-nm-ysiTDs2GcPlOdUPErSp_bcX8kFBZVmGLyJtmeInAW6HuSp2-57ngoGFivTH_l1kkQ1KMvzDKHJbRglsPpd4nVHhx9WkvqczXyogldygvl0LRidyPOsS5H2GYmaPiyIp9In6meqeNQ1n9zkxSHo7B11mp_WXJXl0k1pek7py8XYCedCNW5qnLi4UCNlfTd6Mk9qz31arsiWsesPeR9PN121LBJtiPi023yQU8mgb9piw_a-ccciviJuNsEuRDN3sGnqONG3dMSA");

				sigCtl = new SigCtl();
				sigCtl.licence("eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI3YmM5Y2IxYWIxMGE0NmUxODI2N2E5MTJkYTA2ZTI3NiIsImV4cCI6MjE0NzQ4MzY0NywiaWF0IjoxNTYwOTUwMjcyLCJyaWdodHMiOlsiU0lHX1NES19DT1JFIiwiU0lHQ0FQVFhfQUNDRVNTIl0sImRldmljZXMiOlsiV0FDT01fQU5ZIl0sInR5cGUiOiJwcm9kIiwibGljX25hbWUiOiJTaWduYXR1cmUgU0RLIiwid2Fjb21faWQiOiI3YmM5Y2IxYWIxMGE0NmUxODI2N2E5MTJkYTA2ZTI3NiIsImxpY191aWQiOiJiODUyM2ViYi0xOGI3LTQ3OGEtYTlkZS04NDlmZTIyNmIwMDIiLCJhcHBzX3dpbmRvd3MiOltdLCJhcHBzX2lvcyI6W10sImFwcHNfYW5kcm9pZCI6W10sIm1hY2hpbmVfaWRzIjpbXX0.ONy3iYQ7lC6rQhou7rz4iJT_OJ20087gWz7GtCgYX3uNtKjmnEaNuP3QkjgxOK_vgOrTdwzD-nm-ysiTDs2GcPlOdUPErSp_bcX8kFBZVmGLyJtmeInAW6HuSp2-57ngoGFivTH_l1kkQ1KMvzDKHJbRglsPpd4nVHhx9WkvqczXyogldygvl0LRidyPOsS5H2GYmaPiyIp9In6meqeNQ1n9zkxSHo7B11mp_WXJXl0k1pek7py8XYCedCNW5qnLi4UCNlfTd6Mk9qz31arsiWsesPeR9PN121LBJtiPi023yQU8mgb9piw_a-ccciviJuNsEuRDN3sGnqONG3dMSA");

				sigObj = sigCtl.signature();

				wizCtl.setEventHandler(new WizCtl.WizCtlEvents(){

					public boolean onPadEvent(WizCtl ctl, String id, Object type) {
						// textArea.append("Pad Event: " + id);
						if ("Ok".equals(id)) {
							scriptCompleted();
						}
						else if ("Clear".equals(id)) {
							// textArea.append("Clear\n");
						}
						else if ("Cancel".equals(id)) {
							scriptCancelled();
						}
						else if ("CheckNotificaciones".equals(id)) {
							isCheckNotificaciones = !isCheckNotificaciones;
						}
						else if ("CheckUsoDatos".equals(id)) {
							isCheckUsoDatos = !isCheckUsoDatos;
						}
						else if ("Next".equals(id)) {
							// if (isCheckUsoDatos)
							step2();
						}
						else {

						}
						return true;
					}

				});
				startWizard();
				try {
					wizCtl.processEvents();
				}
				catch (InterruptedException e) {
					log.debug("Wacom/run() - Ha ocurrido un error: " + e.getMessage());
				}
			}
			catch (Exception e) {
				errorConexion = true;
				firmaFinalizada = true;
				log.debug("Wacom/run() - Ha ocurrido un error: " + e.getMessage());
			}

		}

		private void startWizard() {
			boolean success = wizCtl.padConnect();
			if (success) {
				scriptIsRunning = true;
				isCheckNotificaciones = false;
				isCheckUsoDatos = false;

				switch (wizCtl.padWidth()) {
					case 396:
						pad = new TPad("STU-300", 60, 200, 200, 8, 8, 16, 70); // 396x100
						break;
					case 640:
						pad = new TPad("STU-500", 300, 360, 390, 16, 22, 32, 110); // 640x800
						break;
					case 800:
						pad = new TPad("STU-520 or STU-530", 300, 360, 390, 16, 22, 32, 110); // 800x480
						break;
					case 320:
						pad = new TPad("STU-430 or ePad", 100, 130, 150, 10, 12, 16, 110); // 320x200
						break;
					default:
				}

				step1();
			}
			else {
				errorConexion = true;
				firmaFinalizada = true;
			}
		}

		private void step1() {
			wizCtl.reset();

			// insert checkbox
			wizCtl.setFont(pad.textFont);
			wizCtl.addObject(WizCtl.objectCheckbox, "CheckNotificaciones", "left", 10, MENSAJE_CHECK_NOTIFICACIONES, 2);
			wizCtl.addObject(WizCtl.objectCheckbox, "CheckUsoDatos", "left", 50, MENSAJE_CHECK_USODATOS, 2);

			wizCtl.addObject(WizCtl.objectText, "txtInfo", "center", 120, TPad.MENSAJE_INFO, 2);

			// insert the buttons
			wizCtl.setFont(pad.buttonFont);
			wizCtl.addObject(WizCtl.objectButton, "Cancel", "left", "bottom", TPad.BOTON_CANCEL, pad.buttonWidth);
			wizCtl.addObject(WizCtl.objectButton, "Next", "right", "bottom", TPad.BOTON_SIGUIENTE, pad.buttonWidth);

			wizCtl.display();
		}

		private void step2() {
			wizCtl.reset();

			// insert message
			wizCtl.setFont(pad.textFont);
			wizCtl.addObject(WizCtl.objectText, "txt", "centre", "top", TPad.MENSAJE_FIRMAR_DEBAJO, null);

			// insert a signature line
			wizCtl.setFont(pad.sigLineFont);
			if (pad.model == "STU-300")
				wizCtl.addObject(WizCtl.objectText, "txt", "left", pad.signatureLineY, " ", null);
			else
				wizCtl.addObject(WizCtl.objectText, "txt", "centre", pad.signatureLineY, " ", null);

			// insert the signature control
			wizCtl.setFont(pad.textFont);
			wizCtl.addObject(WizCtl.objectSignature, "Sig", 0, 0, sigObj, null);

			// provide who and why for sig capture
			wizCtl.addObject(WizCtl.objectText, "who", "right", pad.whoY, " ", null);
			wizCtl.addObject(WizCtl.objectText, "why", "right", pad.whyY, " ", null);

			// insert the buttons
			wizCtl.setFont(pad.buttonFont);
			if (pad.model == "STU-300") {
				wizCtl.addObject(WizCtl.objectButton, "Cancel", "right", "top", TPad.BOTON_CANCEL, pad.buttonWidth);
				wizCtl.addObject(WizCtl.objectButton, "Clear", "right", "middle", TPad.BOTON_LIMPIAR, pad.buttonWidth);
				wizCtl.addObject(WizCtl.objectButton, "Ok", "right", "bottom", TPad.BOTON_OK, pad.buttonWidth);
			}
			else {
				wizCtl.addObject(WizCtl.objectButton, "Cancel", "left", "bottom", TPad.BOTON_CANCEL, pad.buttonWidth);
				wizCtl.addObject(WizCtl.objectButton, "Clear", "center", "bottom", TPad.BOTON_LIMPIAR, pad.buttonWidth);
				wizCtl.addObject(WizCtl.objectButton, "Ok", "right", "bottom", TPad.BOTON_OK, pad.buttonWidth);
			}

			wizCtl.display();
		}

		private void scriptCompleted() {
			mapaRespuestas.put(RESPUESTA_CONSENTIMIENTO_NOTIFICACIONES, isCheckNotificaciones);
			mapaRespuestas.put(RESPUESTA_CONSENTIMIENTO_USO_DATOS, isCheckUsoDatos);

			SigObj sigObj = sigCtl.signature();
			byte[] imageByte = null;
			if (sigObj.isCaptured()) {
				// sigObj.extraData("AdditionalData", "C# Wizard test: Additional data");

				String filename = "./sig.png";
				int flags = SigObj.outputFilename | SigObj.color32BPP | SigObj.encodeData;
				sigObj.renderBitmap(filename, 200, 150, "image/bmp", 0.5f, 0xff0000, 0xffffff, 0.0f, 0.0f, flags);

				imageByte = paintSignature(filename);

				mapaRespuestas.put(IMAGEN_FIRMA, imageByte);

				closeWizard();
			}

			firmaFinalizada = true;
		}

		private void scriptCancelled() {
			firmaCancelada = true;
			closeWizard();
		}

		private void closeWizard() {
			scriptIsRunning = false;
			wizCtl.reset();
			wizCtl.display();
			wizCtl.padDisconnect();

			firmaFinalizada = true;
		}

		private byte[] paintSignature(String fileName) {
			byte[] imageBytes = null;
			try {

				// Estas dos líneas se añaden porque al realizar el ImageIO.read falla al obtener el contexto del thread
				ClassLoader cl = ClassLoader.getSystemClassLoader();
				Thread.currentThread().setContextClassLoader(cl);

				File ficheroFirma = new File(fileName);
				signatureImage = ImageIO.read(ficheroFirma);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(signatureImage, "bmp", baos);
				baos.flush();
				imageBytes = baos.toByteArray();
				baos.close();

				ficheroFirma.delete();

				imageBytes = baos.toByteArray();
			}
			catch (Exception e) {
				log.debug("paintSignature() - Ha ocurrido un error: " + e.toString());
			}

			return imageBytes;
		}

		protected void releaseControl() {
			wizCtl.reset();
			wizCtl.display();
			wizCtl.padDisconnect();
		}

	}

	@Override
	public void iniciarDispositivoFirma() {
		log.debug("iniciarDispositivoFirma() - NO NECESITA CARGA PREVIA");

	}
}
