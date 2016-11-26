package net.mgsx.gdx.pd;

import net.mgsx.pd.PdAudioBase;
import net.mgsx.pd.PdConfiguration;

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
	public void create(PdConfiguration config) 
	{
		super.create(config);
		
		thread = new PdAudioThread(config);
		thread.start();
	}
	
	

	@Override
	public void release() {
		thread.dispose();
		thread = null;
		
		super.release();
	}

}
