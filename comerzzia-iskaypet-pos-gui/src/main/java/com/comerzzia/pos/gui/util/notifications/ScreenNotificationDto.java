package com.comerzzia.pos.gui.util.notifications;

public class ScreenNotificationDto {

	private String type;

	private String title;

	private String text;

	public ScreenNotificationDto(String type, String title, String text) {
		super();
		this.type = type;
		this.title = title;
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
