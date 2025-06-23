package com.comerzzia.bimbaylola.pos.services.ticket.aparcados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoExample;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;

@Component
@Primary
public class ByLTicketsAparcadosService extends TicketsAparcadosService{

	@Autowired
	protected VariablesServices variableService;
	
	String idVariableGestionCaja = "GESTION.CAJA";
	
	/**
	 * Realiza una consulta a base de datos para saber si tiene tickets aparcados y los cuenta.
	 * Comprueba el tipo de gestión caja que tiene para buscar de una forma o otra.
	 * @param idTipoDocumento : Indica el tipo de documento, en la mayoría de casos se lo pasamos a null.
	 * @return Número de tickets aparcados que tiene.
	 */
	public int countTicketsAparcados(Long idTipoDocumento){
		
        SqlSession sqlSession = new SqlSession();
        
        log.debug("countTicketsAparcados() - Consultando ticket aparcado en base de datos...");
        
        try{
        	
            sqlSession.openSession(SessionFactory.openSession());
            
            TicketAparcadoExample example = new TicketAparcadoExample();
            TicketAparcadoExample.Criteria criteria = example.createCriteria();
            /* Comprobamos el tipo de gestión de caja. */
            if(variableService.getVariableAsString(idVariableGestionCaja).equals("N")){
	            criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
	                    .andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
	                    .andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
            }else{
            	criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
                		.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
            }
            
            if(idTipoDocumento != null){
                criteria.andIdTipoDocumentoEqualTo(idTipoDocumento);
            }
            
            criteria.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);
            /* Devolvemos una variable INT con el total de tickets aparcados. */
            return ticketAparcadoMapper.countByExample(example);
            
        }finally{
            sqlSession.close();
        }
        
    }

	/**
	 * Realiza una consulta a base de datos para saber si tiene tickets aparcados y los cuenta.
	 * A diferencia del anterior método, aquí no se realiza comprobación del tipo de gestión de caja
	 * porque tiene que ser solo los tickets locales.
	 * @param idTipoDocumento : Indica el tipo de documento.
	 * @return Número de tickets aparcados que tiene.
	 */
	public int countTicketsAparcadosParaLabel(Long idTipoDocumento){
		
        SqlSession sqlSession = new SqlSession();
        
        log.debug("countTicketsAparcados() - Consultando ticket aparcado en base de datos...");
        
        try{
        	
            sqlSession.openSession(SessionFactory.openSession());
            
            TicketAparcadoExample example = new TicketAparcadoExample();
            TicketAparcadoExample.Criteria criteria = example.createCriteria();
            criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
                    .andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
                    .andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
          
            if(idTipoDocumento != null){
                criteria.andIdTipoDocumentoEqualTo(idTipoDocumento);
            }
            
            criteria.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);
            /* Devolvemos una variable INT con el total de tickets aparcados. */
            return ticketAparcadoMapper.countByExample(example);
            
        }finally{
            sqlSession.close();
        }
        
    }
	
}
