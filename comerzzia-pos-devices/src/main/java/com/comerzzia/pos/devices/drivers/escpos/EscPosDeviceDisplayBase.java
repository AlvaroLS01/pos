
package com.comerzzia.pos.devices.drivers.escpos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EscPosDeviceDisplayBase {
    
    public static final int ANIMATION_NULL = 0;
    public static final int ANIMATION_FLYER = 1;
    public static final int ANIMATION_SCROLL = 2;
    public static final int ANIMATION_BLINK = 3;
    public static final int ANIMATION_CURTAIN = 4;
    
    private EscPosDeviceDisplayImpl impl;    
    private EscPosDisplayAnimator anim;     
    private javax.swing.Timer m_tTimeTimer;    
    private int counter = 0;
    
    /** Creates a new instance of DeviceDisplayBase */
    public EscPosDeviceDisplayBase(EscPosDeviceDisplayImpl impl) {
        this.impl = impl; 
        anim = new EscPosNullAnimator("", "");
        m_tTimeTimer = new javax.swing.Timer(50, new PrintTimeAction());
    }
    
    public void writeVisor(int animation, String sLine1, String sLine2) {
        
        m_tTimeTimer.stop();
        switch (animation) {
            case ANIMATION_FLYER:
                anim = new EscPosFlyerAnimator(sLine1, sLine2);
                break;
            case ANIMATION_SCROLL:
                anim = new EscPosScrollAnimator(sLine1, sLine2);
                break;
            case ANIMATION_BLINK:
                anim = new EscPosBlinkAnimator(sLine1, sLine2);
                break;
            case ANIMATION_CURTAIN:
                anim = new EscPosCurtainAnimator(sLine1, sLine2);
                break;
            default: // ANIMATION_NULL
                anim = new EscPosNullAnimator(sLine1, sLine2);
                break;
        }
        
        counter = 0;
        anim.setTiming(counter);
        impl.repaintLines();
        
        if (animation != ANIMATION_NULL) {
            counter = 0;
            m_tTimeTimer.start();
        }
    }
         
    public void writeVisor(String sLine1, String sLine2) {
        writeVisor(ANIMATION_NULL, sLine1, sLine2);
    }
    
    public void clearVisor() {
        writeVisor(ANIMATION_NULL, "", "");
    }
    
    public String getLine1() {
        return anim.getLine1();
    }
    
    public String getLine2() {
        return anim.getLine2();
    }
    
    private class PrintTimeAction implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            counter ++;
            anim.setTiming(counter);
            impl.repaintLines();
        }        
	}

	public void writeVisor(String sLine) {
		writeVisor(ANIMATION_NULL, sLine);
	}

	public void writeVisor(int animation, String sLine) {
		m_tTimeTimer.stop();
		switch (animation) {
			case ANIMATION_FLYER:
				anim = new EscPosFlyerAnimator(sLine, "");
				break;
			case ANIMATION_SCROLL:
				anim = new EscPosScrollAnimator(sLine, "");
				break;
			case ANIMATION_BLINK:
				anim = new EscPosBlinkAnimator(sLine, "");
				break;
			case ANIMATION_CURTAIN:
				anim = new EscPosCurtainAnimator(sLine, "");
				break;
			default: // ANIMATION_NULL
				anim = new EscPosNullAnimator(sLine, "");
				break;
		}

		counter = 0;
		anim.setTiming(counter);
		impl.repaintLine();

		if (animation != ANIMATION_NULL) {
			counter = 0;
			m_tTimeTimer.start();
		}
	}
}
