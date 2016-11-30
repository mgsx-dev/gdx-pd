package net.mgsx.pd.patch;

import org.puredata.core.PdBase;

import com.badlogic.gdx.utils.Disposable;

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
		PdBase.closePatch(pdHandle);
	}
	
	
}
