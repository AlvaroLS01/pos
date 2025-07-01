package com.comerzzia.pos.core.gui.initialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.model.Activity;
import com.comerzzia.core.facade.service.activity.ActivityServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.store.StorePos;
import com.comerzzia.omnichannel.facade.service.store.StorePosServiceFacade;
import com.comerzzia.pos.core.devices.DeviceType;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.Device;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.waitwindow.TimedWaitWindowController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.login.userselection.UserSelectionController;
import com.comerzzia.pos.core.gui.skin.CzzImageManager;
import com.comerzzia.pos.core.services.config.Environment;
import com.comerzzia.pos.core.services.config.EnvironmentSelector;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.HealthCheckService;
import com.comerzzia.pos.core.services.session.HealthCheckServiceImpl;
import com.comerzzia.pos.util.actionfiles.ActionFileEvent;
import com.comerzzia.pos.util.actionfiles.ActionFileListener;
import com.comerzzia.pos.util.actionfiles.ActionFilesManager;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.TPVConfig;
import com.comerzzia.pos.util.config.Tpv;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.version.SoftwareVersion;
import com.comerzzia.pos.util.version.SoftwareVersion.VersionInfo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class PreinitializeController extends SceneController implements Initializable, ActionFileListener, Observer {
	
	protected static final int TIMER_RESET = 60;
	
	@FXML
	protected Button btInitialize, btEnvironmentSelector, btRestartPOS;
	
	@FXML
	protected Label lbHealthCheck, lbSoftversion, lbStore, lbTill, lbConfiguration;
	
	@FXML
	protected VBox vbConfiguration;
	
	@FXML
    protected ImageView ivCustomerLogo;
	
	@FXML
	protected HBox hbNoConfig, hbConfig, hbInUse, hbFatalError;
	
	@Autowired
	protected HealthCheckService healthCheckService;
	@Autowired
    protected StorePosServiceFacade storePosService;
	@Autowired
	protected ApplicationSession sesionAplicacion;
	@Autowired
    protected CzzImageManager czzImageManager;
	@Autowired
    protected ComerzziaTenantResolver tenantResolver;
	@Autowired
    protected ActivityServiceFacade activityService;
	@Autowired
	protected CacheManager cacheManager;
	
	@Autowired
	protected VariableServiceFacade variableServiceFacade;

	protected ActionFilesManager actionFilesManager;
	
	protected boolean firedInitialization = false;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		healthCheckService.addObserver(this);
		actionFilesManager = new ActionFilesManager(this);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		showKeyboard = false;
		ivCustomerLogo.setImage(czzImageManager.getLogo(CzzImageManager.IMAGES_CUSTOMER_LOGO_NAME));
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		forceUpdateHealthCheck();
		forceProvitionalTenantSession();
		
		refreshScreenData();
	}

	@Override
	protected void initializeFocus() {
	}
	
	@Override
	public void onSceneShow() {
		clearCache();
		actionFilesManager.scheduleCheck(TIMER_RESET);
		destroyScenesBean();
		super.onSceneShow();
	}
	
	@Override
	protected void onSceneShowed() {
		if(!firedInitialization) {
			firedInitialization = true;
			btInitialize.fire();
		}
	}
	
	protected void destroyScenesBean() {
		ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(CzzActionScene.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(CzzScene.class));
		Set<BeanDefinition> beans = scanner.findCandidateComponents("com.comerzzia");
		beans.stream().forEach( bd -> {
			try {
				contextHolder.destroyBean(Class.forName(bd.getBeanClassName()));
			} catch (Exception e) {
				log.debug("loadFocus() - An error was thrown destroying the bean "+bd.getBeanClassName(), e);
			}
		});
	}

	@FXML
	public void initializeCzz() {
		openScene(InitializerController.class, new SceneCallback<Void>() {

			@Override
			public void onSuccess(Void callbackData) {
				if(AppConfig.getCurrentConfiguration().getButtonsLogin()){
					openScene(UserSelectionController.class, new SceneCallback<Void>() {
						@Override
						public void onSuccess(Void callbackData) {
							disconnectDevices();
						}
						@Override
						public void onCancel() {
							disconnectDevices();
						}
					});
				}else {
					openScene(LoginController.class, new SceneCallback<Void>() {
						@Override
						public void onSuccess(Void callbackData) {
							disconnectDevices();
		                }
						
						@Override
						public void onCancel() {
							disconnectDevices();
						}
					});
				}
			}
		});
		actionFilesManager.scheduleCancel();
	}
	
	public void disconnectDevices() {
		for(Entry<String, Device> entry:Devices.getInstance().getDevices().entrySet()) {
			if(!entry.getKey().equals(DeviceType.LINE_DISPLAY) && entry.getKey()!=null) {
				try{
					entry.getValue().disconnect();
				}catch(Throwable e){
					log.error("disconnectDevices() - An error was thrown disconnecting a device " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				}
			}
		}
	}
	
	@Override
	public boolean canClose() {
		return DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Desea cerrar la aplicación?"));
	}
	
	@FXML
	public void forceUpdateHealthCheck() {
		healthCheckService.checkStatus();
	}
	
	@FXML
	public void restartPOS() {
		if(DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Existe una actualización disponible. ¿Desea reiniciar la aplicación?"))) {
			Platform.exit();
			System.exit(POSApplication.EXIT_CODE_UPDATE);			
		}
	}
	
	protected void setNodeVisibility(Node node, boolean visibility) {
		node.setVisible(visibility);
		node.setManaged(visibility);
	}
	
	protected boolean isTPVOwner(StorePos storePos) throws FileNotFoundException, IOException {
		boolean isOwner = false;
		
		ClassPathResource uidPosFile = new ClassPathResource("uid_pos.properties");
		Properties uidPosProperties = new Properties();
		if (uidPosFile.exists()) {
			try(FileInputStream is = new FileInputStream(uidPosFile.getFile())){
				uidPosProperties.load(is);
			}
		} else {
			ClassPathResource pos_config = new ClassPathResource("pos_config.xml");
			if(!pos_config.exists()) {
				pos_config = new ClassPathResource(EnvironmentSelector.getCurrentEnvironment().getTillConfigurationFile());
			}
			String uidTpv = UUID.randomUUID().toString();
			uidPosProperties.put("uid_pos", uidTpv);
			try (FileOutputStream os = new FileOutputStream(Paths.get(pos_config.getFile().getParent(), "uid_pos.properties").toFile())) {
				uidPosProperties.store(os, "");
			}
		}
		String uidTpv = uidPosProperties.getProperty("uid_pos");
		if(storePos.getPosValidationUid()==null) {
			storePosService.updatePosValidationUid(storePos.getPosUid(), uidTpv);
			isOwner = true;
		}else {
			isOwner = uidTpv.equals(storePos.getPosValidationUid());
		}
		
		return isOwner;
	}

	@FXML
	protected void overridePosUid() {
		ClassPathResource uidPosFile = new ClassPathResource("uid_pos.properties");
		try (FileInputStream fis = new FileInputStream(uidPosFile.getFile())){
			Environment currentEnvironment = EnvironmentSelector.getCurrentEnvironment();
			Properties uidPosProperties = new Properties();
			uidPosProperties.load(fis);
			String uidTpv = uidPosProperties.getProperty("uid_pos");
			storePosService.updatePosValidationUid(currentEnvironment.getConfig().getTpv().getUidCaja(), uidTpv);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
		refreshScreenData();
	}
	

	
	protected void refreshScreenData() {
		btInitialize.setDisable(true);
		hbFatalError.setVisible(false);
		lbHealthCheck.setText(healthCheckService.getStatusMessage());
		
		Environment currentEnvironment = EnvironmentSelector.getCurrentEnvironment();
		
		boolean isOkStatus = healthCheckService.getStatusId().equals(HealthCheckServiceImpl.STATUS_OK);
		boolean isConfigured =currentEnvironment.getConfig()!=null && currentEnvironment.getConfig().getTpv() !=null;
		
		btEnvironmentSelector.setVisible(EnvironmentSelector.getAvailableEnvironments().entrySet().size()>1);
		
		vbConfiguration.setVisible(isOkStatus);
		lbConfiguration.setVisible(isOkStatus);
		
		if(!isOkStatus) {
			return;
		}
		
		setNodeVisibility(hbConfig, isConfigured);
		setNodeVisibility(hbNoConfig, !isConfigured);
		if(!isConfigured) {
			return;
		}
		
		Tpv tpvConfig = currentEnvironment.getConfig().getTpv();
		StorePos storePos = storePosService.getStorePos(tpvConfig.getUidCaja());
		lbStore.setText(storePos.getStoreCode());
		lbTill.setText(storePos.getTillCode());
		
		boolean isOwner = false;
		try {
			isOwner = isTPVOwner(storePos);
			hbInUse.setVisible(!isOwner);
			btInitialize.setDisable(!isOwner);
		} catch (Exception e) {
			log.error("refreshScreenData() - A critical error was thrown: ", e);
			hbFatalError.setVisible(true);
		}
		VersionInfo softwareVersion = SoftwareVersion.get();
		lbSoftversion.setText(softwareVersion.toString());
		if(softwareVersion.isLocalCopyUpdated()) {
			lbSoftversion.getStyleClass().clear();
			lbSoftversion.getStyleClass().add("label");
		}else {
			lbSoftversion.getStyleClass().add("lbError");
		}
		btRestartPOS.setDisable(softwareVersion.isLocalCopyUpdated());
		btRestartPOS.setVisible(!softwareVersion.isLocalCopyUpdated());
		
	}
	
	@FXML
	protected void openEnvironmentSelector() {
		actionFilesManager.scheduleCancel();
		openScene(EnvironmentselectorController.class, new SceneCallback<Environment>() {
			public void onSuccess(Environment selected) {
				Environment current = EnvironmentSelector.getCurrentEnvironment();				
				EnvironmentSelector.setCurrentEnvironment(selected);
				sesionAplicacion.updatedEnvironment(current, selected);
				forceProvitionalTenantSession();
				refreshScreenData();
			}
			@Override
			public void onCancel() {
			}
		});
	}
	
	@Override
	public void actionFileEventPerformed(ActionFileEvent event) {
		if("update".equals(event.getEventName()) || "forcedupdate".equals(event.getEventName())) {
			askCloseByUpdate(event, "forcedupdate".equals(event.getEventName()));
		}

	}
	
	protected void askCloseByUpdate(ActionFileEvent event, boolean force) {
		Platform.runLater(() -> {
			HashMap<String, Object> stageData = new HashMap<>();
			stageData.put(TimedWaitWindowController.PARAM_TIEMPO_MS, 30000);
			stageData.put(TimedWaitWindowController.PARAM_FORCE_UPDATE, force);
			openModalScene(TimedWaitWindowController.class, new SceneCallback<Void>(){

				@Override
				public void onSuccess(Void callbackData) {
					log.info("Reinicio automático aceptado.");
					event.actionPerformed();
					System.exit(event.getExitCode());
				}

				@Override
				public void onCancel() {
					log.info("Reinicio automático cancelado.");
					event.actionPerformed();
					actionFilesManager.scheduleCheck(TIMER_RESET);
				}
			}, stageData);
		});
    }

	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {
			public void run() {
				refreshScreenData();
			}
		});
	}
	
	@Override
	public void onClose() {
		actionFilesManager.scheduleCancel();
		if(Devices.getInstance().getLineDisplay() != null) {
			try {
				Devices.getInstance().getLineDisplay().disconnect();
			} catch (DeviceException e) {
				log.warn("onClose() - An error was thrown disconnecting the line display device. The POS may not be closed.", e);
			}
		}
		super.onClose();
	}
	
	protected void forceProvitionalTenantSession() {
		try {
			TPVConfig tpvConfig = ApplicationSession.getTpvConfig();
			String configuredActivityUid = tpvConfig.getTpv().getUidActividad();
			
			Activity actividad = activityService.findById(configuredActivityUid);
			ComerzziaSession tenantSession = new ComerzziaSession();
			tenantSession.setActivityUid(actividad.getActivityUid());
			tenantSession.setInstanceUid(actividad.getInstanceUid());
			
			tenantResolver.forceCurrentTenantSession(tenantSession);
		}catch(Exception e) {
			log.error("forceProvitionalTenantSession() - An error was thrown setting the configured activity uid", e);
		}
	}

	@Override
	public void onInactivity() {/* Do noting */}
	
	@SuppressWarnings("unchecked")
	protected void clearCache() {
		variableServiceFacade.invalidateCache();

		for (String name : cacheManager.getCacheNames()) {
			Cache cache = cacheManager.getCache(name);
			
			if (cache.getNativeCache() instanceof ConcurrentHashMap) {
				ConcurrentHashMap<String, Object> nativeCache = (ConcurrentHashMap<String, Object>) cacheManager
						.getCache(name).getNativeCache();
				
				
				if (nativeCache.size() > 0) {
					Object firstObject = nativeCache.values().iterator().next();
					
					if (firstObject instanceof AutoCloseable) {
						nativeCache.forEach((k,v) -> {
							try {
								((AutoCloseable)v).close();
							} catch (Exception e) {
								log.warn("Error cerrando objeto de la cache " + name + ": " + e.getMessage());
							}
						});
					} 						
				}
			}
						
			cache.clear();       
	    }		
	}
	
}
