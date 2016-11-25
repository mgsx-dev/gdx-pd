package net.mgsx.pd;

/**
 * Puredata patch (file)
 * @author mgsx
 *
 */
public class PdPatch 
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
	
	
}
