package net.mgsx.pd.audio;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.PdConfiguration;

/**
 * futur unified implementation (when audio CPU bug will be fixed)
 * 
 * @author mgsx
 *
 */
public class PdAudioThread extends Thread implements Disposable
{
	private volatile boolean processing;
	private volatile boolean requirePolling = true;
	private PdConfiguration config;
	
	public PdAudioThread(PdConfiguration config) {
		this.config = config;
	}
	
	@Override
	public void run() 
	{
		int samplePerBuffer = 512;
		
		int ticks = samplePerBuffer / PdBase.blockSize();
		
		short [] inBuffer = new short[samplePerBuffer * config.inputChannels];
		short [] outBuffer = new short[samplePerBuffer * config.outputChannels];
		
		// XXX when bug is fix libgdx/libgdx#2252 : 
		AudioDevice device = Gdx.audio.newAudioDevice(44100, false);
		
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
