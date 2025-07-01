package com.comerzzia.pos.core.devices.device.scanner;

import java.awt.Toolkit;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.springframework.core.io.ClassPathResource;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;

import lombok.extern.log4j.Log4j;


@Log4j
public class DeviceScannerDummy extends DeviceAbstractImpl implements DeviceScanner {
	private Clip clipBeep;
	private Clip clipError;
	
	protected Clip getClip(String resource) {
		ClassPathResource resourceFile = new ClassPathResource("error.wav");
		
		if (resourceFile.exists()) {
			try {
				Clip clipFile = AudioSystem.getClip();
				clipFile.open(AudioSystem.getAudioInputStream(resourceFile.getFile()));
				
				return clipFile;
			} catch (Throwable ignore) {
				log.error("Error initializing clip " + resource + ": " + ignore.getLocalizedMessage(), ignore);
			}
		} else {
			log.warn("Audio file " + resource + " not found. Using system beep.");
		}
		
		return null;
	}
	
	@Override
    public void connect() throws DeviceException {
		clipError = getClip("beep.wav");
		clipError = getClip("beepError.wav");		
    }

	@Override
    public void disconnect() throws DeviceException {
    }

	@Override
    protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
    }

	@Override
	public void enableReader() {
	}

	@Override
	public void disableReader() {
	}
	
	@Override
	public void beep() {
		if (clipBeep == null) {
			Toolkit.getDefaultToolkit().beep();
			return;
		};
		
		clipBeep.setFramePosition(0);
		clipBeep.start();
	}
	
	@Override
	public void beepError() {
		if (clipError == null) {
			Toolkit.getDefaultToolkit().beep();
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		
		clipError.setFramePosition(0);
		clipError.start();		
	}
    
}
