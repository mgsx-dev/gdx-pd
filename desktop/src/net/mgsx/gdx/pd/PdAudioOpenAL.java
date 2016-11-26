package net.mgsx.gdx.pd;

import org.puredata.core.PdBase;

import net.mgsx.pd.PdAudioBase;

/**
 * Pd Audio desktop implementation using libGDX OpenAL implementation.
 * 
 * @author mgsx
 *
 */
public class PdAudioOpenAL extends PdAudioBase
{
	private PdAudioThread thread;
	
	@Override
	public void dispose() {
	}

	@Override
	public void create() 
	{
		PdBase.openAudio(0, 2, 44100);
		PdBase.computeAudio(true);

		thread = new PdAudioThread();
		thread.start();
	}
	
	

	@Override
	public void release() {
		thread.dispose();
		thread = null;
	}

}
