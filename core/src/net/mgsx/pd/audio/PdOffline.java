package net.mgsx.pd.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.puredata.core.PdBase;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class PdOffline {

	public static void bake(File patch, File wav, int channels, int sampleRate, float time) throws IOException {
		
		// disable Pd : does nothing if Pd alreay initialized.
		PdConfiguration.disabled = true;

		// Pause audio.
		// Does nothing in headless mode but required to 
		// have Pd static code executed (load library)
		Pd.audio.pause();
		
		int handle = PdBase.openPatch(patch);
		PdBase.openAudio(0, channels, sampleRate);
		PdBase.computeAudio(true);
		
		int frames = (int)(time * sampleRate);
		int samples = frames * channels;
		short [] data = new short[samples];
		int ticks = frames / PdBase.blockSize();
		PdBase.process(ticks, new short[]{}, data);
		
		PdBase.closePatch(handle);
		
		// save
		byte [] buf = new byte[data.length * 2];
		for(int i=0 ; i<data.length ; i++){
			buf[i*2+0] = (byte)(data[i] & 0xFF);
			buf[i*2+1] = (byte)((data[i] >> 8) & 0xFF);
		}
		
		ByteArrayInputStream stream = new ByteArrayInputStream(buf);
		AudioFormat format = new AudioFormat(sampleRate, 16, channels, true, false);
		AudioInputStream audioStream = new AudioInputStream(stream, format, data.length);
		AudioSystem.write(audioStream, Type.WAVE, wav);
		
		// resume audio
		Pd.audio.resume();
	}
}
