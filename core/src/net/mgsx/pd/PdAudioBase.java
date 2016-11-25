package net.mgsx.pd;

import java.io.IOException;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Pure Data default implementation (platform independent)
 * 
 * @author mgsx
 *
 */
abstract public class PdAudioBase implements PdAudio
{
	abstract public void create();
	
	abstract public void release();
	
	public PdPatch open(FileHandle file)
	{
		try {
			int handle = PdBase.openPatch(file.path());
			return new PdPatch(handle);
		} catch (IOException e) {
			throw new GdxRuntimeException("unable to open patch", e);
		}
	}
	
	@Override
	public void close(PdPatch patch) {
		PdBase.closePatch(patch.pdHandle);
	}
	
	  public void sendBang(String recv){
		  PdBase.sendBang(recv);
	  }

	  public void sendFloat(String recv, float x){
		  PdBase.sendFloat(recv, x);
	  }

	  public void sendSymbol(String recv, String sym){
		  PdBase.sendSymbol(recv, sym);
	  }
	  
	  public void sendList(String recv, Object... args) {
	    PdBase.sendList(recv, args);
	  }

	  public void sendMessage(String recv, String msg, Object... args) {
	    PdBase.sendMessage(recv, msg, args);
	  }
	  
	  public int arraySize(String name){
		  return PdBase.arraySize(name);
	  }

	  public void readArray(float[] destination, int destOffset, String source, int srcOffset,
	      int n) {
	    PdBase.readArray(destination, destOffset, source, srcOffset, n);
	  }

	  public void writeArray(String destination, int destOffset, float[] source, int srcOffset,
	      int n) {
		  PdBase.writeArray(destination, destOffset, source, srcOffset, n);
	  }
}
