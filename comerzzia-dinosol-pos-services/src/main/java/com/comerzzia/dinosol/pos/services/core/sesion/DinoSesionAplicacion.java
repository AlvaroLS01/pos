package com.comerzzia.dinosol.pos.services.core.sesion;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.api.v2.sorteos.client.LotteryApiApi;
import com.comerzzia.api.v2.sorteos.client.model.SorteoDto;
import com.comerzzia.api.virtualmoney.client.AccountsApi;
import com.comerzzia.api.virtualmoney.client.AccountsBalancesApi;
import com.comerzzia.api.virtualmoney.client.PinTarjetasWalletApi;
import com.comerzzia.api.virtualmoney.client.RestrictionsApi;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.dinosol.api.client.loyalty.CouponsApi;
import com.comerzzia.dinosol.api.client.loyalty.LoyaltyOperationsApi;
import com.comerzzia.dinosol.pos.services.sorteos.SorteosService;
import com.comerzzia.dinosol.pos.util.text.DinoTextUtils;
import com.comerzzia.pos.services.core.sesion.SesionAplicacion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.util.text.TextUtils;

@Component
@Primary
public class DinoSesionAplicacion extends SesionAplicacion {

	public static String FILENAME_MASTER_HOSTNAME = "master_url";

	@Autowired
	private ComerzziaApiManager comerzziaApiManager;
	@Autowired
	private SorteosService sorteoService;
	
	private boolean cajaMasterActivada;

	private String urlCajaMaster;

	private String urlDinoWsCajaMaster;
	
	private List<SorteoDto> listaSorteos;

	@Override
	public void init() throws SesionInitException {
		super.init();

		Scanner scanner = null;
		try {
			log.info("Buscando la URL de la caja máster en el fichero " + FILENAME_MASTER_HOSTNAME);
			URL urlFile = Thread.currentThread().getContextClassLoader().getResource(FILENAME_MASTER_HOSTNAME);
			if (urlFile != null) {
				File fileUrlMaster = new File(urlFile.getPath());
				if (fileUrlMaster.exists()) {
					scanner = new Scanner(fileUrlMaster);
					if (scanner.hasNextLine()) {
						urlCajaMaster = scanner.nextLine();

						if (StringUtils.isNotBlank(urlCajaMaster)) {
							log.info("URL de la caja máster: " + urlCajaMaster);
							com.comerzzia.instoreengine.master.rest.path.InStoreEngineWebservicesMasterPath.initPath(urlCajaMaster);
							cajaMasterActivada = true;
						}
						else {
							log.error("El fichero está vacío.");
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("init() - Ha habido un error al buscar el fichero del hostaname de la caja master: " + e.getMessage(), e);
		}
		finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		if (StringUtils.isNotBlank(urlCajaMaster)) {
			urlDinoWsCajaMaster = urlCajaMaster;
			if (urlDinoWsCajaMaster.endsWith("/")) {
				urlDinoWsCajaMaster = urlDinoWsCajaMaster.substring(0, urlDinoWsCajaMaster.length() - 1);
			}
			urlDinoWsCajaMaster = urlDinoWsCajaMaster + "-dino";
		}

		comerzziaApiManager.registerAPI("AccountsApi", AccountsApi.class, "virtualmoney");
		comerzziaApiManager.registerAPI("AccountsBalancesApi", AccountsBalancesApi.class, "virtualmoney");
		comerzziaApiManager.registerAPI("PinTarjetasWalletApi", PinTarjetasWalletApi.class, "virtualmoney");
		comerzziaApiManager.registerAPI("RestrictionsApi", RestrictionsApi.class, "virtualmoney");

		comerzziaApiManager.registerAPI("CouponsApi", CouponsApi.class, "loyalty");
		comerzziaApiManager.registerAPI("LoyaltyOperationsApi", LoyaltyOperationsApi.class, "loyalty");

		comerzziaApiManager.registerAPI("LotteryApiApi", LotteryApiApi.class, "lottery");

		TextUtils.setCustomInstance(new DinoTextUtils());
		
		cargarConfiguracionSorteo();
	}
	
	private void cargarConfiguracionSorteo() {
		List<SorteoDto> listaSorteosApi = sorteoService.getRaffles();
		if (listaSorteosApi != null && !listaSorteosApi.isEmpty()) {
			for (SorteoDto sorteoDto : listaSorteosApi) {
				if (listaSorteos == null) {
					listaSorteos = new ArrayList<SorteoDto>();
				}
				listaSorteos.add(sorteoDto);
				saveRaffleConfigurationFile(sorteoDto);
			}
		}
	}
	
	public void saveRaffleConfigurationFile(SorteoDto sorteo) {
		String filePath = "raffle_" + sorteo.getIdSorteo() + "_configuration.xml";
		try {
			log.debug("saveRaffleConfigurationFile() - Guardando fichero " + filePath);

			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource("entities");

			String folderPath = url.getPath();

			File archivo = new File(folderPath + File.separator + filePath);
			FileWriter writer = new FileWriter(archivo);
			
			log.debug("saveRaffleConfigurationFile() - Contenido: " + sorteo.getConfiguracion());
			writer.write(sorteo.getConfiguracion());

			writer.close();
		} catch (Exception e) {
			log.error("saveRaffleConfigurationFile() - Ha habido un error al guardar el fichero de configuracion del sorteo " + filePath + ": " + e.getMessage(), e);
		}
	}

	public boolean isCajaMasterActivada() {
		return cajaMasterActivada;
	}

	public String getUrlDinoWsCajaMaster() {
		return urlDinoWsCajaMaster;
	}

	public List<SorteoDto> getListaSorteos() {
		return listaSorteos;
	}

	public void setListaSorteos(List<SorteoDto> listaSorteos) {
		this.listaSorteos = listaSorteos;
	}

}