package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contratoAnimal")
public class ContratoAnimalDto {

	@XmlElement(name = "especie")
	private String especie;

	@XmlElement(name = "raza")
	private String raza;

	@XmlElement(name = "sexo")
	private String sexo;

	@XmlElement(name = "peso")
	private String peso;

	@XmlElement(name = "fechaNac")
	private String fechaNac;

	@XmlElement(name = "tipoIden")
	private String tipoIden;

	@XmlElement(name = "numIden")
	private String numIden;

	@XmlElement(name = "nombreFidelizado")
	private String nombre;
	
	@XmlElement(name = "tlfFidelizado")
	private String tlf;
	
	@XmlElement(name = "emailFidelizado")
	private String email;
	
	@XmlElement(name = "apellidoFidelizado")
	private String apellidos;
	
	@XmlElement(name = "documentoFidelizado")
	private String documento;
	
	@XmlElement(name = "cpFidelizado")
	private String cp;
	
	@XmlElement(name = "direccionFidelizado")
	private String direccion;
	
	// lustrum 128891
	@XmlElement(name = "poblacionFidelizado")
	private String poblacion;
	
	@XmlElement(name = "provinciaFidelizado")
	private String provincia;


	@XmlElement(name = "localidadFidelizado")
	private String localidad;
	// lustrum 128891

	@XmlElement(name = "prefijo")
	private String prefijo;
	
	
	//CZZ-490
	@XmlElement(name = "enviado")
	private boolean enviado;

	//CZZ-1355
	@XmlElement(name = "metodoFirma")
	private String metodoFirma = IskaypetEvicertiaService.METODO_FIRMA_DIGITAL; //por defecto es digital, salvo que le den a imprimir
	

	
	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(String fechaNac) {
		this.fechaNac = fechaNac;
	}

	public String getTipoIden() {
		return tipoIden;
	}

	public void setTipoIden(String tipoIden) {
		this.tipoIden = tipoIden;
	}

	public String getNumIden() {
		return numIden;
	}

	public void setNumIden(String numIden) {
		this.numIden = numIden;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTlf() {
		return tlf;
	}

	public void setTlf(String tlf) {
		this.tlf = tlf;
	}

	public String getEmail() {
		return email;
	}

	
	public boolean isEnviado() {
		return enviado;
	}
	
	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellido) {
		this.apellidos = apellido;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String dni) {
		this.documento = dni;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}
	//lustrum 128851
	public String getPoblacion() {
        return poblacion;
    }
	
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

	public String getMetodoFirma() {
		return metodoFirma;
	}

	public void setMetodoFirma(String metodoFirma) {
		this.metodoFirma = metodoFirma;
	}
    
    
    //lustrum 128851
    
    

}
