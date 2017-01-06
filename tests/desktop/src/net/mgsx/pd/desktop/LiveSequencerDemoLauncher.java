package net.mgsx.pd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.demo.LiveSequencerDemoApplication;

public class LiveSequencerDemoLauncher {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 16;
		
		new LwjglApplication(new LiveSequencerDemoApplication(), config);
	}
}
