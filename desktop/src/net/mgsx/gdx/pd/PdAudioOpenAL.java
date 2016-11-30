package net.mgsx.gdx.pd;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudioBase;

/**
 * XXX TEMPORARY (waiting for fix audio CPU)
 * 
 * Pd Audio desktop implementation using libGDX OpenAL implementation.
 * 
 * @author mgsx
 *
 */
public class PdAudioOpenAL extends PdAudioBase
{
	private PdAudioThreadOpenAL thread;
	
	@Override
	public void dispose() {
	}

	@Override
	public void create(PdConfiguration config) 
	{
		super.create(config);
		
		thread = new PdAudioThreadOpenAL(config);
		thread.start();
	}
	
	

	@Override
	public void release() {
		thread.dispose();
		thread = null;
		
		super.release();
	}

}
