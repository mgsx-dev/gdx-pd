package net.mgsx.pd.audio;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.PdConfiguration;

/**
 * Main Pd audio thread.
 * 
 * @author mgsx
 *
 */
public class PdAudioThread extends Thread implements Disposable
{
	private volatile boolean processing;
	private volatile boolean requirePolling = true;
	protected final PdConfiguration config;
	
	public PdAudioThread(PdConfiguration config) {
		super("PdAudioThread");
		setPriority(MAX_PRIORITY);
		this.config = config;
		processing = true;
	}
	
	protected AudioDevice createAudioDevice()
	{
		return Gdx.audio.newAudioDevice(config.sampleRate, config.outputChannels<2);
	}
	
	@Override
	public void run() 
	{
		int samplePerBuffer = config.bufferSize;
		
		// FIXME min 1 ! assume PdBase.blockSize() is 64
		int ticks = samplePerBuffer / PdBase.blockSize();
		
		short [] inBuffer = new short[samplePerBuffer * config.inputChannels];
		short [] outBuffer = new short[samplePerBuffer * config.outputChannels];
		
		AudioDevice device = createAudioDevice();
		
		AudioRecorder recorder = null;
		if(config.inputChannels > 0){
			recorder = Gdx.audio.newAudioRecorder(config.sampleRate, config.inputChannels < 2);
		}
		PdBase.openAudio(config.inputChannels, config.outputChannels, config.sampleRate);
		PdBase.computeAudio(true);
		
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
		
		if(recorder != null){
			recorder.dispose();
		}
	}

	@Override
	public void dispose() 
	{
		processing = false;
	}

}
