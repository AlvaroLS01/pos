package com.comerzzia.api.loyalty.service.accounts.activities;

import java.sql.SQLException;
import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.persistence.accounts.activities.ParametrosBuscarMovimientosBean;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.servicios.ventas.tickets.TicketException;
import com.comerzzia.core.util.db.Connection;

public interface AccountsActivitiesService {

	List<AccountActivity> consultar(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param);

	Double consultarTotalSalida(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param);

	Double consultarTotalEntrada(IDatosSesion datosSesion, ParametrosBuscarMovimientosBean param);

	/**
	 * Obtiene la lista de movimientos que no han sido procesados para la actualización del saldo.
	 * 
	 * @param datosSesion
	 * @param conn
	 * @return
	 * @throws MovimientoException
	 */
	List<AccountActivity> consultarMovimientosNoProcesados(IDatosSesion datosSesion, Long idCuenta);


	void insert(AccountActivity activity, IDatosSesion datosSesion);

	void insert(AccountActivity activity, Account sourceAccount, IDatosSesion datosSesion);
	
	void modificarMovimiento(AccountActivity movimiento, IDatosSesion datosSesion,
			Integer nuevoEstado) throws ApiException;


	AccountActivity consultar(IDatosSesion datosSesion, String uidTransaccion);

	/**
	 * Función que busca el movimiento generado por anulación de otro movimiento
	 * @param configEmpresa
	 * @param uidTransaccionBuscado - uid de transaccion que vamos a buscar en el campo Documento
	 * @param cadenaAnulacion - cadena que vamos a buscar en el campo concepto, por defecto será ANULACION
	 * @return 
	 * @throws MovimientoException 
	 */
	AccountActivity consultarAnulacion(IDatosSesion datosSesion, String uidTransaccionBuscado,
			String cadenaAnulacion);

	void anularMovimientoProvisional(AccountActivity movimiento, IDatosSesion datosSesion) throws ApiException;

	/**
	 * Modifica un movimiento identificado por un uid de transacción
	 * @param numeroTarjetaFidelizacion
	 * @param uidTransaccionTarjetaRegalo
	 * @param config
	 * @throws TicketException 
	 * @throws TarjetaException 
	 * @throws TarjetaNotFoundException 
	 * @throws ApiException 
	 * @throws SQLException 
	 * @throws MovimientoConstraintViolationException 
	 * @throws MovimientoException 
	 */
	void modificarMovimientos(String numeroTarjetaFidelizacion,
			String uidTransaccionTarjetaRegalo, IDatosSesion datosSesion)
			throws TicketException, ApiException;

	void anularMovimientosProvisionalesTicket(Connection conn, IDatosSesion datosSesion, String uidTicket) throws ApiException;

	void crearMovimientos(List<AccountActivity> movimientos, IDatosSesion datosSesion);

	void updateByPrimaryKey(AccountActivity movimiento, IDatosSesion datosSesion);

}