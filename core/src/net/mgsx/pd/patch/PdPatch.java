package net.mgsx.pd.patch;

import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.Pd;

/**
 * Puredata patch (file)
 * @author mgsx
 *
 */
public class PdPatch implements Disposable
{
	int pdHandle;

	/**
	 * Construct a patch with a valid pd handle.
	 * @param pdHandle
	 */
	public PdPatch(int pdHandle) {
		super();
		this.pdHandle = pdHandle;
	}

	@Override
	public void dispose() {
		Pd.audio.close(this);
	}
	
	
}
