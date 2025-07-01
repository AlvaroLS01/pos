package com.comerzzia.pos.core.gui.components.textField;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.pos.util.format.FormatUtils;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class NumericTextField extends TextField {

	private static final String SPECIAL_CHARACTER = "ñ";
	private boolean interceptar;
	private boolean decimalKey;

	public NumericTextField() {
		super();

		// Capturamos la escritura del punto decimal en el KEY_PRESSED porque el KEY_TYPED no devuelve el código de la
		// tecla pulsada
		super.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				interceptar = false;
				decimalKey = false;
				// "." de teclado numérico lo cambiamos por el decimal separator
				if (event.getCode().equals(KeyCode.DECIMAL)) {
					decimalKey = true;
					return;
				}
				//Interceptamos los siguientes:
				if (event.getCode().isLetterKey() || event.getCode().isWhitespaceKey() || event.getText().equalsIgnoreCase(SPECIAL_CHARACTER)){
					interceptar = true;
				}
			}
		});

		super.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				if(isEditable()) {
					if(interceptar){
						event.consume();
						interceptar = false;
					}else if(decimalKey) {
						event.consume();
						appendText(String.valueOf(FormatUtils.getInstance().getDecimalSeparator()));
						decimalKey = false;
					}else if(StringUtils.isNotBlank(event.getCharacter()) && Character.isLetter(event.getCharacter().charAt(0))) {
						event.consume();
					}
				}
			}
		});

	}

}
