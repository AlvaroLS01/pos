package com.comerzzia.dinosol.pos.gui.componentes;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AutocompletionlTextField extends TextField {
	
	private Logger log = Logger.getLogger(AutocompletionlTextField.class);

	// Local variables
	// entries to autocomplete
	private final SortedSet<String> entries;
	// popup GUI
	private ContextMenu entriesPopup;
	
	private List<TextSelectionListener> listeners;

	public AutocompletionlTextField() {
		super();
		this.entries = new TreeSet<>();
		this.entriesPopup = new ContextMenu();
		this.listeners = new ArrayList<TextSelectionListener>();

		setListner();
	}

	/**
	 * "Suggestion" specific listners
	 */
	private void setListner() {
		// Add "suggestions" by changing text
		textProperty().addListener((observable, oldValue, newValue) -> {
			String enteredText = getText();
			// always hide suggestion if nothing has been entered (only "spacebars" are dissalowed in
			// TextFieldWithLengthLimit)
			if (enteredText == null || enteredText.isEmpty()) {
				entriesPopup.hide();
			}
			else {
				// filter all possible suggestions depends on "Text", case insensitive
				List<String> filteredEntries = new ArrayList<String>();
				String direccionFormateada = escaparCadena(enteredText);
				for(String resultSearch : entries) {
					String direccionDevuelta = escaparCadena(resultSearch);
					
					if(direccionDevuelta.toLowerCase().contains(direccionFormateada.toLowerCase())) {
						filteredEntries.add(resultSearch);
					}
				}
				// some suggestions are found
				if (!filteredEntries.isEmpty()) {
					// build popup - list of "CustomMenuItem"
					populatePopup(filteredEntries, enteredText);
					if (!entriesPopup.isShowing()) { // optional
						entriesPopup.show(AutocompletionlTextField.this, Side.BOTTOM, 0, 0); // position of popup
					}
					// no suggestions -> hide
				}
				else {
					entriesPopup.hide();
				}
			}
		});

		// Hide always by focus-in (optional) and out
		focusedProperty().addListener((observableValue, oldValue, newValue) -> {
			entriesPopup.hide();
		});
	}

	protected String escaparCadena(String text) {
		String scapedText = Normalizer.normalize(text, Normalizer.Form.NFD);
		scapedText = scapedText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return scapedText;
	}

	/**
	 * Populate the entry set with the given search results. Display is limited to 10 entries, for performance.
	 * 
	 * @param searchResult
	 *            The set of matching strings.
	 */
	public void populatePopup(List<String> searchResult, String searchReauest) {
		// List of "suggestions"
		List<CustomMenuItem> menuItems = new LinkedList<>();
		// List size - 10 or founded suggestions count
		int maxEntries = 10;
		int count = Math.min(searchResult.size(), maxEntries);
		// Build list as set of labels
		for (int i = 0; i < count; i++) {
			final String result = searchResult.get(i);
			// label with graphic (text flow) to highlight founded subtext in suggestions
			Label entryLabel = new Label();
			 entryLabel.setGraphic(buildTextFlow(result, searchReauest));
			entryLabel.setPrefHeight(10); // don't sure why it's changed with "graphic"
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			menuItems.add(item);

			// if any suggestion is select set it into text and close popup
			item.setOnAction(actionEvent -> {
				setText(result);
				positionCaret(result.length());
				entriesPopup.hide();
				throwTextSelectionListeners(result);
			});
		}

		// "Refresh" context menu
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);
	}

	/**
	 * Get the existing set of autocomplete entries.
	 * 
	 * @return The existing autocomplete entries.
	 */
	public SortedSet<String> getEntries() {
		return entries;
	}

	/**
	 * Build TextFlow with selected text. Return "case" d ependent.
	 * 
	 * @param text
	 *            - string with text
	 * @param filter
	 *            - string to select in text
	 * @return - TextFlow
	 */
	private TextFlow buildTextFlow(String text, String filter) {
		String textFormatted = escaparCadena(text).toUpperCase();
		String filterFormatted = escaparCadena(filter).toUpperCase();
		
		Text textBefore = new Text(text);
		Text textAfter = new Text("");
		Text textFilter = new Text("");
		textFilter.setFill(Color.GREEN);
		
		int filterIndex = textFormatted.toLowerCase().indexOf(filterFormatted.toLowerCase());
		if(filterIndex > 0) {
			textBefore = new Text(text.substring(0, filterIndex));
			textAfter = new Text(text.substring(filterIndex + filter.length()));
			textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length()));
			textFilter.setFill(Color.GREEN);
		}
		return new TextFlow(textBefore, textFilter, textAfter);
	}
	
	public interface TextSelectionListener {
		public void textSelection(String text);
	}
	
	public void throwTextSelectionListeners(String text) {
		for(TextSelectionListener listener : listeners) {
			listener.textSelection(text);
		}
	}
	
	public void addTextSelectionListener(TextSelectionListener listener) {
		listeners.add(listener);
	}
	
	public void changePopup(List<String> filteredEntries, String enteredText) {
		populatePopup(filteredEntries, enteredText);
		if (entriesPopup.isShowing()) {
			entriesPopup.hide();
		}
		entriesPopup.show(AutocompletionlTextField.this, Side.BOTTOM, 0, 0); // position of popup
	}
	
}