package com.comerzzia.iskaypet.pos.services.proformas.rest.classes.cliente;


public class ClienteDTO {

    private String idQvet;
    private String idComerzzia;
    private String numeroTarjeta;
    private String nombre;
    private String apellidos;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String telefono;
    private String movil;
    private String direccion;
    private String codigoPostal;
    private String localidad;
    private String provincia;
    private String pais;
    private boolean fidelizado;
    private String tipoCliente;
    private boolean requiereRespuesta;

    public ClienteDTO() {
    }

    public ClienteDTO(String idQvet, String idComerzzia, String numeroTarjeta, String nombre, String apellidos, String tipoDocumento, String documento, String email, String telefono, String movil, String direccion, String codigoPostal, String localidad, String provincia, String pais, boolean fidelizado, String tipoCliente, boolean requiereRespuesta) {
        this.idQvet = idQvet;
        this.idComerzzia = idComerzzia;
        this.numeroTarjeta = numeroTarjeta;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.movil = movil;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.fidelizado = fidelizado;
        this.tipoCliente = tipoCliente;
        this.requiereRespuesta = requiereRespuesta;
    }

    public String getIdQvet() {
        return idQvet;
    }

    public void setIdQvet(String idQvet) {
        this.idQvet = idQvet;
    }

    public String getIdComerzzia() {
        return idComerzzia;
    }

    public void setIdComerzzia(String idComerzzia) {
        this.idComerzzia = idComerzzia;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public boolean isFidelizado() {
        return fidelizado;
    }

    public void setFidelizado(boolean fidelizado) {
        this.fidelizado = fidelizado;
    }

    public boolean isRequiereRespuesta() {
        return requiereRespuesta;
    }

    public void setRequiereRespuesta(boolean requiereRespuesta) {
        this.requiereRespuesta = requiereRespuesta;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
}
