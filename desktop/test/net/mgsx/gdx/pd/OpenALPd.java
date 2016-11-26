package net.mgsx.gdx.pd;

import java.io.IOException;

import org.puredata.core.PdBase;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

/** @author mgsx */
public class OpenALPd extends OpenALMusic
{
	private static int inChannels = 0;
	private static int outChannels = 2;
	private static int sampleRate = 44100;
	
	private static float [] pdInBuffer, pdOutBuffer;
	private static int ticks;
	
	public static void init(LwjglApplicationConfiguration config)
	{
		PdBase.openAudio(inChannels, outChannels, sampleRate);
		PdBase.computeAudio(true);
		int pdBufferSize = config.audioDeviceBufferSize * config.audioDeviceBufferCount;
		ticks = pdBufferSize / PdBase.blockSize(); // TODO should be even !
		pdOutBuffer = new float[pdBufferSize * outChannels];
		pdInBuffer = new float[0];
	}
	
	public OpenALPd (OpenALAudio audio, FileHandle file) 
	{
		super(audio, file);
		setup(outChannels, sampleRate);
		try {
			PdBase.openPatch(file.path());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int read (byte[] buffer) 
	{
		PdBase.process(ticks, pdInBuffer, pdOutBuffer);
		int ii=0;
		for(int i=0 ; i<pdOutBuffer.length ; i++){
			float floatSample = pdOutBuffer[i];
			floatSample = MathUtils.clamp(floatSample, -1f, 1f);
			int intSample = (int)(floatSample * 32767);
			buffer[ii++] = (byte)(intSample & 0xFF);
			buffer[ii++] = (byte)((intSample >> 8) & 0xFF);
		}
		return pdOutBuffer.length * 2;
	}

	public void reset () {
		// reset has no effect in this context.
	}
}
