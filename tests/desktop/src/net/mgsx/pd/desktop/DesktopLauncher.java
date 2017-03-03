package net.mgsx.pd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.gdx.pd.PdAudioOpenAL;
import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.DefaultPdMidi;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Pd.audio = new PdAudioOpenAL();
		Pd.midi = new DefaultPdMidi();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new GdxPdDemo(), config);
	}
}
