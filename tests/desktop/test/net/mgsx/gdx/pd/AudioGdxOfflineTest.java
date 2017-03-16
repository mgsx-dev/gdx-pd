package net.mgsx.gdx.pd;

import java.io.File;
import java.io.IOException;

import net.mgsx.pd.audio.PdOffline;

public class AudioGdxOfflineTest {

	public static void main(String[] args) throws IOException {
		PdOffline.bake(
				new File("test-resources/complex-sound.pd"), 
				File.createTempFile("baked-sound", ".wav"), 
				1, 44100, 3);
	}
}
