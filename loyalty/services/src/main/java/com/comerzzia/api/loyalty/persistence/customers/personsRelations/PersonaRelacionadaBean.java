package com.comerzzia.api.loyalty.persistence.customers.personsRelations;

import java.util.Date;

public class PersonaRelacionadaBean extends PersonaRelacionadaKey {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9177083883971975433L;

	private Long idFidelizadoOrigen;

    private Long idFidelizadoDestino;

    private String codTipoRelacion = null;

    private String nombre = "";

    private String apellidos = "";

    private String sexo;

    private Date fechaNacimiento;

    private String desTipoRelacion = null;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    private String nombreCompleto;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public Long getIdFidelizadoOrigen() {
        return idFidelizadoOrigen;
    }

    public void setIdFidelizadoOrigen(Long idFidelizadoOrigen) {
        this.idFidelizadoOrigen = idFidelizadoOrigen;
    }

    public Long getIdFidelizadoDestino() {
        return idFidelizadoDestino;
    }

    public void setIdFidelizadoDestino(Long idFidelizadoDestino) {
        this.idFidelizadoDestino = idFidelizadoDestino;
    }

    public String getCodTipoRelacion() {
        return codTipoRelacion;
    }

    public void setCodTipoRelacion(String codTipoRelacion) {
        this.codTipoRelacion = codTipoRelacion == null ? null : codTipoRelacion.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos == null ? null : apellidos.trim();
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo == null ? null : sexo.trim();
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDesTipoRelacion() {
        return desTipoRelacion;
    }

    public void setDesTipoRelacion(String desTipoRelacion) {
        this.desTipoRelacion = desTipoRelacion == null ? null : desTipoRelacion.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public void setNombreCompleto(String nombreCompleto){
    	String[] nombres = nombreCompleto.split(",");
    	setApellidos(nombres[0]);
    	setNombre(nombres[1]);
    	this.nombreCompleto = nombreCompleto;
    }
    
    public String getNombreCompleto(){
    	this.nombreCompleto = new String();
    	if(!this.nombre.isEmpty() && !this.apellidos.isEmpty()){
    		this.nombreCompleto =  apellidos+","+ nombre;
    	}
    	return nombreCompleto;
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((idFidelizadoDestino == null) ? 0 : idFidelizadoDestino.hashCode());
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
	    PersonaRelacionadaBean other = (PersonaRelacionadaBean) obj;
	    if (idFidelizadoDestino == null) {
		    if (other.idFidelizadoDestino != null)
			    return false;
	    }
	    else if (!idFidelizadoDestino.equals(other.idFidelizadoDestino))
		    return false;
	    return true;
    }
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------
}