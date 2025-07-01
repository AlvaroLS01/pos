package com.comerzzia.pos.core.gui.components.fxtable;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Component
public class FxTableUtils {
	
	protected static final Logger log = Logger.getLogger(FxTableUtils.class);

	public void setColumnFitToContent(final TableColumn<?, ?> column) {
//		Platform.runLater(new Runnable(){
//			@Override
//			public void run() {
//				try {
//					TableViewSkin<?> skin = (TableViewSkin<?>) column.getTableView().getSkin();
//					TableColumnHeader tableColumnHeader = skin.getTableHeaderRow().getRootHeader();
//					Method declaredMethod = TableColumnHeader.class.getDeclaredMethod("resizeToFit", TableColumn.class, int.class);
//					declaredMethod.setAccessible(true);
//					declaredMethod.invoke(tableColumnHeader, column, -1);
//				} catch (Exception e) {
//					log.error("setColumnFitToContent() - " + e.getMessage(), e);
//				}
//			}});
	}

	public <S> void setColumnFillRemainingSpace(TableView<S> table, TableColumn<?, ?> column) {
		ObservableList<TableColumn<S,?>> columns = table.getColumns();
		
		ReadOnlyDoubleProperty widthProperty = table.widthProperty();
		DoubleBinding binding = null;
		for (TableColumn<S, ?> tableColumn : columns) {
			if (tableColumn == column) {
				continue;
			}
			if (binding == null) {
				binding = widthProperty.subtract(tableColumn.widthProperty());
			} else {
				binding = binding.subtract(tableColumn.widthProperty());
			}
		}
		
		column.prefWidthProperty().bind(binding.subtract(2)); //2 de borde
	}
	
}
