package com.comerzzia.pos.core.gui.permissions.user;

import com.comerzzia.core.facade.service.permissions.PermissionDTO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PermissionAPComponent extends AnchorPane {

	protected HBox hbPermissionLine;
    
	protected Label lbText;
	// protected ImageView image;
    
    private PermissionDTO permission;
      
	public PermissionAPComponent() {
		super();
		hbPermissionLine = new HBox();
		hbPermissionLine.setAlignment(Pos.CENTER_LEFT);
		hbPermissionLine.setPadding(new Insets(0, 0, 0, 50));
		
		lbText = new Label();
		lbText.setAlignment(Pos.CENTER_LEFT);
		lbText.setContentDisplay(ContentDisplay.CENTER);
		lbText.setMinHeight(42);
		HBox.setHgrow(lbText, Priority.ALWAYS);
		hbPermissionLine.getChildren().add(lbText);
		
		// image = new ImageView();
		// image.setFitHeight(40);
		// image.setFitWidth(40);
		// image.setLayoutX(10);
		// hbPermissionLine.getChildren().add(image);
		// hbPermissionLine.setAlignment(Pos.CENTER);

		this.getChildren().add(hbPermissionLine);

	}

	public PermissionAPComponent(PermissionDTO permission) {
		this();
		this.setPermission(permission);
	}

	public PermissionDTO getPermission() {
		return permission;
	}

	public void setPermission(PermissionDTO permission) {
		this.permission = permission;

		this.lbText.setText(permission.getOperation().getOperationDes());
		if (permission.isManage()) {
			hbPermissionLine.getStyleClass().add("lineapermiso-administrar");
		}
		else if (permission.isGranted()) {
			hbPermissionLine.getStyleClass().add("lineapermiso-concedido");
		}
		else if (permission.isForbidden()) {
			hbPermissionLine.getStyleClass().add("lineapermiso-denegado");
		}
		else {
			hbPermissionLine.getStyleClass().add("lineapermiso-noestablecido");
		}
	}
    
}
