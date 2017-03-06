package net.mgsx.gdx.pd;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudioDevice;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.PdConfiguration;

public class PdAudioThreadOpenAL extends Thread implements Disposable
{
	private volatile boolean processing;
	private volatile boolean requirePolling = true;
	private PdConfiguration config;
	
	public PdAudioThreadOpenAL(PdConfiguration config) {
		super("PdAudioThread");
		setPriority(MAX_PRIORITY);
		this.config = config;
	}
	
	@Override
	public void run() 
	{
		int samplePerBuffer = config.bufferSize;
		
		// FIXME min 1 ! assume PdBase.blockSize() is 64
		int ticks = samplePerBuffer / PdBase.blockSize();
		
		short [] inBuffer = new short[samplePerBuffer * config.inputChannels];
		short [] outBuffer = new short[samplePerBuffer * config.outputChannels];
		
		// It could be done like this :
		// AudioDevice device = Gdx.audio.newAudioDevice(config.sampleRate, config.outputChannels < 2);
		// but we need to align buffer size : Pd.process and device.write.
		int bufferSizeBytes = outBuffer.length * 2;
		AudioDevice device = new OpenALAudioDevice((OpenALAudio)Gdx.audio, config.sampleRate, config.outputChannels<2, bufferSizeBytes, config.bufferCount);
		
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
