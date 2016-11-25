package net.mgsx.pd;

import java.io.IOException;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;

/**
 * Pure Data common interface (platform independent)
 * 
 * @author mgsx
 *
 */
abstract public class PdAudio 
{
	/**
	 * initialize audio
	 */
	abstract public void create();
	abstract public void release();
	
	public PdPatch open(FileHandle file)
	{
		PdPatch patch = new PdPatch();
		try {
			patch.pdHandle = PdBase.openPatch(file.path());
		} catch (IOException e) {
			throw new Error("unable to open patch " + file);
		}
		return patch;
	}
}
