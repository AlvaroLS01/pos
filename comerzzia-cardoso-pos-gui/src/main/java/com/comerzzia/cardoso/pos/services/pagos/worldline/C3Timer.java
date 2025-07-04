/*
 * ----------------------------------------------------------------------------- INGENICO Technical Software Department
 * ----------------------------------------------------------------------------- Copyright (c) 2011 - 2015 INGENICO.
 * 28-32 boulevard de Grenelle 75015 Paris, France. All rights reserved. This source program is the property of the
 * INGENICO Company mentioned above and may not be copied in any form or by any means, whether in part or in whole,
 * except under license expressly granted by such INGENICO company. All copies of this source program, whether in part
 * or in whole, and whether modified or not, must display this and all other embedded copyright and ownership notices in
 * full.
 */
package com.comerzzia.cardoso.pos.services.pagos.worldline;

import javax.swing.Timer;

/**
 * Timer class firing up every second until the overall timeout expires This can be used to automatically close a dialog
 * when the timer expires
 */
public class C3Timer {

	protected int delay_;
	protected int tick_;
	protected Timer timer_;
	protected TimerCallback callback_;

	interface TimerCallback {

		void tickLast();
	}

	protected C3Timer(TimerCallback callback, int delay) {
		callback_ = callback;
		delay_ = delay;
		tick_ = 0;

		// setup the timer that will be firing up every second
		timer_ = new Timer(1000, e -> {
			tick_++;
			if (tick_ >= delay_) {
				callback_.tickLast();
				timer_.stop();
			}
		});
		timer_.setRepeats(true);
		timer_.start();
	}

	/**
	 * Stop the internal SWING timer
	 */
	protected void stop() {
		if (timer_.isRunning()) {
			timer_.stop();
		}
	}	
}
