package net.mgsx.pd.audio;

import net.mgsx.pd.PdConfiguration;

/**
 * futur unified implementation (when audio CPU bug will be fixed)
 * 
 * @author mgsx
 *
 */
public class PdAudioDefault extends PdAudioBase
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
