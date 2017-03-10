package net.mgsx.pd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.PdConfiguration;

public class DesktopRemoteLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration.disableAudio = true;
		PdConfiguration.remoteEnabled = true;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new GdxPdDemo(), config);
	}
}
