package net.mgsx.gdx.pd;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudio;
import net.mgsx.pd.patch.PdPatch;

public class PdTests {

	public static void main(String[] args) throws IOException {
		
		PdAudio audio = new PdAudioDesktop();
		audio.create(new PdConfiguration());
		PdPatch patch = audio.open(new FileHandle("pd/test-suite.pd"));
		
		System.in.read();
		
		audio.close(patch);
		
		System.in.read();
		
		audio.release();
	}
}
