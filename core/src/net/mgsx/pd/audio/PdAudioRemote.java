package net.mgsx.pd.audio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.patch.PdPatch;

public class PdAudioRemote implements PdAudio
{
	OSCPortOut sender;
	OSCPortIn receiver;
	
	private ObjectMap<String, Array<PdListener>> listeners = new ObjectMap<String, Array<PdListener>>();
	
	@Override
	public void create(PdConfiguration config) 
	{
		try {
			sender = new OSCPortOut(InetAddress.getByName("localhost"), 3000); // TODO config ?
			receiver = new OSCPortIn(3002); // TODO config
			receiver.addListener(new AddressSelector() {
				@Override
				public boolean matches(String messageAddress) {
					return true;
				}
			}, new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) 
				{
					if("/send".equals(message.getAddress())){
						if(message.getArguments().size() > 1){
							String name = message.getArguments().get(0).toString();
							Array<PdListener> list = listeners.get(name);
							if(list != null){
								for(PdListener l : list) l.receiveFloat(name, Float.valueOf(message.getArguments().get(1).toString()));
							}
						}
					}
				}
			});
			receiver.startListening();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 
	}
	
	@Override
	public void sendFloat(String recv, float x) {
		Collection<Object> args = new ArrayList<Object>();
		args.add(x);
		OSCMessage msg = new OSCMessage("/send/" + recv, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void release() 
	{
		sender.close();
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public int arraySize(String arg0) {
		Gdx.app.error("PdAudioRemote", "get array size not supported in remote mode");
		return 0;
	}

	@Override
	public void close(PdPatch arg0) {
		// nothing to close (dummy patch).
	}

	@Override
	public PdPatch open(FileHandle arg0) {
		// return a dummy patch
		return new PdPatch(0);
	}

	@Override
	public void readArray(float[] arg0, int arg1, String arg2, int arg3, int arg4) {
		Gdx.app.error("PdAudioRemote", "read array not supported in remote mode");
	}

	@Override
	public void sendBang(String arg0) {
		OSCMessage msg = new OSCMessage("/send/" + arg0);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendList(String arg0, Object... arg1) {
		Collection<Object> args = new ArrayList<Object>();
		for(Object o : arg1) args.add(o);
		OSCMessage msg = new OSCMessage("/send/" + arg0, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String arg0, String arg1, Object... arg2) {
		Collection<Object> args = new ArrayList<Object>();
		args.add(arg1);
		for(Object o : arg2) args.add(o);
		OSCMessage msg = new OSCMessage("/send/" + arg0, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendSymbol(String arg0, String arg1) {
		Collection<Object> args = new ArrayList<Object>();
		args.add(arg1);
		OSCMessage msg = new OSCMessage("/send/" + arg0, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeArray(String arg0, int arg1, float[] arg2, int arg3, int arg4) {
		Gdx.app.error("PdAudioRemote", "write array not supported in remote mode");
	}

	@Override
	public void addListener(String source, PdListener listener) {
		Array<PdListener> listeners = this.listeners.get(source);
		if(listeners == null){
			Collection<Object> args = new ArrayList<Object>();
			args.add(1);
			OSCMessage msg = new OSCMessage("/subscribe/" + source, args);
			try {
				sender.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.listeners.put(source, listeners = new Array<PdListener>());
		}
		listeners.add(listener);
	}

	@Override
	public void removeListener(String source, PdListener listener) {
		Array<PdListener> listeners = this.listeners.get(source);
		if(listeners != null){
			listeners.removeValue(listener, true);
		}
	}

}