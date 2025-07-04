
package com.comerzzia.cardoso.pos.services.core.sesion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.worldline.TefWorldlineManager;
import com.comerzzia.cardoso.pos.services.pagos.worldline.CancelacionesAutomaticasWorldline;
import com.comerzzia.cardoso.pos.services.pagos.worldline.WorldlineService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.util.i18n.I18N;

@Primary
@Component
public class CardosoSesion extends Sesion {

	// TAXFREE
	@Autowired
	TaxfreeVariablesService taxfreeVariablesService;
	@Autowired
	protected WorldlineService worldlineService;
	@Autowired
	protected PaymentsManager paymentsManager;
	@Autowired
	protected PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	protected CancelacionesAutomaticasWorldline cancelacionesAutomaticasWorldline;

	protected String idTaxFree = "";
	protected String contentTaxfree = "";
	protected String mimeTypeTaxfree = "";

	@Override
	public void initAplicacion() throws SesionInitException {
		super.initAplicacion();

		// TAXFREE
		iniciarConexionGlobalBlue();
		worldlineService.borrarRegistroPagosTarjeta();
		inicializarPinpadWorldline();
		cancelacionesAutomaticasWorldline.init();
	}

	// TAXFREE
	public void iniciarConexionGlobalBlue() {
		try {
			// Cliente Java
			// GlobalBlueWebServicesPath.initPath(taxfreeVariablesService.getUrlServicio());
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("No se ha podido obtener la URL de acceso a los servicios de Global Blue");
			log.error("initAplicacion() - " + msgError + " : " + e.getMessage(), e);
		}
	}

	private void inicializarPinpadWorldline() {
		// No es correcto iniciar el pinpad en el arranque del POS, pero Worldline puede enviar actualizaciones que
		// tarden cierto tiempo, por lo que no es nada recomendable esperar al primer pago
		// para realizar la inicializacion del pinpad.
		new BackgroundTask<Void>(false){

			@Override
			protected Void call() throws Exception {
				paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
				Optional<PaymentMethodManager> worldlineManagerOpt = paymentsManager.getPaymentsMehtodManagerAvailables().values().stream().filter(TefWorldlineManager.class::isInstance).findFirst();

				if (!worldlineManagerOpt.isPresent()) {
					log.warn("inicializarPinpadWorldline() - No se ha encontrado el manager de Worldline. No se puede iniciar el pinpad");
				}
				else {
					TefWorldlineManager worldlineManager = (TefWorldlineManager) worldlineManagerOpt.get();
					worldlineService.init(worldlineManager.getIdTerminal());
				}
				return null;
			}

			@Override
			protected void failed() {
				super.failed();
				log.error("inicializarPinpadWorldline() - Ha ocurrido un error al iniciar el pinpad de Worldline: " + getException().getMessage(), getException());
			}

		}.start();
	}

	public String getIdTaxFree() {
		return idTaxFree;
	}

	public void setIdTaxFree(String idTaxFree) {
		this.idTaxFree = idTaxFree;
	}

	public String getContentTaxfree() {
		return contentTaxfree;
	}

	public void setContentTaxfree(String contentTaxfree) {
		this.contentTaxfree = contentTaxfree;
	}

	public String getMimeTypeTaxfree() {
		return mimeTypeTaxfree;
	}

	public void setMimeTypeTaxfree(String mimeTypeTaxfree) {
		this.mimeTypeTaxfree = mimeTypeTaxfree;
	}
}
