package net.mgsx.pd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.gdx.pd.PdAudioDesktop;
import net.mgsx.pd.GdxPdTest;
import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.JavaSoundMidi;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Pd.audio = new PdAudioDesktop();
		Pd.midi = new JavaSoundMidi();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GdxPdTest(), config);
	}
}
