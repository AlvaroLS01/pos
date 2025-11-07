package com.comerzzia.api.loyalty.persistence.customers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.api.loyalty.persistence.customers.access.AccesoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.addresses.DireccionFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.contacts.TiposContactoFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.links.EnlaceFidelizadoBean;
import com.comerzzia.api.loyalty.persistence.customers.personsRelations.PersonaRelacionadaBean;
import com.comerzzia.api.loyalty.persistence.customers.wishlists.ListaDeseosFidelizadoBean;
import com.comerzzia.core.model.etiquetas.categorias.EtiquetaBean;
import com.fasterxml.jackson.annotation.JsonIgnore;


@XmlRootElement(name="fidelizado")
@XmlType(name="fid")
public class FidelizadoBean extends FidelizadoKey {

	private static final long serialVersionUID = -6648042929500540690L;

	@XmlTransient
	private Boolean activoFiltro;
	
	private String nombre;

    private String apellidos;

    private String domicilio;

    private String poblacion;

    private String localidad;

    private String provincia;

    private String cp;

    private String codPais;

    private String codTipoIden;

    private String documento;

    private String observaciones;

    private Date fechaNacimiento;

    private String sexo;

    private String codEstCivil;

    private Date fechaAlta;

    private Date fechaModificacion;

    private Date fechaBaja;

    private String codFidelizado;

    private String desPais;

    private String desEstCivil;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    private String domicilioEntrega = null;
	private String poblacionEntrega = null;
	private String localidadEntrega = null;
	private String provinciaEntrega = null;
	private String cpEntrega = null;
	private String numeroTarjeta = null;
	private String anyo = null;
	private String mes = null;
	private String dia = null;
	
	private EnlaceFidelizadoBean enlace = null;

	@XmlTransient
	private boolean tarjetasCargadas = false;
	private List<Card> tarjetas = null;

	@XmlTransient
	private boolean personasRelacionadasCargadas = false;
	private List<PersonaRelacionadaBean> personasRelacionadas = new ArrayList<PersonaRelacionadaBean>();

	private List<Account> cuentas = null;
	private String descripcionEstadoCivil = null;

	@XmlTransient
	private boolean colectivosFidelizacionCargadas = false;
	private List<ColectivosFidelizadoBean> colectivos = new ArrayList<ColectivosFidelizadoBean>();

	@XmlTransient
	private boolean tiposContactoCargados = false;
	private boolean tiposContactosDisponiblesCargados = false;
	@XmlElementWrapper(name ="contactos")
	@XmlElement(name="contacto")
	//@JsonProperty(value="contactos")
	private List<TiposContactoFidelizadoBean> tiposContacto = new ArrayList<TiposContactoFidelizadoBean>();

	@XmlTransient
	private boolean direccionesCargadas = false;
	private List<DireccionFidelizadoBean> direcciones = new ArrayList<DireccionFidelizadoBean>();
		
	//Etiquetas-categoria
	@XmlTransient
	private boolean etiquetasCategoriasCargados = false;
	private List<EtiquetaBean> etiquetasCategorias = new ArrayList<EtiquetaBean>();
	
	//Tienda favorita
	@XmlTransient
	private boolean codAlmFavoritoCargado = false; 
	@XmlTransient
	private String codAlm;
	@XmlTransient
	private String desAlm;
    
	//Acceso fidelizado - App
	private AccesoFidelizadoBean acceso = null;
	
	@XmlTransient
	private boolean altaRapida;
	
	private Double saldo;
	
	
	private Double saldoProvisional;
	
	//Listas de deseos
	@XmlTransient
	private boolean listasDeseosCargadas = false;
	private List<ListaDeseosFidelizadoBean> listasDeseos = new ArrayList<ListaDeseosFidelizadoBean>();
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------

	public Boolean getActivoFiltro() {
		return activoFiltro;
	}
	
	public void setActivoFiltro(Boolean activoFiltro) {
		this.activoFiltro = activoFiltro;
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

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio == null ? null : domicilio.trim();
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion == null ? null : poblacion.trim();
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad == null ? null : localidad.trim();
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia == null ? null : provincia.trim();
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp == null ? null : cp.trim();
    }

    public String getCodPais() {
        return codPais;
    }

    public void setCodPais(String codPais) {
        this.codPais = codPais == null ? null : codPais.trim();
    }

    public String getCodTipoIden() {
        return codTipoIden;
    }

    public void setCodTipoIden(String codTipoIden) {
        this.codTipoIden = codTipoIden == null ? null : codTipoIden.trim();
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento == null ? null : documento.trim();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones == null ? null : observaciones.trim();
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo == null ? null : sexo.trim();
    }

    public String getCodEstCivil() {
        return codEstCivil;
    }

    public void setCodEstCivil(String codEstCivil) {
        this.codEstCivil = codEstCivil == null ? null : codEstCivil.trim();
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getCodFidelizado() {
        return codFidelizado;
    }

    public void setCodFidelizado(String codFidelizado) {
        this.codFidelizado = codFidelizado == null ? null : codFidelizado.trim();
    }

    public String getDesPais() {
        return desPais;
    }

    public void setDesPais(String desPais) {
        this.desPais = desPais == null ? null : desPais.trim();
    }

    public String getDesEstCivil() {
        return desEstCivil;
    }

    public void setDesEstCivil(String desEstCivil) {
        this.desEstCivil = desEstCivil == null ? null : desEstCivil.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    @Override
    protected void initNuevoBean() {
		tarjetasCargadas = true;
		personasRelacionadasCargadas = true;
		colectivosFidelizacionCargadas = true;
		tarjetas = new ArrayList<Card>();

		personasRelacionadas = new ArrayList<PersonaRelacionadaBean>();
		colectivos = new ArrayList<ColectivosFidelizadoBean>();

		cuentas = new ArrayList<Account>();

		// Por defecto se sugiere que el cliente reciba información
		// recibeInformacion = "S";
		tiposContactoCargados = true;
		tiposContacto = new ArrayList<TiposContactoFidelizadoBean>();

		direccionesCargadas = true;
		direcciones = new ArrayList<DireccionFidelizadoBean>();
		
		//Etiquetas-categoria
		etiquetasCategorias = new ArrayList<EtiquetaBean>();
		etiquetasCategoriasCargados = true;
		
		//Tienda favorita
		codAlmFavoritoCargado = true;
		codAlm = new String();
		desAlm = new String();
	}
    
    public String getDomicilioEntrega() {
		return domicilioEntrega;
	}

	public void setDomicilioEntrega(String domicilioEntrega) {
		this.domicilioEntrega = domicilioEntrega;
	}

	public String getPoblacionEntrega() {
		return poblacionEntrega;
	}

	public void setPoblacionEntrega(String poblacionEntrega) {
		this.poblacionEntrega = poblacionEntrega;
	}

	public String getLocalidadEntrega() {
		return localidadEntrega;
	}

	public void setLocalidadEntrega(String localidadEntrega) {
		this.localidadEntrega = localidadEntrega;
	}

	public String getProvinciaEntrega() {
		return provinciaEntrega;
	}

	public void setProvinciaEntrega(String provinciaEntrega) {
		this.provinciaEntrega = provinciaEntrega;
	}

	public String getCpEntrega() {
		return cpEntrega;
	}

	public void setCpEntrega(String cpEntrega) {
		this.cpEntrega = cpEntrega;
	}
	
	@JsonIgnore
	public String getSexoDescripcion() {
		String sexo = "";
		if ("H".equals(this.sexo)) {
			sexo = "Hombre";
		}
		else if ("M".equals(this.sexo)) {
			sexo = "Mujer";
		}

		return sexo;
	}
	
	public boolean isTarjetasCargadas() {
		return tarjetasCargadas;
	}

	public void setTarjetasCargadas(boolean tarjetasCargadas) {
		this.tarjetasCargadas = tarjetasCargadas;
	}

	public List<Card> getTarjetas() {
		return tarjetas;
	}

	public void setTarjetas(List<Card> tarjetas) {
		this.tarjetas = tarjetas;
	}

	public List<Account> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Account> cuentas) {
		this.cuentas = cuentas;
	}

	public String getNombreCompleto() {
		return this.nombre + " " + this.apellidos;
	}

	public String getDescripcionEstadoCivil() {
		return descripcionEstadoCivil;
	}

	public void setDescripcionEstadoCivil(String descripcionEstadoCivil) {
		this.descripcionEstadoCivil = descripcionEstadoCivil;
	}

	public List<PersonaRelacionadaBean> getPersonasRelacionadas() {
		return personasRelacionadas;
	}

	public void setPersonasRelacionadas(List<PersonaRelacionadaBean> personasRelacionadas) {
		this.personasRelacionadas = personasRelacionadas;
	}

	public boolean isPersonasRelacionadasCargadas() {
		return personasRelacionadasCargadas;
	}

	public void setPersonasRelacionadasCargadas(boolean personasRelacionadasCargadas) {
		this.personasRelacionadasCargadas = personasRelacionadasCargadas;
	}

	public boolean isColectivosFidelizacionCargadas() {
		return colectivosFidelizacionCargadas;
	}

	public void setColectivosFidelizacionCargadas(boolean colectivosFidelizacionCargadas) {
		this.colectivosFidelizacionCargadas = colectivosFidelizacionCargadas;
	}

	public List<ColectivosFidelizadoBean> getColectivos() {
		return colectivos;
	}

	public void setColectivos(List<ColectivosFidelizadoBean> colectivos) {
		this.colectivos = colectivos;
	}

	public List<TiposContactoFidelizadoBean> getTiposContacto() {
		return tiposContacto;
	}

	public void setTiposContacto(List<TiposContactoFidelizadoBean> tiposContacto) {
		this.tiposContacto = tiposContacto;
	}

	public boolean isTiposContactoCargados() {
		return tiposContactoCargados;
	}

	public void setTiposContactoCargados(boolean tiposContactoCargados) {
		this.tiposContactoCargados = tiposContactoCargados;
	}
	
	public boolean isDireccionesCargadas() {
		return direccionesCargadas;
	}

	public void setDireccionesCargadas(boolean direccionesCargadas) {
		this.direccionesCargadas = direccionesCargadas;
	}

	public List<DireccionFidelizadoBean> getDirecciones() {
		return direcciones;
	}

	public void setDirecciones(List<DireccionFidelizadoBean> direcciones) {
		this.direcciones = direcciones;
	}

	public boolean isTiposContactosDisponiblesCargados() {
		return tiposContactosDisponiblesCargados;
	}

	public void setTiposContactosDisponiblesCargados(boolean tiposContactosDisponiblesCargados) {
		this.tiposContactosDisponiblesCargados = tiposContactosDisponiblesCargados;
	}

	public EnlaceFidelizadoBean getEnlace() {
		return enlace;
	}

	public void setEnlace(EnlaceFidelizadoBean enlace) {
		this.enlace = enlace;
	}
	
	//******** ETIQUETAS-CATEGORÍAS ******** //
	public boolean isEtiquetasCategoriasCargados() {
		return etiquetasCategoriasCargados;
	}

	public void setEtiquetasCategoriasCargados(boolean etiquetasCategoriasCargados) {
		this.etiquetasCategoriasCargados = etiquetasCategoriasCargados;
	}

	public List<EtiquetaBean> getEtiquetasCategorias() {
		return etiquetasCategorias;
	}

	public void setEtiquetasCategorias(List<EtiquetaBean> etiquetasCategorias) {
		this.etiquetasCategorias = etiquetasCategorias;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public String getAnyo() {
		return anyo;
	}

	public void setAnyo(String anyo) {
		this.anyo = anyo;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getCodAlm() {
		return codAlm;
	}

	public void setCodAlm(String codAlm) {
		this.codAlm = codAlm;
	}

	public String getDesAlm() {
		return desAlm;
	}

	public void setDesAlm(String desAlm) {
		this.desAlm = desAlm;
	}

	public boolean isCodAlmFavoritoCargado() {
		return codAlmFavoritoCargado;
	}

	public void setCodAlmFavoritoCargado(boolean codAlmFavoritoCargado) {
		this.codAlmFavoritoCargado = codAlmFavoritoCargado;
	}

	public AccesoFidelizadoBean getAcceso() {
		return acceso;
	}

	public void setAcceso(AccesoFidelizadoBean acceso) {
		this.acceso = acceso;
	}
	
	public boolean isAltaRapida(){
		return altaRapida;
	}
	
	public void setAltaRapida(boolean altaRapida){
		this.altaRapida = altaRapida;
	}
	
	public Double getSaldo(){
		return saldo;
	}
	
	public void setSaldo(Double saldo){
		this.saldo = saldo;
	}
	
	public Double getSaldoProvisional(){
		return saldoProvisional;
	}
	
	public void setSaldoProvisional(Double saldoProvisional){
		this.saldoProvisional = saldoProvisional;
	}
    	
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

	public TiposContactoFidelizadoBean getTipoContacto(String codTipoContacto){
		for(TiposContactoFidelizadoBean tipoContacto : getTiposContacto()){
			if(codTipoContacto.equals(tipoContacto.getCodTipoCon())){
				return tipoContacto;
			}
		}
		return null;
	}
	
	public boolean isListasDeseosCargadas() {
		return listasDeseosCargadas;
	}

	public void setListasDeseosCargadas(boolean listasDeseosCargadas) {
		this.listasDeseosCargadas = listasDeseosCargadas;
	}

	public List<ListaDeseosFidelizadoBean> getListasDeseos() {
		return listasDeseos;
	}

	public void setListasDeseos(List<ListaDeseosFidelizadoBean> listasDeseos) {
		this.listasDeseos = listasDeseos;
	}
	
	
}