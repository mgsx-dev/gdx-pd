package net.mgsx.pd.audio;


import org.puredata.core.PdListener;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.patch.PdPatch;

/**
 * Pure Data audio interface.
 * Abstract PdBase static methods.
 * 
 * @author mgsx
 *
 */
public interface PdAudio extends Disposable
{
	/**
	 * initialize audio
	 * @throws PdRuntimeException on error
	 */
	public void create(PdConfiguration config);
	
	/**
	 * release audio
	 */
	public void release();
	
	/**
	 * Open a patch
	 * @param file patch file
	 * @return the opened path.
	 * @throws PdRuntimeException if file doesn't exists or can't be opened.
	 */
	public PdPatch open(FileHandle file);
	
	/**
	 * Close a patch
	 * @param patch patch to close
	 */
	public void close(PdPatch patch);
	
	  /**
	   * Sends a bang to the object associated with the given symbol.
	   * 
	   * @param recv symbol associated with receiver
	   * @throws PdRuntimeException on error
	   */
	  public void sendBang(String recv);
	
	  /**
	   * Sends a float to the object associated with the given symbol.
	   * 
	   * @param recv symbol associated with receiver
	   * @param x float value to send to receiver
	   * @throws PdRuntimeException on error
	   */
	  public void sendFloat(String recv, float x);
	
	  /**
	   * Sends a symbol to the object associated with the given symbol.
	   * 
	   * @param recv symbol associated with receiver
	   * @param sym symbol to send to receiver
	   * @throws PdRuntimeException on error
	   */
	  public void sendSymbol(String recv, String sym);
	  
	  /**
	   * Sends a list to an object in Pd.
	   * 
	   * @param recv symbol associated with receiver
	   * @param args list of arguments of type Integer, Float, or String
	   * @throws PdRuntimeException on error
	   */
	  public void sendList(String recv, Object... args) ;
	
	  /**
	   * Sends a typed message to an object in Pd.
	   * 
	   * @param recv symbol associated with receiver
	   * @param msg first symbol of message
	   * @param args list of arguments of type Integer, Float, or String
	   * @throws PdRuntimeException on error
	   */
	  public void sendMessage(String recv, String msg, Object... args);
	  
	  /**
	   * Returns the size of an array in Pd.
	   * 
	   * @param name of the array in Pd
	   * @throws PdRuntimeException on error
	   */
	  public int arraySize(String name);
	
	  /**
	   * Reads values from an array in Pd.
	   * 
	   * @param destination float array to write to
	   * @param destOffset index at which to start writing
	   * @param source array in Pd to read from
	   * @param srcOffset index at which to start reading
	   * @param n number of values to read
	   * @throws PdRuntimeException on error
	   */
	  public void readArray(float[] destination, int destOffset, String source, int srcOffset,
	      int n);
	
	  /**
	   * Writes values to an array in Pd.
	   * 
	   * @param destination name of the array in Pd to write to
	   * @param destOffset index at which to start writing
	   * @param source float array to read from
	   * @param srcOffset index at which to start reading
	   * @param n number of values to write
	   * @throws PdRuntimeException on error
	   */
	  public void writeArray(String destination, int destOffset, float[] source, int srcOffset,
	      int n);
	  
	  /**
	   * add listener for symbol
	   * @param source name of the symbol for which receive events.
	   * @param listener
	   * @throws PdRuntimeException on error
	   */
	  public void addListener(String source, PdListener listener);
	  
	  /**
	   * remove previously added listener
	   * @param source
	   * @param listener
	   */
	  public void removeListener(String source, PdListener listener);
	  
	  
}
