package com.comerzzia.iskaypet.pos.services.valesdevolucion;

import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucion;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionExample;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionKey;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucionMapper;
import com.comerzzia.iskaypet.pos.services.valesdevolucion.exception.ValesDevolucionNotFoundException;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class ValesDevolucionService {

    private static final Logger log = Logger.getLogger(ValesDevolucionService.class);

    @Autowired
    protected Sesion sesion;
    @Autowired
    protected ValeDevolucionMapper valesDevolucionMapper;

    public ValeDevolucion consultarValeDevolucion(String numeroTarjetaV) throws Exception {
        log.debug("consultarValeDevolucion() - Consultando vale de devolucion con numero: " + numeroTarjetaV);

        ValeDevolucionKey key = new ValeDevolucionKey();
        key.setUidInstancia(sesion.getAplicacion().getUidInstancia());
        key.setNumeroTarjeta(numeroTarjetaV);

        ValeDevolucion valeDev = valesDevolucionMapper.selectByPrimaryKey(key);

        if (valeDev == null) {
            log.error("consultarValeDevolucion() - No se ha encontrado ningun vale de devolución con numero: " + numeroTarjetaV);
            throw new ValesDevolucionNotFoundException();
        }

        return valeDev;
    }

    public void eliminarValesDevExpirados() throws Exception {
        log.debug("eliminarValesDevExpirados() - Eliminando vales de devolucion con más de una hora...");

        ValeDevolucionExample example = new ValeDevolucionExample();
        Criteria criteria = example.createCriteria();
        criteria.andUidInstanciaEqualTo(sesion.getAplicacion().getUidInstancia());
        criteria.andFechaActivacionLessThan(Fechas.sumaHoras(new Date(), -1));

        int result = valesDevolucionMapper.deleteByExample(example);

        log.debug("eliminarValesDevExpirados() - Se han eliminado " + result + " vales de devolucion");
    }


    /*
     *
     */
    public void guardarValesDevolucion(IskaypetTicketManager ticketManager, List<PagoTicket> lstValesDevolucion) throws Exception {
        log.debug("guardarValesDevolucion() - Insertando vale de devolucion...");

        int result = 0;
        String msgError = "";
        try {
            // Recorremos los pagoTicket
            for (PagoTicket valeDevolucionPago : lstValesDevolucion) {

                result = 0;

                if (valeDevolucionPago.getGiftcards() != null && !valeDevolucionPago.getGiftcards().isEmpty()) {

                    GiftCardBean giftCard = valeDevolucionPago.getGiftcards().get(0);
                    ValeDevolucion valeDevolucion = null;

                    // Si es devolucion debemos insertarlo en bbdd
                    if (ticketManager.isEsDevolucion()) {

                        valeDevolucion = new ValeDevolucion();
                        // Cargamos objeto vale devolucion
                        valeDevolucion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
                        valeDevolucion.setNumeroTarjeta(giftCard.getNumTarjetaRegalo());
                        valeDevolucion.setSaldoDisponible(giftCard.getSaldo());
                        valeDevolucion.setFechaActivacion(new Date());

                        result = valesDevolucionMapper.insert(valeDevolucion);

                        // Si no se ha guardado lanzamos excepcion
                        if (result == 0) {
                            msgError = "No se ha podido insertar el vale de devolución en tienda";
                            log.error("guardarValesDevolucion() - " + msgError);
                            throw new Exception(msgError);
                        } else {
                            log.debug("guardarValesDevolucion() - Se ha guardado el vale de devolucion con número " + giftCard.getNumTarjetaRegalo() + " correctamente...");
                        }

                    } else {

                        ValeDevolucion valeDevConsultado;

                        // Consultamos para ver si existe en tienda
                        try {
                            valeDevConsultado = consultarValeDevolucion(giftCard.getNumTarjetaRegalo());
                        } catch (Exception e) {
                            if (e instanceof ValesDevolucionNotFoundException) {
                                valeDevConsultado = null;
                            } else {
                                throw new Exception(e);
                            }
                        }

                        // Si existe en tienda significaría que ha sido creado en una devolución de la tienda en la
                        // última hora
                        // Por lo tanto actualizamos el saldo ya que significa que ha sido usado en una venta
                        if (valeDevConsultado != null) {

                            valeDevolucion = new ValeDevolucion();
                            valeDevolucion.setUidInstancia(valeDevConsultado.getUidInstancia());
                            valeDevolucion.setNumeroTarjeta(valeDevConsultado.getNumeroTarjeta());

                            // Actualizamos el saldo
                            BigDecimal saldoActualizado = valeDevConsultado.getSaldoDisponible().subtract(giftCard.getImportePago().abs());
                            valeDevolucion.setSaldoDisponible(saldoActualizado);

                            valeDevolucion.setFechaActivacion(valeDevConsultado.getFechaActivacion());

                            result = valesDevolucionMapper.updateByPrimaryKey(valeDevolucion);

                            if (result == 0) {
                                msgError = "No se ha podido actualizar el vale de devolución en tienda";
                                log.error("guardarValesDevolucion() - " + msgError);
                                throw new Exception(msgError);
                            } else {
                                log.debug("guardarValesDevolucion() - Se ha actualizado el vale de devolucion con número " + giftCard.getNumTarjetaRegalo() + " correctamente...");
                            }
                        }

                    }
                } else {
                    log.error("guardarValesDevolucion() - No existe en el pago de vale de devolucion los datos de gift card para salvarlo en tienda...");
                }
            }
        } catch (Exception e) {
            log.error("guardarValesDevolucion() - Ha ocurrido un error al guardar el vale de devolución en bbdd " + e.getMessage());
            throw new Exception(e.getMessage(), e);
        }
    }

}
