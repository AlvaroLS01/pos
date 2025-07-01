package com.comerzzia.pos.core.gui.helper;

import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class HelperRow<T> {

	protected SimpleStringProperty helperDesc;
    protected SimpleStringProperty helperCode;
    
    protected T object;
}
