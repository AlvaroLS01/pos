package com.comerzzia.pos.core.gui.components.keyboard;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.VirtualKeyboard;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.stage.Screen;
import javafx.stage.Window;

@Component
@Lazy(value = true)
public class Keyboard {

	protected static KeyBoardPopup keyboardPopup;
	protected Map<Scene, SceneFocusChangeListener> mapScenesInUse = new HashMap<>();
	
	public Keyboard() throws IOException, URISyntaxException {
		if (keyboardPopup == null) {
    		keyboardPopup = createKeyBoardPopup();
			keyboardPopup.getKeyBoard().setOnKeyboardCloseButton(new EventHandler<Event>() {
				public void handle(Event event) {
					close();
				}
			});
    	}
	}

	protected KeyBoardPopup createKeyBoardPopup() throws IOException, URISyntaxException {
		Locale locale = new Locale(AppConfig.getCurrentConfiguration().getLanguage(), AppConfig.getCurrentConfiguration().getCountry()); //TODO: Implementar carga de idioma real
		
		//Buscamos el recurso para el idioma concreto
		URL skinResource = SkinManager.getInstance().getResource("com/comerzzia/pos/core/gui/components/keyboard/" + locale.getLanguage());
    	
		//Si no existe usaremos idioma inglés
    	if (skinResource == null) {
    		locale = Locale.UK;
    		skinResource = SkinManager.getInstance().getResource("com/comerzzia/pos/core/gui/components/keyboard/" + locale.getLanguage());
    	}
		
    	Path path = null;
    	String resourceStr = skinResource.toURI().toString();
		if (resourceStr.startsWith("jar")) {
			File tempDir = createTempDirectory();
			tempDir.deleteOnExit();
    		path = tempDir.toPath();
    		String substring = resourceStr.substring(resourceStr.indexOf("!")+1, resourceStr.lastIndexOf("/"));
			copyFromJar(substring, skinResource.toURI(), path);
    	} else {
    		File file = urlToFile(skinResource);
    		path = file.getParentFile().toPath();
    	}
		
		VirtualKeyboard vkConfig = null;
		if (AppConfig.getCurrentConfiguration() != null) {
		    vkConfig = AppConfig.getCurrentConfiguration().getVirtualKeyboard();
		}

		double initScale = 1.4;
		if (vkConfig != null && vkConfig.getInitScale() != null) {
		    initScale = vkConfig.getInitScale();
		}

		KeyBoardPopup popup = KeyBoardPopupBuilder.create()
		        .initScale(initScale)
		        .initLocale(locale)
		        .addIRobot(new FXRobotHandler())
		        .layerPath(path)
		        .layer(DefaultLayer.NUMBLOCK)
		        .build();

		popup.getKeyBoard().getStylesheets().clear();

		if (vkConfig != null) {
		    if (vkConfig.getMinScale() != null) {
		        popup.getKeyBoard().setMinScale(vkConfig.getMinScale());
		    }
		    if (vkConfig.getMaxScale() != null) {
		        popup.getKeyBoard().setMaxScale(vkConfig.getMaxScale());
		    }
		}

		popup.getKeyBoard().getStylesheets().clear();
		
		return popup;
    }
	
	public void setPopupVisible(final boolean show, final TextInputControl textNode, final Window parentWindow) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				calcularTamañoPantalla(show, textNode);
				
				if (!show){
					getKeyboardPopup().hide();
					return;
				}
				else {
					if (!getKeyboardPopup().isShowing()) {						
						getKeyboardPopup().show(parentWindow);
					}
				}
			}
		});
	}

	protected void calcularTamañoPantalla(final boolean show, final TextInputControl textNode) {
        if (show) {
			if(textNode instanceof NumericTextField) {
				getKeyboardPopup().getKeyBoard().setKeyboardType(KeyboardType.NUMERIC);
			}
			else {
				getKeyboardPopup().getKeyBoard().setKeyboardType(KeyboardType.TEXT_SHIFT);							
			}
			
			if (textNode != null && textNode.getScene() != null) {
				Window window = textNode.getScene().getWindow();
				if(window == null) {
					return; //The screen was closed before the keyboard popup could show up
				}
				Rectangle2D textNodeBounds = new Rectangle2D(
						window.getX() + textNode.getLocalToSceneTransform().getTx(), 
						window.getY() + textNode.getLocalToSceneTransform().getTy(), 
						textNode.getWidth(), 
						textNode.getHeight());

				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				if (textNodeBounds.getMinX() + getKeyboardPopup().getWidth() > screenBounds.getMaxX()) {
					getKeyboardPopup().setX(screenBounds.getMaxX() - getKeyboardPopup().getWidth());
				} else {
					getKeyboardPopup().setX(textNodeBounds.getMinX());
				}

				if (textNodeBounds.getMaxY() + getKeyboardPopup().getHeight() > screenBounds.getMaxY()) {
					getKeyboardPopup().setY(textNodeBounds.getMinY() - getKeyboardPopup().getHeight() - 40);
				} else {
					getKeyboardPopup().setY(textNodeBounds.getMaxY() + 40);
				}
			}
		}
    }

	public void close() {
		setPopupVisible(false, null, null);
	}
	
	public static KeyBoardPopup getKeyboardPopup() {
		return keyboardPopup;
	}

	public static void setKeyboardPopup(KeyBoardPopup keyboardPopup) {
		Keyboard.keyboardPopup = keyboardPopup;
	}

	public void onController(SceneController controller) {
		if(AppConfig.getCurrentConfiguration().getVirtualKeyboard() != null) {
			if(AppConfig.getCurrentConfiguration().getVirtualKeyboard().isShow()) {
				updateFocusListenersOnKeyboardToggle(controller);
			}
		}
		else if (AppConfig.getCurrentConfiguration().getShowAlphanumericKeyboard()) {
			updateFocusListenersOnKeyboardToggle(controller);
		}
	}

	private void updateFocusListenersOnKeyboardToggle(SceneController controller) {
		close();
		if (controller.isShowKeyboard()) {
			Scene scene = controller.getScene();
			addFocusListener(scene);
		} else {
			removeFocusListener(controller.getScene());
		}
	}

	protected void addFocusListener(Scene scene) {
		SceneFocusChangeListener sceneFocusChangeListener = mapScenesInUse.get(scene);
		if (sceneFocusChangeListener == null) {
			sceneFocusChangeListener = new SceneFocusChangeListener(scene.getWindow());
			mapScenesInUse.put(scene, sceneFocusChangeListener);
		}
		if (!sceneFocusChangeListener.isAttached()) {
			scene.focusOwnerProperty().addListener(sceneFocusChangeListener);
			sceneFocusChangeListener.setAttached(true);
		}
	}
	
	protected void removeFocusListener(Scene scene) {
		if(mapScenesInUse.containsKey(scene)) {
			SceneFocusChangeListener sceneFocusChangeListener = mapScenesInUse.get(scene);
			scene.focusOwnerProperty().removeListener(sceneFocusChangeListener);
			sceneFocusChangeListener.setAttached(false);
		}
	}

	public static File createTempDirectory() throws IOException {
		final File temp;

		temp = File.createTempFile("cmz", "");

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}
	
	public static void copyFromJar(String source, URI resource, final Path target) throws URISyntaxException, IOException {
	    try (FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap())){
	    	final Path jarPath = fileSystem.getPath(source);
	    	
	    	Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {
	    		
	    		private Path currentTarget;
	    		
	    		@Override
	    		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	    			currentTarget = target.resolve(jarPath.relativize(dir).toString());
	    			Files.createDirectories(currentTarget);
	    			return FileVisitResult.CONTINUE;
	    		}
	    		
	    		@Override
	    		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	    			Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
	    			return FileVisitResult.CONTINUE;
	    		}
	    		
	    	});
	    }
	}
	
	public static File urlToFile(URL url) {
		File f;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		return f;
	}
	
	protected class SceneFocusChangeListener implements ChangeListener<Node> {
    	protected Window window;
    	protected Boolean isAttached = false;
    	
		public SceneFocusChangeListener(Window window) {
			super();
			this.window = window;
		}

		@Override
		public void changed(ObservableValue<? extends Node> value, Node n1, Node n2) {
			if (n2 != null && n2 instanceof TextInputControl) {
				setPopupVisible(true, (TextInputControl) n2, this.window);
			} else {
				close();
			}
		}

		public Boolean isAttached() {
			return isAttached;
		}

		public void setAttached(Boolean isAttached) {
			this.isAttached = isAttached;
		}
    }
	
}
