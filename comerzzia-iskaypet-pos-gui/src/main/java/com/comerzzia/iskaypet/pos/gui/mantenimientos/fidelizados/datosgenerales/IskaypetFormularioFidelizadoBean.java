package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Component
@Scope("prototype")
public class IskaypetFormularioFidelizadoBean extends FormularioGui {

	@Size(max = 30, message = "La longitud del campo Código no puede superar los {max} caracteres.")
	private String codigo;
	private String numeroTarjeta;
	@NotEmpty(message = "Debe rellenar el campo nombre")
	@Size(max = 45, message = "La longitud del campo Nombre no puede superar los {max} caracteres.")
	private String nombre;
	@Size(max = 45, message = "La longitud del campo Apellidos no puede superar los {max} caracteres.")
	private String apellidos;
	@NotNull(message = "Debe seleccionar un tipo de documento")
	@NotEmpty(message = "Debe seleccionar un tipo de documento")
	private String tipoDocumento;
	@NotEmpty(message = "Debe rellenar el campo documento")
	@Size(max = 20, message = "La longitud del campo Documento no puede superar los {max} caracteres.")
	private String documento;
	private String sexo;
	private String estadoCivil;

	@Past(message = "La fecha debe ser anterior a la fecha actual")
	private Date fechaNacimiento;
	@NotEmpty(message = "Debe rellenar el campo email")
	@Size(max = 255, message = "La longitud del campo Email no puede superar los {max} caracteres.")
	@Pattern(regexp = "^(?!.*([.-])\\1)(?!.*[.-]{2})[\\w.-]+@[\\w.-]+\\.\\w{2,10}$", message = "El campo Email no tiene un formato válido.")
	private String email;
	@NotEmpty(message = "Debe rellenar el campo movil")
	@Size(min = 9, message = "La longitud del campo Móvil no puede ser inferior a {min} caracteres.")
	@Pattern(regexp = "^\\+?\\d{1,3}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}$", message = "El campo Movil no tiene un formato válido.")
	private String movil;
	
	// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
	@Size(min = 9, message = "La longitud del campo Teléfono Fijo no puede ser inferior a {min} caracteres.")
	@Pattern(regexp = "^\\+?\\d{1,3}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}$", message = "El campo Teléfono Fijo no tiene un formato válido.")
	private String telefonoFijo;
	
	@NotEmpty(message = "Debe rellenar el campo país")
	@Size(max = 4, message = "La longitud del campo Código de Pais no puede superar los {max} caracteres.")
	private String codPais;
	private String desPais;
	
	// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
	@NotEmpty(message = "Debe rellenar el campo Idioma")
	@Size(max = 6, message = "La longitud del campo Código del Idioma no puede superar los {max} caracteres.")
	private String codLenguaje;
	private String desLenguaje;
	
	@NotEmpty(message = "Debe rellenar el campo código postal")
	@Size(max = 8, message = "La longitud del campo Código Postal no puede superar los {max} caracteres.")
	private String cp;
	@Size(max = 50, message = "La longitud del campo Provincia no puede superar los {max} caracteres.")
	private String provincia;
	@Size(max = 50, message = "La longitud del campo Población no puede superar los {max} caracteres.")
	private String poblacion;
	@NotEmpty(message = "Debe rellenar el campo localidad")
	@Size(max = 50, message = "La Longitud del campo Localidad no puede superar los {max} caracteres.")
	private String localidad;
	@Size(max = 50, message = "La longitud del campo Domicilio no puede superar los {max} caracteres.")
	private String domicilio;
	@Size(max = 255, message = "La longitud del campo Observaciones no puede superar los {max} caracteres.")
	private String observaciones;
	private String codAlmFav;
	private String desAlmFav;
	private String codColectivo;
	private String desColectivo;


	// Constructores
	public IskaypetFormularioFidelizadoBean() {
	}

	@Override
	public void limpiarFormulario() {
		this.codigo = "";
		this.numeroTarjeta = "";
		this.nombre = "";
		this.apellidos = "";
		this.tipoDocumento = null;
		this.documento = "";
		this.fechaNacimiento = null;
		this.sexo = null;
		this.estadoCivil = null;
		this.email = "";
		this.movil = "";
		this.codPais = "";
		this.desPais = "";
		this.cp = "";
		this.provincia = "";
		this.poblacion = "";
		this.localidad = "";
		this.domicilio = "";
		this.codAlmFav = "";
		this.desAlmFav = "";
		this.codColectivo = "";
		this.desColectivo = "";
		this.observaciones = "";
		this.codLenguaje = "";
		this.desLenguaje = "";
		this.telefonoFijo = "";
	}

	// Getter y Setters
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMovil() {
		return movil;
	}

	public void setMovil(String movil) {
		this.movil = movil;
	}

	public String getCodPais() {
		return codPais;
	}

	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}

	public String getDesPais() {
		return desPais;
	}

	public void setDesPais(String desPais) {
		this.desPais = desPais;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getCodAlmFav() {
		return codAlmFav;
	}

	public void setCodAlmFav(String codAlmFav) {
		this.codAlmFav = codAlmFav;
	}

	public String getDesAlmFav() {
		return desAlmFav;
	}

	public void setDesAlmFav(String desAlmFav) {
		this.desAlmFav = desAlmFav;
	}

	public String getCodColectivo() {
		return codColectivo;
	}

	public void setCodColectivo(String codColectivo) {
		this.codColectivo = codColectivo;
	}

	public String getDesColectivo() {
		return desColectivo;
	}

	public void setDesColectivo(String desColectivo) {
		this.desColectivo = desColectivo;
	}

	public String getCodLenguaje() {
		return codLenguaje;
	}

	public void setCodLenguaje(String codLenguaje) {
		this.codLenguaje = codLenguaje;
	}

	public String getDesLenguaje() {
		return desLenguaje;
	}

	public void setDesLenguaje(String desLenguaje) {
		this.desLenguaje = desLenguaje;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}
	
}
