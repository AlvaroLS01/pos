package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

import com.comerzzia.dinosol.librerias.sad.client.model.Vehiculos;

/**
 * Bean para controlar los datos que mostramos en el ComboBox de envíos a domicilio.
 */
public class RutasVehiculoBean {

	private String nombre;
	private Vehiculos vehiculo;
	
	public String getNombre() {
		return vehiculo.getNombre();
	}
	
	public Vehiculos getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(Vehiculos vehiculo) {
		this.vehiculo = vehiculo;
		this.nombre = vehiculo.getNombre();
	}

	@Override
	public String toString() {
		return nombre;
	}

}
