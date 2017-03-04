package net.mgsx.pd.midi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.ChannelAftertouch;
import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteAftertouch;
import net.mgsx.midi.sequence.event.NoteOff;
import net.mgsx.midi.sequence.event.NoteOn;
import net.mgsx.midi.sequence.event.PitchBend;
import net.mgsx.midi.sequence.event.ProgramChange;
import net.mgsx.midi.sequence.util.MidiEventListener;
import net.mgsx.midi.sequence.util.MidiProcessor;

/**
 * Remote (java socket) implementation of PdMidi.
 * 
 * Enable passing sequencer stream to network both in broadcast or unicast mode supported.
 * Use one of {@link #createDefaultBroadcast()} or {@link #createDefaultUnicast()} if you use
 * default values.
 * Use {@link #PdMidiRemote(String, int)} if you want to use non default values.
 * 
 * @author mgsx
 *
 */
public class PdMidiRemote implements PdMidi
{
	private DatagramSocket datagramSocket;

	final private byte[] datagramBuffer = new byte[3];
	
	private DatagramPacket p3, p2;
	
	private MidiEventListener synth = new MidiEventListener() {
		@Override
		public void onStop(boolean finished){
		}

		@Override
		public void onStart(boolean fromBeginning) {
		}

		@Override
		public void onEvent(MidiEvent event, long ms) {
			DatagramPacket p = null;
			if(event instanceof NoteOn){
				NoteOn me = (NoteOn)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | (me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getNoteValue() & 0xFF);
				datagramBuffer[2] = (byte)(me.getVelocity() & 0xFF);
				p = p3;
			}
			else if(event instanceof NoteOff){
				NoteOff me = (NoteOff)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | (me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getNoteValue() & 0xFF);
				datagramBuffer[2] = (byte)(me.getVelocity() & 0xFF);
				 p = p3;
			}
			else if(event instanceof Controller){
				Controller me = (Controller)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | ( me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getControllerType()& 0xFF);
				datagramBuffer[2] = (byte)(me.getValue() & 0xFF);
				 p = p3;
			}
			else if(event instanceof PitchBend){
				PitchBend me = (PitchBend)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | ( me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getLeastSignificantBits() & 0xFF);
				datagramBuffer[2] = (byte)(me.getMostSignificantBits()& 0xFF);
				 p = p3;
			}
			else if(event instanceof NoteAftertouch){
				NoteAftertouch me = (NoteAftertouch)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | ( me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getNoteValue()& 0xFF);
				datagramBuffer[3] = (byte)(me.getAmount() & 0xFF);
				 p = p3;
			}
			else if(event instanceof ChannelAftertouch){
				ChannelAftertouch me = (ChannelAftertouch)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | ( me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getAmount() & 0xFF);
				 p = p2;
			}
			else if(event instanceof ProgramChange){
				ProgramChange me = (ProgramChange)event;
				datagramBuffer[0] = (byte)(((me.getType() << 4)  & 0xFF) | ( me.getChannel() & 0xFF));
				datagramBuffer[1] = (byte)(me.getProgramNumber()& 0xFF);
				 p = p2;
			}
			if(p != null){
				try {
					datagramSocket.send(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	};
	
	/**
	 * create PdMidiRemote with default broadcast configuration (qmidinet default 225.0.0.37:21928)
	 * @return
	 */
	public static PdMidiRemote createDefaultBroadcast()
	{
		return new PdMidiRemote("225.0.0.37", 21928);
	}
	
	/**
	 * create PdMidiRemote with default unicast configuration (gdx-net default localhost:3001)
	 * @return
	 */
	public static PdMidiRemote createDefaultUnicast()
	{
		return new PdMidiRemote("localhost", 3001);
	}
	
	public PdMidiRemote(String host, int port) 
	{
		if(datagramSocket != null) return;
		p3 = new DatagramPacket(datagramBuffer, 3);
		p2 = new DatagramPacket(datagramBuffer, 2);
		try {
			InetAddress address = InetAddress.getByName(host);
			datagramSocket = new DatagramSocket();
			p3.setAddress(address);
			p3.setPort(port);
			p2.setAddress(address);
			p2.setPort(port);
			
		} catch (SocketException e) {
			throw new GdxRuntimeException(e);
		} catch (UnknownHostException e) {
			throw new GdxRuntimeException(e);
		}
			 
	}
	
	
	@Override
	public MidiMusic createMidiMusic(FileHandle file) {
		return createMidiMusic(new MidiSequence(file));
	}

	@Override
	public MidiMusic createMidiMusic(MidiSequence sequence) 
	{
		MidiProcessor sequencer = new MidiProcessor(sequence);
		sequencer.registerEventListener(synth, MidiEvent.class);
		
		return new DefaultMidiMusic(sequencer, sequence);
	}

	@Override
	public MidiEventListener getPdSynth() {
		return synth;
	}

}
