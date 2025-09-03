package com.comerzzia.iskaypet.pos.services.proformas.rest.classes;


import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.cliente.ClienteDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas.LineaProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.pagos.PagoProformaDTO;

import java.util.List;


public class ProformaDTO extends ProformaHeaderDTO {

    private List<LineaProformaDTO> lineas;

    private List<PagoProformaDTO> pagos;

    private ClienteDTO cliente;

    public ProformaDTO() {
    }

    public ProformaDTO(List<LineaProformaDTO> lineas, List<PagoProformaDTO> pagos, ClienteDTO cliente) {
        this.lineas = lineas;
        this.pagos = pagos;
        this.cliente = cliente;
    }

    public ProformaDTO(String uidActividad, String idProforma, String sistemaOrigen, String tipoDocumento, String fechaProforma, Long idAlbaran, boolean automatica, String idProformaOrigen, String documentoOrigen, String localizador, String almacen, String nombreCliente, String estadoActual, String fechaCreacion, String fechaModificacion, List<LineaProformaDTO> lineas, List<PagoProformaDTO> pagos, ClienteDTO cliente) {
        super(uidActividad, idProforma, sistemaOrigen, tipoDocumento, fechaProforma, idAlbaran, automatica, idProformaOrigen, documentoOrigen, localizador, almacen, nombreCliente, estadoActual, fechaCreacion, fechaModificacion);
        this.lineas = lineas;
        this.pagos = pagos;
        this.cliente = cliente;
    }

    public List<LineaProformaDTO> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaProformaDTO> lineas) {
        this.lineas = lineas;
    }

    public List<PagoProformaDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoProformaDTO> pagos) {
        this.pagos = pagos;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }
}




