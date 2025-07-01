package com.comerzzia.pos.core.gui;

public interface ApplicationListener {

	void onBeforeApplicationInit();

	void onAfterApplicationInit();

	void onBeforeApplicationStart();

	void onAfterApplicationStart();

	void onBeforeApplicationClose();
	
}
