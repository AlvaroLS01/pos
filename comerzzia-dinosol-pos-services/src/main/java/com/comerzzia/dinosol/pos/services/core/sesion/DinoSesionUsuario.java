package com.comerzzia.dinosol.pos.services.core.sesion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.rest.client.usuarios.CambiarClaveUsuarioRequestRest;
import com.comerzzia.core.rest.client.usuarios.UsuariosRest;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasena;
import com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto.PoliticaContrasenaUsuario;
import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.ServicioConfiguracionSipay;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.perfiles.PerfilException;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.sesion.SesionUsuario;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.response.ResponsePostRest;

@Component
@Primary
public class DinoSesionUsuario extends SesionUsuario {
	
	@Autowired
	private ServicioConfiguracionSipay servicioConfiguracionSipay;

	public void initUsuarioOnline(PoliticaContrasenaUsuario usuario, PoliticaContrasena politica) throws PerfilException {
		this.usuario = new UsuarioBean();

		this.usuario.setActivo(true);
		this.usuario.setClave(usuario.getClave());
		this.usuario.setIdUsuario(politica.getIdUsuario());
		this.usuario.setUidInstancia(usuario.getUidInstancia());
		this.usuario.setUsuario(usuario.getUsuario());
		
		setIsSuperAdministrador();
	}

	@Override
	public boolean cambiarPassword(String newPassword, String oldPassword, String usuario)
			throws RestException, RestHttpException {

		boolean result = false;
		CambiarClaveUsuarioRequestRest request = new CambiarClaveUsuarioRequestRest();

		request.setClave(oldPassword);
		request.setClaveNueva(newPassword);
		request.setUsuario(usuario);
		request.setApiKey(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY));
		request.setUidActividad(sesion.getAplicacion().getUidActividad());

		ResponsePostRest response = UsuariosRest.setClaveUsuario(request);

		if (response.getNumeroUpdates() > 0) {
			result = true;
		}

		return result;
	}
	public void initUsuarioQR(UsuarioBean usuario) throws PerfilException {
		this.usuario = usuario;
		
		setIsSuperAdministrador();
	}
	@Override
	public void init(String usuario, String password) throws SesionInitException, UsuarioInvalidLoginException {
		super.init(usuario, password);
		//Cambiamos modo online-offline en función a su valor al iniciar login
//		servicioConfiguracionSipay.operativaOffline(false);
	}
}
