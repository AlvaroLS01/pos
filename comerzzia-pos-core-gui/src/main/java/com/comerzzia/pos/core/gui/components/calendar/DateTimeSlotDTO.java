package com.comerzzia.pos.core.gui.components.calendar;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeSlotDTO<T extends TimeSlotDTO> {

	protected Date date;
	protected List<T> timeSlots;

}
