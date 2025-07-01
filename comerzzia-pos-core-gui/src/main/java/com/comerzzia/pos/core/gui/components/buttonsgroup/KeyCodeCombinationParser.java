package com.comerzzia.pos.core.gui.components.buttonsgroup;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;

public class KeyCodeCombinationParser {

	public KeyCodeCombination parse(String keyCode, boolean opcional) {
		try {
			return parse(keyCode);
		} catch(RuntimeException e) {
			if (opcional) {
				return null;
			} else {
				throw e;
			}
		}
	}
	
	public KeyCodeCombination parse(String keyCode, KeyCodeCombination defaultKeyCodeCombination) {
		try {
			return parse(keyCode);
		} catch(RuntimeException e) {
			return defaultKeyCodeCombination;
		}
	}
	
	public KeyCodeCombination parse(String keyCode) {
		if (keyCode == null) {
			throw new IllegalArgumentException("keyCode no puede ser null");
		}
		
		String teclaAccesoRapido = keyCode.toLowerCase();
		boolean ctrl = false;
		ctrl |= teclaAccesoRapido.contains("ctrl");
		ctrl |= teclaAccesoRapido.contains("control");
		// Limpio la cadena
		teclaAccesoRapido = teclaAccesoRapido.replace("ctrl", "").replace("control", "");
		
		boolean shift = false;
		shift |= teclaAccesoRapido.contains("shift");
		teclaAccesoRapido = teclaAccesoRapido.replace("shift", "");
		
		boolean alt = false;
		alt |= teclaAccesoRapido.contains("alt");
		teclaAccesoRapido = teclaAccesoRapido.replace("alt", "");
		
		teclaAccesoRapido = teclaAccesoRapido.replace("+", "");
		teclaAccesoRapido = teclaAccesoRapido.replace(" ", "");
		
		// En este momento en teclaAccesoRapido sólo debería haber una tecla, sin modificadores
		// Le ponemos la primera letra a mayus, el resto se queda en minus
		String tecla = null;
		if (teclaAccesoRapido.length() > 1) {
			tecla = Character.toUpperCase(teclaAccesoRapido.charAt(0)) + teclaAccesoRapido.substring(1);
		} else if (teclaAccesoRapido.length() > 0) {
			tecla = teclaAccesoRapido.toUpperCase();
		}
		
		List<Modifier> modifiers = new ArrayList<>();
		if (ctrl) {
			Modifier modifier = KeyCombination.CONTROL_DOWN;
			Modifier shortcutMod = KeyCombination.SHORTCUT_DOWN;
			modifiers.add(modifier);
			modifiers.add(shortcutMod);
		} else {
		}
		if (shift) {
			Modifier modifier = KeyCombination.SHIFT_DOWN;
			modifiers.add(modifier);
		} else {
		}
		if (alt) {
			Modifier modifier = KeyCombination.ALT_DOWN;
			modifiers.add(modifier);
		} else {
		}
		
		return new KeyCodeCombination(KeyCode.getKeyCode(tecla), modifiers.toArray(new Modifier[0]));
	}
	
	public KeyCodeCombination build(KeyCode keyCode, KeyCode modifier) {
		List<Modifier> modifiers = new ArrayList<>();
		
		if (KeyCode.CONTROL.equals(modifier)) {
			Modifier mod = KeyCombination.CONTROL_DOWN;
			Modifier shortcutMod = KeyCombination.SHORTCUT_DOWN;
			modifiers.add(mod);
			modifiers.add(shortcutMod);
		}

		if (KeyCode.SHIFT.equals(modifier)) {
			Modifier mod = KeyCombination.SHIFT_DOWN;
			modifiers.add(mod);
		}
		
		if (KeyCode.ALT.equals(modifier)) {
			Modifier mod = KeyCombination.ALT_DOWN;
			modifiers.add(mod);
		} 

		return new KeyCodeCombination(keyCode, modifiers.toArray(new Modifier[0]));
	}

}
