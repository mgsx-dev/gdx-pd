package net.mgsx.pd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.audio.PdAudioRemote;
import net.mgsx.pd.midi.PdMidiRemote;

public class DesktopRemoteLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration.disableAudio = true;
		Pd.audio = new PdAudioRemote();
		Pd.midi = PdMidiRemote.createDefaultUnicast();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GdxPdDemo(), config);
	}
}
