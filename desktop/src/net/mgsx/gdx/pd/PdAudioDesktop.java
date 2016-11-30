package net.mgsx.gdx.pd;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudioBase;

/**
 * Pd Audio desktop implementation using JavaSound library.
 * 
 * @author mgsx
 *
 */
public class PdAudioDesktop extends PdAudioBase
{
	private JavaSoundThread audioThread;
	
	@Override
	public void create(PdConfiguration config) 
	{
		super.create(config);
		if(audioThread != null) throw new Error("pd already started");
		audioThread = new JavaSoundThread(config.sampleRate, config.outputChannels, 16); // TODO configure
		audioThread.start();
	}
	
	@Override
	public void release() {
		if(audioThread == null) throw new Error("pd not started yet");
		audioThread.interrupt();
		try {
			audioThread.join();
		} catch (InterruptedException e) {
			// silently fail.
		}
		audioThread = null;
		super.release();
	}

	@Override
	public void dispose() {
		
	}

}
