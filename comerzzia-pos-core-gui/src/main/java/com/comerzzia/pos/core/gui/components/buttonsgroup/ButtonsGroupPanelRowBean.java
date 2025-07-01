
package com.comerzzia.pos.core.gui.components.buttonsgroup;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ButtonsGroupPanelRowBean {

	@XmlElement(name = "Button")
	private List<ButtonConfigurationBean> buttonRow;

	public List<ButtonConfigurationBean> getButtonRow() {
		return buttonRow;
	}

	public void setButtonRow(List<ButtonConfigurationBean> buttonRow) {
		this.buttonRow = buttonRow;
	}

}
