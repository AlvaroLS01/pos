package com.comerzzia.pos.core.gui.components.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class CalendarioTableController<T extends TimeSlotDTO> {
	private static final Logger log = Logger.getLogger(CalendarioTableController.class);

	protected TableView<ObservableList<T>> tbFechas;
	protected List<TableColumn<ObservableList<T>, String>> tableColumns = new ArrayList<>();

	protected CellChangedListener<T> cellChangedListener;
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void initialize(TableView<ObservableList<T>> tbFechas, TableColumn<ObservableList<T>, String> tc1, TableColumn<ObservableList<T>, String> tc2, TableColumn<ObservableList<T>, String> tc3,
	        TableColumn<ObservableList<T>, String> tc4, TableColumn<ObservableList<T>, String> tc5, TableColumn<ObservableList<T>, String> tc6, TableColumn<ObservableList<T>, String> tc7) {
		this.tbFechas = tbFechas;
		tableColumns.add(tc1);
		tableColumns.add(tc2);
		tableColumns.add(tc3);
		tableColumns.add(tc4);
		tableColumns.add(tc5);
		tableColumns.add(tc6);
		tableColumns.add(tc7);

		tbFechas.setPlaceholder(new Label(""));
		tbFechas.getSelectionModel().setCellSelectionEnabled(true);
		tbFechas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		final ObservableList<ObservableList<T>> dates = FXCollections.observableList(new ArrayList<ObservableList<T>>());

		for (int i = 0 ; i< tableColumns.size() ; i++) {
			final int index = i;

			tableColumns.get(i).setCellFactory(CellFactoryBuilder.createCellRendererCelda(
			        "tbFechas", "tc" + (index + 1), null,
					CellFactoryBuilder.LEFT_ALIGN_STYLE));


			tableColumns.get(i).setCellValueFactory(new Callback<CellDataFeatures<ObservableList<T>, String>, ObservableValue<String>>(){
				@Override
				public ObservableValue<String> call(
				        CellDataFeatures<ObservableList<T>, String> cdf) {

					if (cdf.getValue().get(index) == null || cdf.getValue().get(index).getEmpty() == null) {
						return new SimpleStringProperty("");
					} else {
						return new SimpleStringProperty(cdf.getValue().get(index)
						        .getHourFrom() + " - " + cdf.getValue().get(index).getHourTo());
					}
				}
			});


			tableColumns.get(i).setCellFactory(new Callback<TableColumn<ObservableList<T>, String>, TableCell<ObservableList<T>, String>>(){
				@Override
				public TableCell<ObservableList<T>, String> call(TableColumn<ObservableList<T>, String> column) {

					final TableCell<ObservableList<T>, String> cell = new TableCell<ObservableList<T>, String>(){
						@Override
						protected void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (empty) {
								setGraphic(null);
								setTooltip(null);
							} else {
								if (getTableRow() != null) {
									ObservableList<T> list = getTableView().getItems().get(getTableRow().getIndex());
									if (list.get(index) == null || list.get(index).getEmpty()) {
										this.getStyleClass().add("empty-cell");
									} else {
										if (list.get(index).getAvailable() && !list.get(index).getEmpty()) {
											this.getStyleClass().add("available-cell");
										} else {
											this.getStyleClass().add("unavailable-cell");
										}
										setText(list.get(index).getHourFrom() + " - " + list.get(index).getHourTo());
									}
								}
							}
						}
					};
					if (cellChangedListener != null) {
						ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
							@Override
							public void changed(
									ObservableValue<? extends Boolean> arg0,
									Boolean wasFocused, Boolean isFocused) {
								cellChangedListener.cellChanged(cell, index, isFocused);
							}
						};
						cell.focusedProperty().addListener(changeListener);
					}
					cell.setAlignment(Pos.BASELINE_CENTER);
					return cell;
				}
			});
		}
		tbFechas.setItems(dates);
	}

	public void loadTable(Date dateFrom, List<DateTimeSlotDTO<T>> dateSlots) {
		tbFechas.getItems().clear();

		Calendar cal = Calendar.getInstance();
		if(dateFrom != null) {
			cal.setTime(dateFrom);
		} else {
			cal.setTime(new Date());
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Map<Date, List<T>> mapDateTimeSlots = new TreeMap<Date, List<T>>();
		for (DateTimeSlotDTO<T> entry : dateSlots) {
			mapDateTimeSlots.put(entry.getDate(), entry.getTimeSlots());
		}

		int rowNumber = 0;
		for (Entry<Date, List<T>> entry : mapDateTimeSlots.entrySet()) {
			if (entry.getValue().size() > rowNumber) {
				rowNumber = entry.getValue().size();
			}
		}

		int col = 0;
		ObservableList<TableColumn<ObservableList<T>, ?>> columns = tbFechas
				.getColumns();

		for (Entry<Date, List<T>> entry : mapDateTimeSlots.entrySet()) {
			Calendar headerCalendar = Calendar.getInstance();
			headerCalendar.setTime(entry.getKey());
			columns.get(col).setText(
					getDayName(headerCalendar) + " " + sdf.format(entry.getKey()));
			col++;
		}

		for(int i = 0; i < rowNumber; i++) {
			ObservableList<T> aux = FXCollections.observableList(new ArrayList<T>());
			Calendar rowsCalendar = Calendar.getInstance();
			rowsCalendar.setTime(cal.getTime());
			rowsCalendar.set(Calendar.HOUR_OF_DAY, 0);
			rowsCalendar.set(Calendar.MINUTE, 0);
			rowsCalendar.set(Calendar.SECOND, 0);
			rowsCalendar.set(Calendar.MILLISECOND, 0);

			for(int j = 0; j <= 7; j++) {
				List<T> list = mapDateTimeSlots.get(rowsCalendar.getTime());

				if(list != null && !list.isEmpty()) {
					if(list.size() > i){
						aux.add(list.get(i));
					}else{
						aux.add(null);
					}
				}

				rowsCalendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			tbFechas.getItems().add(aux);
		}
	}

	protected String getDayName(Calendar cal) {
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return I18N.getText("Lunes");
			case Calendar.TUESDAY:
				return I18N.getText("Martes");
			case Calendar.WEDNESDAY:
				return I18N.getText("Miércoles");
			case Calendar.THURSDAY:
				return I18N.getText("Jueves");
			case Calendar.FRIDAY:
				return I18N.getText("Viernes");
			case Calendar.SATURDAY:
				return I18N.getText("Sábado");
			default:
				return I18N.getText("Domingo");
		}
	}

	public void setCellChangedListener(final CellChangedListener<T> cellChangedListener) {
		this.cellChangedListener = cellChangedListener;
	}

	public interface CellChangedListener<T> {

		void cellChanged(final TableCell<ObservableList<T>, String> cell, final int index, Boolean isFocused);
	}
}
