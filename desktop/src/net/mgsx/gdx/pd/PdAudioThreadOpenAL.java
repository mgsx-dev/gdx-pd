package net.mgsx.gdx.pd;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALDevicePatched;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.PdConfiguration;

// XXX TEMPORARY (waiting for fix audio CPU)
public class PdAudioThreadOpenAL extends Thread implements Disposable
{
	private volatile boolean processing;
	private volatile boolean requirePolling = true;
	private PdConfiguration config;
	
	public PdAudioThreadOpenAL(PdConfiguration config) {
		this.config = config;
	}
	
	@Override
	public void run() 
	{
		int samplePerBuffer = 512;
		
		int ticks = samplePerBuffer / PdBase.blockSize();
		
		short [] inBuffer = new short[samplePerBuffer * config.inputChannels];
		short [] outBuffer = new short[samplePerBuffer * config.outputChannels];
		
		// TODO when bug is fix libgdx/libgdx#2252 : 
		// Gdx.audio.newAudioDevice(samplingRate, isMono);
		AudioDevice device = new OpenALDevicePatched((OpenALAudio)Gdx.audio, 44100, false, outBuffer.length * 2, 8);
		
		AudioRecorder recorder = null;
		if(config.inputChannels > 0){
			recorder = Gdx.audio.newAudioRecorder(config.sampleRate, config.inputChannels < 2);
		}
		PdBase.openAudio(config.inputChannels, config.outputChannels, config.sampleRate);
		PdBase.computeAudio(true);
		
		processing = true;
		
		final Runnable pollRunnable = new Runnable() {
			
			@Override
			public void run() {
				PdBase.pollPdMessageQueue();
				requirePolling = true;
			}
		};
		
		while(processing){
			if(recorder != null){
				recorder.read(inBuffer, 0, inBuffer.length);
			}
			PdBase.process(ticks, inBuffer, outBuffer);
			device.writeSamples(outBuffer, 0, outBuffer.length);
			
			if(requirePolling){
				Gdx.app.postRunnable(pollRunnable);
			}
		}
		
		device.dispose();
		
		recorder.dispose();
	}

	@Override
	public void dispose() 
	{
		processing = false;
	}

}
