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
		
		short[] pdBufferIn = new short[PdBase.blockSize() * config.inputChannels];
		short[] pdBufferOut = new short[PdBase.blockSize() * config.outputChannels];
		
		long nanoDuration = (long)(1e9 * (double)PdBase.blockSize() / (double)config.sampleRate);
		
		long realTime = System.nanoTime();
		long logicTime = realTime;
		
		// fill empty buffers to avoid glitches at startup.
		for(int i=0 ; i<config.bufferCount ; i++){
			device.writeSamples(outBuffer, 0, outBuffer.length);
		}
		
		while(processing){
			if(recorder != null){
				recorder.read(inBuffer, 0, inBuffer.length);
			}
			
			int inIndex = 0;
			int outIndex = 0;
			for(int i=0 ; i<ticks ; i++){
				
				realTime = System.nanoTime();
				long waitTime = (logicTime - realTime) / 1000000;
				if(waitTime > 0){
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
					}
				}else{
					logicTime = realTime;
				}
				logicTime += nanoDuration;
				
				for(int j=0 ; j<pdBufferIn.length ; j++){
					pdBufferIn[j] = inBuffer[inIndex++];
				}
				PdBase.process(1, pdBufferIn, pdBufferOut);
				for(int j=0 ; j<pdBufferOut.length ; j++){
					outBuffer[outIndex++] = pdBufferOut[j];
				}
				
			}
			
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
