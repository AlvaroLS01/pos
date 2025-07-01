package com.comerzzia.pos.util.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VirtualKeyboard {
	private boolean show = false;
	private boolean showBottomInitially = false;
	private Double initScale;
	private Double minScale;
	private Double maxScale;
	
}
