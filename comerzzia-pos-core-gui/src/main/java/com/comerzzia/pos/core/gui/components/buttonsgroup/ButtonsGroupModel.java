
package com.comerzzia.pos.core.gui.components.buttonsgroup;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name="ButtonsGroup" )
@XmlAccessorType(XmlAccessType.FIELD)
public class ButtonsGroupModel {

	@XmlElement( name="ButtonsRow" )
	private List<ButtonsGroupPanelRowBean> buttonsRow;
	
	public List<ButtonsGroupPanelRowBean> getButtonsRow() {
		return buttonsRow;
	}

	public void setButtonsRow(List<ButtonsGroupPanelRowBean> lineasBotones) {
		this.buttonsRow = lineasBotones;
	}
	
}
