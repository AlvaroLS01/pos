package com.comerzzia.pos.util.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterfaceInfo {
	protected Integer width;
    protected Integer height;
    protected boolean fullscreen = true;
    protected boolean showTaskbar = true;    
}
