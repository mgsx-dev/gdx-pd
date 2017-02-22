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
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.patch.PdPatch;

/**
 * Remote version of Puredata implementation.
 * 
 * To be used with Puredata desktop application and provided patch (gdx-pd-network.pd)
 * which support network connection with gdx-pd.
 * 
 * @author mgsx
 *
 */
// TODO throw or trace errors ... maybe something robust is wanted here
public class PdAudioRemote implements PdAudio
{
	final private String sendHost;
	final private int sendPort;
	final private int recvPort;
	
	private OSCPortOut sender;
	private OSCPortIn receiver;
	
	private ObjectMap<String, Array<PdListener>> listeners = new ObjectMap<String, Array<PdListener>>();
	
	/**
	 * Create remote with default network settings (as configured by default in gdx-pd-network.pd)
	 */
	public PdAudioRemote() {
		this("localhost", 3000, 3002);
	}
	
	/**
	 * Create remote with specific network settings
	 * @param sendHost host where Pd is running (can be "localhost", "192.168.0.45", ...)
	 * @param sendPort port on which Pd is listening (see gdx-pd-network-help.pd)
	 * @param recvPort port on which Pd is sending back (see gdx-pd-network-help.pd)
	 */
	public PdAudioRemote(String sendHost, int sendPort, int recvPort) {
		super();
		this.sendHost = sendHost;
		this.sendPort = sendPort;
		this.recvPort = recvPort;
	}

	@Override
	public void create(PdConfiguration config) 
	{
		try {
			sender = new OSCPortOut(InetAddress.getByName(sendHost), sendPort);
			receiver = new OSCPortIn(recvPort);
			receiver.addListener(new AddressSelector() {
				@Override
				public boolean matches(String messageAddress) {
					return true;
				}
			}, new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) 
				{
					try{
						onRemoteMessage(message);
					}catch(Throwable e){
						e.printStackTrace();
					}
				}
			});
			receiver.startListening();
			sendClearSubscriptions();
		} catch (SocketException e) {
			throw new GdxRuntimeException(e);
		} catch (UnknownHostException e) {
			throw new GdxRuntimeException(e);
		}
			 
	}
	
	private void onRemoteMessage(OSCMessage message)
	{
		String address = message.getAddress();
		if(!"/send".equals(address)){
			throw new GdxRuntimeException("OSC message not supported : " + String.valueOf(address));
		}
		if(message.getArguments().size() < 2){
			throw new GdxRuntimeException("OSC send message at least 2 args expected");
		}
		if(!(message.getArguments().get(0) instanceof String)){
			throw new GdxRuntimeException("OSC send message expect first argument (receiver) to be a string, get : " + String.valueOf(message.getArguments().get(0)));
		}
		if(!(message.getArguments().get(1) instanceof String)){
			throw new GdxRuntimeException("OSC send message expect second argument (type) to be a string, get : " + String.valueOf(message.getArguments().get(1)));
		}
		String name = message.getArguments().get(0).toString();
		Array<PdListener> listener = listeners.get(name);
		if(listener == null){
			Gdx.app.error("Pd", "warning no listeners for receiver : " + name);
			return;
		}
		String type = message.getArguments().get(1).toString();
		if("msg".equals(type)){
			// case of no args : bang
			if(message.getArguments().size() <= 2){
				for(PdListener l : listener) l.receiveBang(name);
			}
			// case of one arg : simple handlers
			else if(message.getArguments().size() <= 3){
				Object value = message.getArguments().get(2);
				if(value instanceof Float){
					for(PdListener l : listener) l.receiveFloat(name, (Float)value);
				}
				else if(value instanceof String){
					for(PdListener l : listener) l.receiveSymbol(name, (String)value);
				}
				else{
					Gdx.app.error("Pd", "warning unsupported type for " + value.toString());
				}
			}
			// case of multi args : message
			else{
				Object msg = message.getArguments().get(2);
				if(!(msg instanceof String)){
					throw new GdxRuntimeException("first argument of message should be a string, actual : " + String.valueOf(msg));
				}
				Object[] arguments = new Object[message.getArguments().size() - 3];
				for(int i=0 ; i<arguments.length ; i++) arguments[i] = message.getArguments().get(i+3);
				for(PdListener l : listener) l.receiveMessage(name, (String)msg, arguments);
			}
		}else if("list".equals(type)){
			Object[] arguments = new Object[message.getArguments().size() - 2];
			for(int i=0 ; i<arguments.length ; i++) arguments[i] = message.getArguments().get(i+2);
			for(PdListener l : listener) l.receiveList(name, arguments);
		}else{
			throw new GdxRuntimeException("OSC send message unsupported type : " + type + ", expect msg or list");
		}
		
	}
	
	private void sendClearSubscriptions()
	{
		OSCMessage msg = new OSCMessage("/clear");
		try {
			sender.send(msg);
		} catch (IOException e) {
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
		receiver.stopListening();
		receiver.close();
		sender.close();
		receiver = null;
		sender = null;
		listeners.clear();
	}

	@Override
	public int arraySize(String name) {
		Gdx.app.error("PdAudioRemote", "get array size not supported in remote mode");
		return 0;
	}

	@Override
	public void close(PdPatch patch) {
		// nothing to close (dummy patch).
	}

	@Override
	public PdPatch open(FileHandle file) {
		// return a dummy patch
		return new PdPatch(0);
	}

	@Override
	public void sendBang(String recv) {
		OSCMessage msg = new OSCMessage("/send/" + recv);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendList(String recv, Object... values) {
		Collection<Object> args = new ArrayList<Object>();
		for(Object o : values) args.add(o);
		OSCMessage msg = new OSCMessage("/send/" + recv, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String recv, String message, Object... values) {
		Collection<Object> args = new ArrayList<Object>();
		args.add(message);
		for(Object o : values) args.add(o);
		OSCMessage msg = new OSCMessage("/send/" + recv, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendSymbol(String recv, String sym) {
		Collection<Object> args = new ArrayList<Object>();
		args.add(sym);
		OSCMessage msg = new OSCMessage("/send/" + recv, args);
		try {
			sender.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readArray(float[] destination, int destOffset, String source, int srcOffset, int n) {
		Gdx.app.error("PdAudioRemote", "read array not supported in remote mode");
	}

	@Override
	public void writeArray(String destination, int destOffset, float[] source, int srcOffset, int n) 
	{
		// since pd oscparse doesn't support array type, we need to use standard message
		// which can be slow for big data.
		// More, OSC doesn't support really big messages ... then we need to break it up.
		final int MAX_FLOAT_PER_MESSAGES = 128;
		
		Collection<Object> args = new ArrayList<Object>();
		for(int position = 0 ; position < n ; )
		{
			args.clear();
			args.add(destOffset + position);
			for(int i=0 ; i<MAX_FLOAT_PER_MESSAGES && position < n ; i++, position++){
				args.add(source[srcOffset+position]);
			}
			OSCMessage msg = new OSCMessage("/write/" + destination, args);
			try {
				sender.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// required to wait a little in order to not lost so many packets.
			// without this, array in pd will be corrupted at some point ...
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
