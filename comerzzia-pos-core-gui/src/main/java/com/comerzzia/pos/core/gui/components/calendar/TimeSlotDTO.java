package com.comerzzia.pos.core.gui.components.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {

	protected String hourFrom;
	protected String hourTo;

	protected Boolean available;
	protected Boolean empty;

}
