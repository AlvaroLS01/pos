package com.comerzzia.pos.core.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.comerzzia.pos.core.gui.exception.PreinitializationExceptionController;
import com.comerzzia.pos.core.gui.exception.PreinitializationExceptionView;
import com.comerzzia.pos.core.gui.view.CssScene;
import com.comerzzia.pos.core.gui.view.SceneView;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;

@Log4j
public class POSApplication extends Application {
	private static final String COMERZZIA_HOME_ENV_VAR = "COMERZZIA_HOME";
	
	public static final int EXIT_CODE_NORMAL = 0;
	public static final int EXIT_CODE_UPDATE = 10;
	public static final int EXIT_CODE_ALREADY_RUNNING = 100;

	protected ConfigurableApplicationContext applicationContext;
	public static Class<?> source;
	public static String[] args = {""};
	public static int exitCode = EXIT_CODE_NORMAL;

	/**
	 * Indica si han ocurrido alguna excepcion antes de la carga del Stage principal
	 */
	protected Throwable initException;
	/**
	 * Es llamado antes de start() y mientras se ejecuta el Preloader. No es llamado
	 * dentro del UI Thread.
	 * 
	 * @throws Exception
	 */
	@Override
	public void init() {
		MDC.put("user", "boot");
		
		loadPosConfiguration();
		
		try {
			long time = System.currentTimeMillis();

			applicationContext = new SpringApplicationBuilder(source).headless(false).run(args);

			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					log.error("Error no controlado en thread: " + t + " Exception: " + e, e);
				}
			});

			
			log.debug(String.format("init() - Spring context inicializado en %d milisegundos", System.currentTimeMillis() - time));

			// Inicializamos el URLHandler
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				public URLStreamHandler createURLStreamHandler(String protocol) {
					if (protocol.equalsIgnoreCase("cmzpos") || protocol.equalsIgnoreCase("czzpos")) {
						log.debug(String.format("createURLStreamHandler() - " + "url con protocolo '%s' detectado", protocol));
						return new POSURLHandler();
					} else if (protocol.equalsIgnoreCase("nousa")) {
						// Redireccionado desde POSURLHandler
						return null;
					}
					return null;
				}
			});

		} catch (Exception e) {
			// Capturamos la excepcion y la mostraremos cuando se ejecute start()
			log.error("init() " + e.getMessage(), e);
			initException = e;
		}
	}

	/**
	 * Inicializa la aplicacion. Es llamado despues de ocultar el Preloader por lo
	 * que debe ser ligero para ejecutarse rapido. Pasar todo el trabajo pesado a
	 * init() que es llamado en otro Thread.
	 *
	 * @param primaryStage
	 */
	@Override
	public void start(Stage stage) {
		try {
			log.debug("start() - Starting application");
			
			if (applicationContext == null) {
				// If the application context is null it means that an exception was thrown in the
				// init() method and the application can't start
				checkInitExceptions(); 
				return;
			}
			applicationContext.publishEvent(new StageReadyEvent(stage));
		} catch (Throwable ex) {
			log.error("start() " + ex.getMessage(), ex);
			if(initException == null) {
				initException = ex; //If an exception was already thrown that's the real cause of the application not starting
			}
			checkInitExceptions();
		}
	}

	@Override
	public void stop() throws Exception {
		log.info("stopping application.....");
		applicationContext.close();
		Platform.exit();
		System.exit(exitCode);
	}

	protected boolean checkInitExceptions() {
		if (initException != null) {
			Stage stage = new Stage();
			Pane root = new Pane();
			stage.setScene(new CssScene(root));
			stage.setMinWidth(1024);
			stage.setMinHeight(768);
			try {
				FXMLLoader loader = new FXMLLoader(SceneView.getFXMLResource(SceneView.getFXMLName(PreinitializationExceptionView.class)));
				PreinitializationExceptionController controller = new PreinitializationExceptionController();
				controller.setStage(stage);
				loader.setController(controller);
				loader.load();
				controller.setException(initException);
				stage.getScene().setRoot(loader.getRoot());
			} catch (Exception e) {
				log.error("Error initializing exception scene: ",e);
				buildFallbackExceptionScene(stage, root, e);
			
			}
			stage.show();
			stage.centerOnScreen();
			
			return true;
		}
		return false;
	}
	
	protected void buildFallbackExceptionScene(Stage stage, Pane root, Throwable e) {
		VBox vBox = new VBox();
		Label label = new Label();
		TextArea ta = new TextArea();
		Button bt = new Button("Cerrar");
		final StringWriter salida = new StringWriter();
		try (PrintWriter psalida = new PrintWriter(salida)) {
			initException.printStackTrace(psalida);
			ta.setText(salida.toString());
		}
		ta.setEditable(false);
		String message = "An error was thrown initializing the application.";
		label.setText(message);
		bt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				stage.close();
			}
		});
		bt.getStyleClass().add("btn");

		vBox.getChildren().add(label);
		vBox.getChildren().add(ta);
		vBox.getChildren().add(bt);
		root.getChildren().add(vBox);

	}

	protected static void loadPosConfiguration() {
		ClassPathResource configFile = new ClassPathResource("comerzzia-pos.yml");
		Properties properties;
		
		if (!configFile.exists()) {
			configFile = new ClassPathResource("comerzzia-pos.json");
			
			if (!configFile.exists()) {
				throw new RuntimeException("POS configuration file comerzzia-pos.(yml|json) not found");
			}
		}
		
		YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
		
		factoryBean.setResources(configFile);
		
		try {
			log.info("Loading POS configuration file from " + configFile.getDescription() + " : " + configFile.getURL().toString());
			
			properties = factoryBean.getObject();
		} catch (Exception e) {
			log.error("Error reading POS configuration file: " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
							
		
		ConfigurationPropertySource propertySource = new MapConfigurationPropertySource(properties);

		Binder binder = new Binder(propertySource);

		AppConfigData configData = binder.bind("comerzzia.pos", AppConfigData.class).orElse(new AppConfigData()); // same prefix as @ConfigurationProperties
		
		initComerzziaHomePath(configData);

		AppConfig.setConfigData(configData);
		
		loadConfiguredLocale();
	}
	
	protected static void loadConfiguredLocale() {
		String countryCode = AppConfig.getCurrentConfiguration().getCountry();
		String languageCode = AppConfig.getCurrentConfiguration().getLanguage();
		if (StringUtils.isBlank(countryCode)) {
			countryCode = Locale.getDefault().getCountry();
		}
		if (StringUtils.isBlank(languageCode)) {
			languageCode = Locale.getDefault().getLanguage();
		}
		Locale.setDefault(new Locale(languageCode, countryCode));
	}


	/**
	 * Obtiene la ruta de trabajo de comerzzia. Esta ruta vendra dada por el valor
	 * de una variable de entorno COMERZZIA_HOME o, en caso de no existir, del valor
	 * devuelto por la propiedad del sistema user.home.
	 */
	protected static void initComerzziaHomePath(AppConfigData configData) {
		if (StringUtils.isBlank(configData.getCmzHomePath())) {
			String newComerzziaHome = System.getenv(COMERZZIA_HOME_ENV_VAR);

			if (newComerzziaHome != null) {
				log.info("initComerzziaHomePath() - COMERZZIA_HOME assigned by environment variable");
				
				configData.setCmzHomePath(newComerzziaHome);
			} else {
				newComerzziaHome = System.getProperty(COMERZZIA_HOME_ENV_VAR);

				if (newComerzziaHome != null) {
					log.info("initComerzziaHomePath() - COMERZZIA_HOME assigned by system property");
					configData.setCmzHomePath(newComerzziaHome);
				} 			
			}
		}
	}

}
