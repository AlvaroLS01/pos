package com.comerzzia.pos.core.services.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.commons.sessions.ComerzziaUserSession;
import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.model.Profile;
import com.comerzzia.core.facade.model.User;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionServiceFacade;
import com.comerzzia.core.facade.service.user.UserInvalidLoginException;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.pos.core.services.api.security.ComerzziaTokenProvider;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

@Component
public class POSUserSession extends Observable{
    protected static final Logger log = Logger.getLogger(POSUserSession.class.getName());
    
    protected SimpleObjectProperty<User> user = new SimpleObjectProperty<>(null);
    protected Map<PermisosCacheKey, EffectiveActionPermissionsDto> permissions; // IdAccion - Permisos
    protected Boolean superAdministratorCached;
    protected SimpleBooleanProperty leftHandedMode = new SimpleBooleanProperty(false);
    
    @Autowired
    protected ApplicationSession applicationSession;
    
    @Autowired
    protected VariableServiceFacade variableService;
    
    @Autowired
    protected PermissionServiceFacade permissionService;
    @Autowired
    protected UserServiceFacade userService;
    
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    @Autowired
    protected ComerzziaSession session;

    @Autowired
    protected ComerzziaTokenProvider tokenProvider;
    
    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    protected String sessionToken;

    protected POSUserSession() {
        permissions = new HashMap<>();
        user.addListener(changed -> {
        	setChanged();
        	notifyObservers(this);
        });
        leftHandedMode.addListener(changed -> {
        	setChanged();
        	notifyObservers(this);
        });
    }

    public void init(String userCode, String password) throws SesionInitException, UserInvalidLoginException {
    	applicationSession.auditOperation(new ComerzziaAuditEventBuilder().addAction("authentication").addField("userCode", userCode));
    	
        try {
        	
        	User newUser = userService.findByCode(userCode);
        	
        	if (!passwordEncoder.matches(password, newUser.getPassword())) {
        		throw new UserInvalidLoginException();
        	}
            
            
            MDC.put("user", newUser.getUserCode());
            
            if (sessionToken == null) {
            	sessionToken = tokenProvider.getValidToken(applicationSession.getConfiguredActivityUid(), userCode);
            }
            
            tenantResolver.setToken(sessionToken);            

            session.setActivityUid(applicationSession.getConfiguredActivityUid());
            session.setInstanceUid(applicationSession.getConfiguredInstanceUid());            
			session.setUser(modelMapper.map(newUser, ComerzziaUserSession.class));
			session.setToken(sessionToken);
			session.setTerminalId(applicationSession.getPosUid());
			
			tenantResolver.forceCurrentTenantSession(session);
			            
			superAdministratorCached = null;
			setUser(newUser);
            
        }
        catch (UserInvalidLoginException ex) {
            throw ex;
        }
        catch (Exception ex) {
            log.error("init() - Error inicializando sesión de usuario: " + userCode + " - " + ex.getMessage(), ex);
            throw new SesionInitException(ex);
        }
    }

    
    public User getUser() {
    	return user.get();
    }
    
	protected void setUser(User user) {
		this.user.set(user);
	}
    
	public SimpleObjectProperty<User> userProperty() {
		return user;
	}

    public Map<PermisosCacheKey, EffectiveActionPermissionsDto> getPermissions() {
        return permissions;
    }

    public void clearPermissions() {
        this.permissions.clear();
    }

    public EffectiveActionPermissionsDto getEffectiveActionPermissions(ActionDetail action) {
        return getEffectivePermissions(action, true);
    }
    
    public EffectiveActionPermissionsDto getEffectiveActionPermissions(User user, ActionDetail action, boolean cache) {
    	PermisosCacheKey permisosCacheKey = new PermisosCacheKey(action.getActionId(), user.getUserId());
    	EffectiveActionPermissionsDto permisosEfectivos = permissions.get(permisosCacheKey);
		if (!cache || permisosEfectivos == null) {
			permisosEfectivos = permissionService.obtainEffectivePermissions(action, user.getUserId());
			permisosEfectivos.setSuperAdmin(isSuperAdministrator(user));
			permissions.put(permisosCacheKey, permisosEfectivos);
		}
    	return permisosEfectivos;
    }

    public EffectiveActionPermissionsDto getEffectivePermissions(ActionDetail action, boolean cache) {
        return getEffectiveActionPermissions(getUser(), action, cache);
    }
    
    public Boolean isSuperAdministrator(User user) {
		for (Profile profile : user.getProfiles()) {
			if (profile.getProfileId().equals(0l)) {
				return true;
			}
		}
		return false;
    }

	public Boolean isSuperAdministrator() {
		if (superAdministratorCached == null) {
			superAdministratorCached = isSuperAdministrator(getUser());
		}
		return superAdministratorCached;
	}
	
	public Boolean getLeftHandedMode() {
		return leftHandedMode.get();
	}

	public void setLeftHandedMode(Boolean leftHandedMode) {
		this.leftHandedMode.set(leftHandedMode);
	}

	public void clear() {
		permissions = new HashMap<>();
    }
	
	static class PermisosCacheKey {
		Long idAccion;
		Long idUsuario;
		public PermisosCacheKey(Long idAccion, Long idUsuario) {
			super();
			this.idAccion = idAccion;
			this.idUsuario = idUsuario;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((idAccion == null) ? 0 : idAccion.hashCode());
			result = prime * result + ((idUsuario == null) ? 0 : idUsuario.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PermisosCacheKey other = (PermisosCacheKey) obj;
			if (idAccion == null) {
				if (other.idAccion != null)
					return false;
			} else if (!idAccion.equals(other.idAccion))
				return false;
			if (idUsuario == null) {
				if (other.idUsuario != null)
					return false;
			} else if (!idUsuario.equals(other.idUsuario))
				return false;
			return true;
		}
	}

	public String getSessionToken() {
		return sessionToken;
	}	
	
}
