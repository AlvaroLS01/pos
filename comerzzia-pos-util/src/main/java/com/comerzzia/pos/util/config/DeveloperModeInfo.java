package com.comerzzia.pos.util.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeveloperModeInfo { 
	protected String usuario;
    protected String password;
    protected boolean autologin;
    protected boolean forceReload = false;    
}
