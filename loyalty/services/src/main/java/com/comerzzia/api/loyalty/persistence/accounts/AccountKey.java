package com.comerzzia.api.loyalty.persistence.accounts;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class AccountKey extends MultiInstance {

	private static final long serialVersionUID = -7030420037399841410L;
	
	private Long idCuentaTarjeta;
    
    public AccountKey() {
    	
    }
        
    public AccountKey(IDatosSesion datosSesion) {
    	super(datosSesion);
    }

	public AccountKey(IDatosSesion datosSesion, Long idCuentaTarjeta) {
		this(datosSesion);
		this.idCuentaTarjeta = idCuentaTarjeta;
	}

    public Long getIdCuentaTarjeta() {
        return idCuentaTarjeta;
    }

    public void setIdCuentaTarjeta(Long idCuentaTarjeta) {
        this.idCuentaTarjeta = idCuentaTarjeta;
    }

	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((idCuentaTarjeta == null) ? 0 : idCuentaTarjeta.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    AccountKey other = (AccountKey) obj;
	    if (idCuentaTarjeta == null) {
		    if (other.idCuentaTarjeta != null)
			    return false;
	    }
	    else if (!idCuentaTarjeta.equals(other.idCuentaTarjeta))
		    return false;
	    return true;
    }
}