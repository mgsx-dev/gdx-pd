package net.mgsx.gdx.pd;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALDevicePatched;
import com.badlogic.gdx.utils.Disposable;

public class PdAudioThread extends Thread implements Disposable
{
	private volatile boolean processing;
	
	@Override
	public void run() 
	{
		int channels = 2;
		
		int samplePerBuffer = 512;
		
		int ticks = samplePerBuffer / PdBase.blockSize();
		int bufferSamples = samplePerBuffer * channels;
		int bytesPerBuffer = bufferSamples * 2;
		
		float [] inBuffer = new float[0];
		float [] outBuffer = new float[bufferSamples];
		
		// TODO when bug is fix libgdx/libgdx#2252 : 
		// Gdx.audio.newAudioDevice(samplingRate, isMono);
		AudioDevice device = new OpenALDevicePatched((OpenALAudio)Gdx.audio, 44100, false, bytesPerBuffer, 8);
		
		processing = true;
		
		while(processing){
			PdBase.process(ticks, inBuffer, outBuffer);
			device.writeSamples(outBuffer, 0, outBuffer.length);
		}
		
		device.dispose();
	}

	@Override
	public void dispose() 
	{
		processing = false;
	}

}
