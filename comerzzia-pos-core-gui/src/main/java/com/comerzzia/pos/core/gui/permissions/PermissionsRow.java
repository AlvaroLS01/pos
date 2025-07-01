package com.comerzzia.pos.core.gui.permissions;

import java.util.HashMap;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.model.ActionOperationDetail;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.permissions.PermissionDTO;
import com.comerzzia.pos.core.gui.permissions.add.AddPermissionRow;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionsRow {
	
	protected String holder; // Does not change
    protected ActionDetail action;
    protected EffectiveActionPermissionsDto holderPermissions;
    
    protected HashMap<String, SimpleStringProperty> holderPermissionsGui; // Change. Update when changing permissions

    public PermissionsRow(ActionDetail action, AddPermissionRow addPermissionRow) {
        this.holder = addPermissionRow.getHolder();
        this.action = action;

        EffectiveActionPermissionsDto holderPermissions = new EffectiveActionPermissionsDto();
        holderPermissions.setMenuAction(action);

        if (addPermissionRow.getProfile() != null) {
            holderPermissions.setProfileId(addPermissionRow.getProfile().getProfileId());
            holderPermissions.setProfileDes(addPermissionRow.getProfile().getProfileDes());

        }
        else if (addPermissionRow.getUser() != null) {
            holderPermissions.setUserId(addPermissionRow.getUser().getUserId());
            holderPermissions.setUserDes(addPermissionRow.getUser().getUserDes());
        }

        this.holderPermissions = holderPermissions;
        
        this.holderPermissionsGui = new HashMap<>();

        // Register operations
        for (ActionOperationDetail operation : action.getOperations()) {
        	PermissionDTO permission = new PermissionDTO();
            permission.setProfileId(holderPermissions.getProfileId());
            permission.setProfileDes(holderPermissions.getProfileDes());
            permission.setUserDes(holderPermissions.getUserDes());
            permission.setUserId(holderPermissions.getUserId());
            permission.setActionId(action.getActionId());
            permission.setOperacion(operation);
            permission.setAccessLevelUndefined();
            
            this.holderPermissions.addPermission(permission);
            this.holderPermissionsGui.put(operation.getOperationDes(), new SimpleStringProperty("permisos.noestablecido"));
        }

    }

    /**
     * Constructor for an already existing permission
     */
	public PermissionsRow(ActionDetail action, EffectiveActionPermissionsDto permissions) {		
        if (permissions.isProfilePermission()) {
            this.holder = permissions.getProfileDes();
        }
        else if (permissions.isUserPermission()) {
        	this.holder = permissions.getUserDes();
        }
        
        this.action = permissions.getMenuAction();
        
        this.holderPermissions = permissions;

        this.holderPermissionsGui = new HashMap<>();

		for (PermissionDTO permission : holderPermissions.getPermissionList()) {
			if (permission.isManage()) {
				holderPermissionsGui.put(permission.getOperation().getOperationDes(), new SimpleStringProperty("permisos.administrar"));
			}
			else if (permission.isGranted()) {
				holderPermissionsGui.put(permission.getOperation().getOperationDes(), new SimpleStringProperty("permisos.concedido"));
			}
			else {
				holderPermissionsGui.put(permission.getOperation().getOperationDes(), new SimpleStringProperty("permisos.denegado"));
			}
		}
        
        // If there is no permission for an operation, create it
        // Register operations
		for (ActionOperationDetail operation : action.getOperations()) {
			if (!holderPermissions.getPermissions().containsKey(operation.getOperationDes())) {
				PermissionDTO missingPermission = new PermissionDTO();
				missingPermission.setProfileId(permissions.getProfileId());
				missingPermission.setProfileDes(permissions.getProfileDes());
				missingPermission.setUserDes(permissions.getUserDes());
				missingPermission.setUserId(permissions.getUserId());
				missingPermission.setActionId(action.getActionId());
				missingPermission.setOperacion(operation);
				missingPermission.setAccessLevelUndefined();
				holderPermissions.addPermission(missingPermission);
				holderPermissionsGui.put(operation.getOperationDes(), new SimpleStringProperty("permisos.noestablecido"));
			}
		}
        
    }

}
