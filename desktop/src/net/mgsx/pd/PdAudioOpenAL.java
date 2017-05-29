package net.mgsx.pd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudioDevice;

import net.mgsx.pd.audio.PdAudioBase;
import net.mgsx.pd.audio.PdAudioThread;

/**
 * Pd Audio desktop implementation.
 * 
 * Reason of specific implementation is related to Lwjgl audio configuration confliting
 * with audio process synchronization (buffers size for audio recorder / pd process / audio device).
 * It might be unified in the futur.
 * 
 * @author mgsx
 *
 */
public class PdAudioOpenAL extends PdAudioBase
{
	private static class PdAudioThreadOpenAL extends PdAudioThread
	{
		public PdAudioThreadOpenAL(PdConfiguration config) {
			super(config);
		}
		
		@Override
		protected AudioDevice createAudioDevice() 
		{
			// It could be done like this :
			// AudioDevice device = Gdx.audio.newAudioDevice(config.sampleRate, config.outputChannels < 2);
			// but we need to align buffer size : Pd.process and device.write.
			int samplePerFrame = config.bufferSize;
			int samplePerBuffer = samplePerFrame * config.outputChannels;
			int bufferSizeBytes = samplePerBuffer * 2;
			if(Gdx.audio instanceof OpenALAudio)
				return new OpenALAudioDevice((OpenALAudio)Gdx.audio, config.sampleRate, config.outputChannels<2, bufferSizeBytes, config.bufferCount);
			return Gdx.audio.newAudioDevice(config.sampleRate, config.outputChannels<2);
		}

	}
	
	@Override
	protected PdAudioThread createThread(PdConfiguration config) 
	{
		return new PdAudioThreadOpenAL(config);
	}

}
