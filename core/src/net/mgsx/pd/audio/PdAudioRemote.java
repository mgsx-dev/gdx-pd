package net.mgsx.pd.audio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.patch.PdPatch;

public class PdAudioRemote implements PdAudio
{
	OSCPortOut sender;
	
	@Override
	public void create(PdConfiguration config) 
	{
		try {
			sender = new OSCPortOut(InetAddress.getByName("localhost"), 3000); // TODO config ?
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
	public void addListener(String arg0, PdListener arg1) {
		// TODO support remote listener
		Gdx.app.error("PdAudioRemote", "listeners not supported in remote mode");
	}

	@Override
	public void removeListener(String arg0, PdListener arg1) {
		// TODO support remote listener
		Gdx.app.error("PdAudioRemote", "listeners not supported in remote mode");
	}

}
