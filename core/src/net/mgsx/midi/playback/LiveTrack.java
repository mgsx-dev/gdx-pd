package net.mgsx.midi.playback;

import com.badlogic.gdx.utils.Array;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.MidiTrack;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.ProgramChange;
import net.mgsx.midi.sequence.event.meta.Tempo;
import net.mgsx.midi.sequence.util.MidiEventListener;

public class LiveTrack
{
	final private MidiEventListener listener;
	private long loopStart, loopEnd, trackEnd;
	private int nextLoopStart, nextLoopEnd, modulus;
	private boolean loop = false;
	private boolean nextLoop = false;
	private int index, loopStartIndex;
	private int resolution;
	private long offset;
	private long virtualPosition;
	
	final private Array<MidiEvent> events;
	
	private final LiveSequencer master;
	
	// TODO sequence, just need resolution !
	// TODO master, just need to change tempo
	public LiveTrack(LiveSequencer master, MidiSequence file, MidiTrack track, MidiEventListener listener) {
		this.master = master;
		events = new Array<MidiEvent>();
		for(MidiEvent e : track.getEvents()){
			events.add(e);
		}
		loopStart = 0;
		trackEnd = loopEnd = track.getLengthInTicks();
		index = loopStartIndex = 0;
		resolution = file.getResolution();
		this.listener = listener;
		prePos = 0;
	}
	
	public boolean isNextLoop() {
		return nextLoop;
	}
	
	public long getLoopStart() {
		return loopStart;
	}

	public long getLoopEnd() {
		return loopEnd;
	}

	public long getTrackEnd() {
		return trackEnd;
	}

	public int getNextLoopStart() {
		return nextLoopStart;
	}

	public int getNextLoopEnd() {
		return nextLoopEnd;
	}

	public int getResolution() {
		return resolution;
	}

	public boolean isRunning() {
		return running;
	}



	private void setLoop(int startBeat, int endBeat){
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
		loop = true;
		offset = 0;
	}
	public void setLoop(int startBeat, int endBeat, int modulus){
		nextLoopStart = startBeat;
		nextLoopEnd = endBeat;
		nextLoop = true;
		this.modulus = modulus;
	}
	
	long prePos;
	boolean active = true;
	private boolean running = false;
	
	public long update(long position)
	{
		if(events.size == 0)
		{
			return 0;
		}
		
		virtualPosition = position;
		
		if(nextLoop){
			int mod = modulus * resolution;
			long pPosMod = (prePos % mod + mod) % mod;
			long cPosMod = (position % mod + mod) % mod;;
			if(pPosMod > cPosMod){
				setLoop(nextLoopStart, nextLoopEnd);
				nextLoop = false;
			}
		}
		
		long inPos;
		
		if(loop){
			long loopLen = loopEnd - loopStart;
			if(loopLen <= 0) return 0;
			inPos = loopStart + (((position) % loopLen) + loopLen) % loopLen;
		}else{
			inPos = position + offset;
		}
		
		if(prePos > trackEnd){
			prePos = inPos;
			return trackEnd;
		}
		
		if(!running){
			// send some event at first run
			sendProgramChange(inPos);
			running = true;
		}
		
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
		}
		
		while(inPos >= nextEvent.getTick()){
			// handle tempo change 
			// TODO first track only ?
			// TODO should be handled by sequencer not track
			if(nextEvent instanceof Tempo){
				master.bpm = ((Tempo) nextEvent).getBpm();
			}
			// dispatch event
			listener.onEvent(nextEvent, 0);
			
			// move to next event
			// or stay on last event if track exhausted
			if(index < events.size-1){
				index++;
				nextEvent = events.get(index);
			}else{
				break;
			}
		}
		prePos = inPos;
		// return tick
		return nextEvent.getTick();
	}

	private void sendProgramChange(long position) {
		for(int i=0 ; i<events.size ; i++){
			MidiEvent nextEvent = events.get(i);
			if(nextEvent instanceof ProgramChange && nextEvent.getTick() <= position){
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

	// TODO sync ?
	public void unloop() {
		if(loop){
			long loopLen = loopEnd - loopStart;
			long localPos = ((virtualPosition % loopLen) + loopLen) % loopLen;
			offset = loopStart + localPos - virtualPosition;
			loop = false;
		}
	}

	public int endBeat() {
		return (int)(events.get(events.size-1).getTick() / resolution);
	}

	public void sendNotesOff() 
	{
		// TODO only this track (this channel)
		master.sendAllNotesOff();
	}

	public Array<MidiEvent> getEvents() {
		return events;
	}

	public void reset() 
	{
		running  = false;
	}
}