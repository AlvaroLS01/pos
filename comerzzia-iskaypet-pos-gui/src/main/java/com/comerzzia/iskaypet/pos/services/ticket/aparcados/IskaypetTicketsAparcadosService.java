package com.comerzzia.iskaypet.pos.services.ticket.aparcados;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoExample;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@Primary
@Service
public class IskaypetTicketsAparcadosService extends TicketsAparcadosService {

    public int countTicketsAparcadosTotales(Long idTipoDocumento) {
        SqlSession sqlSession = new SqlSession();
        TicketAparcadoExample example = null;
        try {
            log.debug("countTicketsAparcados() - Consultando ticket aparcado en base de datos...");
            sqlSession.openSession(SessionFactory.openSession());
            example = new TicketAparcadoExample();

            TicketAparcadoExample.Criteria exampleCriteria1 = example.or();
            TicketAparcadoExample.Criteria exampleCriteria2 = example.or();

            exampleCriteria1.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());

            exampleCriteria2.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());

            if(idTipoDocumento != null) {
                TipoDocumentoBean documento;
                documento = documentos.getDocumento(idTipoDocumento);

                String codTipoDocumentoFacturaCompleta = documento.getTipoDocumentoFacturaDirecta();
                TipoDocumentoBean tipoDocumentoFacturaCompleta = documentos.getDocumento(codTipoDocumentoFacturaCompleta);
                Long idTipoDocumentoFacturaCompleta = tipoDocumentoFacturaCompleta.getIdTipoDocumento();

                exampleCriteria1.andIdTipoDocumentoEqualTo(idTipoDocumento);
                exampleCriteria2.andIdTipoDocumentoEqualTo(idTipoDocumentoFacturaCompleta);
            }

            exampleCriteria1.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);
            exampleCriteria2.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);

        } catch (Exception e) {
            String msg = "Se ha producido un error durante el recuento de tickets apartados - " + e.getMessage();;
            log.error("countTicketsAparcados() - " + msg, e);
        }
        finally {
            sqlSession.close();
        }

        return ticketAparcadoMapper.countByExample(example);
    }


}
