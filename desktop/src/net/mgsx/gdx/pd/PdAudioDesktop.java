package net.mgsx.gdx.pd;

import net.mgsx.pd.PdAudio;

/**
 * Pd Audio desktop implementation using JavaSound library.
 * 
 * @author mgsx
 *
 */
public class PdAudioDesktop extends PdAudio
{
	private JavaSoundThread audioThread;
	
	@Override
	public void create() 
	{
		if(audioThread != null) throw new Error("pd already started");
		audioThread = new JavaSoundThread(44100, 2, 16); // TODO configure
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
	}

}
