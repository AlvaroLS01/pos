package com.comerzzia.api.loyalty.persistence.customers.access;

import com.comerzzia.core.util.base.MantenimientoBean;

public class AccesoFidelizadoKey extends MantenimientoBean {

	private static final long serialVersionUID = -7544654223565835242L;

	private String uidInstancia;

    private String usuario;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getUsuario() {
    	String usu = null;
    	if(usuario != null){
    		usu = usuario.replace("@", "");
    	}
    	return usu;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario == null ? null : usuario.trim();
    }

	@Override
	protected void initNuevoBean() {
		// TODO Auto-generated method stub
		
	}
}