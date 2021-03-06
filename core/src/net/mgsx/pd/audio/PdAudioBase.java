package net.mgsx.pd.audio;

import java.io.IOException;

import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.PdReceiver;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.patch.PdPatch;
import net.mgsx.pd.utils.PdRuntimeException;

/**
 * Pure Data default implementation (platform independent)
 * 
 * @author mgsx
 *
 */
abstract public class PdAudioBase implements PdAudio
{
	final private ObjectMap<String, Array<PdListener>> listeners = new ObjectMap<String, Array<PdListener>>();
	
	private PdAudioThread thread;
	
	private PdConfiguration config;
	
	public void create(PdConfiguration config){
		this.config = config;
		
		PdBase.setReceiver(new PdReceiver(){

			private final static String printTag = "gdx-pd-print";
			@Override
			public void receiveBang(String source) {
				Array<PdListener> contextualListeners = listeners.get(source);
				if(contextualListeners != null){
					for(PdListener listener : contextualListeners){
						listener.receiveBang(source);
					}
				}
			}

			@Override
			public void receiveFloat(String source, float x) {
				Array<PdListener> contextualListeners = listeners.get(source);
				if(contextualListeners != null){
					for(PdListener listener : contextualListeners){
						listener.receiveFloat(source, x);
					}
				}
			}

			@Override
			public void receiveSymbol(String source, String symbol) {
				Array<PdListener> contextualListeners = listeners.get(source);
				if(contextualListeners != null){
					for(PdListener listener : contextualListeners){
						listener.receiveSymbol(source, symbol);
					}
				}
			}

			@Override
			public void receiveList(String source, Object... args) {
				Array<PdListener> contextualListeners = listeners.get(source);
				if(contextualListeners != null){
					for(PdListener listener : contextualListeners){
						listener.receiveList(source, args);
					}
				}
			}

			@Override
			public void receiveMessage(String source, String symbol, Object... args) {
				Array<PdListener> contextualListeners = listeners.get(source);
				if(contextualListeners != null){
					for(PdListener listener : contextualListeners){
						listener.receiveMessage(source, symbol, args);
					}
				}
			}

			@Override
			public void print(String s) {
				if(Gdx.app != null){
					if(Gdx.app.getLogLevel() >= Application.LOG_DEBUG){
						Gdx.app.debug(printTag, s);
					}
					Gdx.app.log(printTag, s);
				}else{
					System.out.println("pd " + s);
				}
			}
			
		});
		
		thread = createThread(config);
		thread.start();
		
	}
	
	protected PdAudioThread createThread(PdConfiguration config)
	{
		return new PdAudioThread(config);
	}
	
	public void release()
	{
		if(thread != null){
			thread.dispose();
			thread = null;
		}
		listeners.clear();
		PdBase.setReceiver(null);
	}
	
	@Override
	public void dispose() {
		release();
	}
	
	@Override
	public void addListener(String source, PdListener listener) {
		Array<PdListener> contextualListeners = listeners.get(source);
		if(contextualListeners == null){
			PdBase.subscribe(source);
			listeners.put(source, contextualListeners = new Array<PdListener>());
		}
		contextualListeners.add(listener);
	}
	@Override
	public void removeListener(String source, PdListener listener) {
		Array<PdListener> contextualListeners = listeners.get(source);
		if(contextualListeners != null){
			contextualListeners.removeValue(listener, true);
			if(contextualListeners.size <= 0){
				PdBase.unsubscribe(source);
				listeners.remove(source);
			}
		}
	}
	
	public PdPatch open(FileHandle file)
	{
		try {
			int handle = PdBase.openPatch(file.path());
			return new PdPatch(handle);
		} catch (IOException e) {
			throw new PdRuntimeException("unable to open patch", e);
		}
	}
	
	@Override
	public void close(PdPatch patch) {
		PdBase.closePatch(patch.getPdHandle());
	}
	
	  public void sendBang(String recv){
		  checkError(PdBase.sendBang(recv));
	  }

	  public void sendFloat(String recv, float x){
		  checkError(PdBase.sendFloat(recv, x));
	  }

	  public void sendSymbol(String recv, String sym){
		  checkError(PdBase.sendSymbol(recv, sym));
	  }
	  
	  public void sendList(String recv, Object... args) {
		  checkError(PdBase.sendList(recv, args));
	  }

	  public void sendMessage(String recv, String msg, Object... args) {
		  checkError(PdBase.sendMessage(recv, msg, args));
	  }
	  
	  public int arraySize(String name){
		  int size = PdBase.arraySize(name);
		  if(size < 0){
			  throw new PdRuntimeException(size);
		  }
		  return size;
	  }

	  public void readArray(float[] destination, int destOffset, String source, int srcOffset,
	      int n) {
		  checkError(PdBase.readArray(destination, destOffset, source, srcOffset, n));
	  }

	  public void writeArray(String destination, int destOffset, float[] source, int srcOffset,
	      int n) {
		  checkError(PdBase.writeArray(destination, destOffset, source, srcOffset, n));
	  }
	  
	  protected void checkError(int code){
		  if(PdConfiguration.safe && code != 0){
			  throw new PdRuntimeException(code);
		  }
	  }
	  
	@Override
	public void pause() {
		thread.dispose();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// silently fail.
		}
		thread = null;
	}
	
	@Override
	public void resume() {
		if(thread == null){
			thread = createThread(config);
			thread.start();
		}
	}
}
