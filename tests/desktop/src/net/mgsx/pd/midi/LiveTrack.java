package net.mgsx.pd.midi;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.util.MidiEventListener;

public class LiveTrack
{
	MidiEventListener listener;
	public long loopStart, loopEnd, trackEnd;
	public int nextLoopStart, nextLoopEnd, modulus;
	public boolean loop = false;
	public boolean nextLoop = false;
	public int index, loopStartIndex;
	public int resolution;
	
	final private Array<MidiEvent> events;
	
	private int [] notes = new int[127 * 16];
	private int [] notesOnIndices = new int[127 * 16];
	private int notesOnCount = 0;
	
	private ResetNote off = new ResetNote();
	
	public LiveTrack(MidiFile file, MidiTrack track, MidiEventListener listener) {
		events = new Array<MidiEvent>();
		for(MidiEvent e : track.getEvents()){
			events.add(e);
		}
		loopStart = 0;
		trackEnd = loopEnd = track.getLengthInTicks();
		index = loopStartIndex = 0;
		resolution = file.getResolution();
		this.listener = listener;
		prePos = loopEnd + 1;
	}
	
	public void setLoop(int startBeat, int endBeat){
		loopStart = startBeat * resolution;
		loopEnd = endBeat * resolution;
		// find index
		for(int i=0 ; i<events.size ; i++){
			MidiEvent nextEvent = events.get(i);
			if(nextEvent.getTick() >= loopStart){
				loopStartIndex = i;
				break;
			}
		}
		index = loopStartIndex;
		prePos = 0;
		sendNotesOff();
	}
	public void setLoop(int startBeat, int endBeat, int modulus){
		nextLoopStart = startBeat;
		nextLoopEnd = endBeat;
		nextLoop = true;
		this.modulus = modulus;
	}
	
	long prePos;
	boolean active = true;
	
	public long update(long position)
	{
		position += resolution * 0;
		if(position < 0) return 0;
		if(nextLoop){
			
			long pPosMod = prePos % (modulus * resolution);
			long cPosMod = position % (modulus * resolution);
			if(pPosMod > cPosMod){
				setLoop(nextLoopStart, nextLoopEnd);
				nextLoop = false;
			}
		}
		
		long loopLen = loopEnd - loopStart;
		if(loopLen <= 0) return 0;
		long inPos = loopStart + (((position) % loopLen) + loopLen) % loopLen;
		
		// check if nextEvent should be played now
		MidiEvent nextEvent = events.get(index);
		if(inPos < prePos){
			
			// play end loop events
			while(nextEvent.getTick() < loopEnd){
				listener.onEvent(nextEvent, 0);
				if(index < events.size - 1){
					index++;
				}else{
					break;
				}
				nextEvent = events.get(index);
			}
			
			sendNotesOff();
			
			index = loopStartIndex;
			nextEvent = events.get(index);
//				while(inPos > nextEvent.getTick()){
//					if(index < events.size - 1){
//						index++;
//					}else{
//						break;
//					}
//					nextEvent = events.get(index);
//				}
		}
		// System.out.println(inPos);
		while(inPos >= nextEvent.getTick()){
			if(nextEvent instanceof NoteOn){
				NoteOn e = ((NoteOn) nextEvent);
				int index = (e.getChannel() << 7) | e.getNoteValue();
				int value = e.getVelocity();
				if(notes[index] == 0 && value > 0){
					notes[index] = value;
					notesOnIndices[notesOnCount++] = index;
				}
			}
			listener.onEvent(nextEvent, 0);
			if(index < events.size - 1){
				index++;
			}else{
				break;
			}
			nextEvent = events.get(index);
		}
		prePos = inPos;
		// return tick
		return nextEvent.getTick();
	}

	public void sendMeta() {
		for(int i=0 ; i<events.size ; i++){
			MidiEvent nextEvent = events.get(i);
			if(nextEvent instanceof ProgramChange && nextEvent.getTick() == 0){
				ProgramChange pgm = (ProgramChange)nextEvent;
				listener.onEvent(nextEvent, 0);
				System.out.println("pgm change channel " + pgm.getChannel());
			}else if(nextEvent.getTick() == 0 && !(nextEvent instanceof NoteOn)){
				listener.onEvent(nextEvent, 0);
			}
		}
	}

	public void mute(boolean on) {
		active  = on;
		if(!active) sendNotesOff();
	}

	public int getPosition() {
		return (int)(prePos / resolution);
	}

	public void loop(boolean b) {
		loop = b;
		setLoop(0, (int)(trackEnd / resolution));
	}

	public int endBeat() {
		return (int)(events.get(events.size-1).getTick() / resolution);
	}

	public void sendNotesOff() {
		
		for(int i=0 ; i<notesOnCount ; i++){
			int index = notesOnIndices[i];
			int note = index & 0x7F;
			int channel = index >> 7;
			if(notes[index] != 0){
				listener.onEvent(off.set(channel, note), 0);
				notes[index] = 0;
			}
		}
		notesOnCount = 0;
	}

	public Array<MidiEvent> getEvents() {
		return events;
	}
}