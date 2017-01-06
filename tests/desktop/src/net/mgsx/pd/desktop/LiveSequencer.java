package net.mgsx.pd.desktop;

import org.puredata.core.PdBase;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ChannelEvent;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.util.MidiEventListener;

public class LiveSequencer {

	public static class ResetNote extends NoteOn
	{
		public ResetNote() {
			super(0,0,0,0);
		}
		
		public ResetNote set(int channel, int note){
			this.mChannel = channel;
			this.mValue1 = note;
			return this;
		}
	}
	
	public static class Track{
		
		MidiEventListener listener;
		public long loopStart, loopEnd, trackEnd;
		private int nextLoopStart, nextLoopEnd, modulus;
		public boolean loop = false;
		public boolean nextLoop = false;
		public int index, loopStartIndex;
		int resolution;
		
		final private Array<MidiEvent> events;
		
		private int [] notes = new int[127];
		
		private ResetNote off = new ResetNote();
		
		int channel;
		
		public Track(MidiFile file, MidiTrack track, MidiEventListener listener) {
			events = new Array<MidiEvent>();
			for(MidiEvent e : track.getEvents()){
				events.add(e);
				if(e instanceof ChannelEvent){
					channel = ((ChannelEvent) e).getChannel();
				}
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
		}
		public void setLoop(int startBeat, int endBeat, int modulus){
			nextLoopStart = startBeat;
			nextLoopEnd = endBeat;
			nextLoop = true;
			this.modulus = modulus;
		}
		
		long prePos;
		private boolean active = true;
		
		public long update(long position)
		{
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
				
				for(int i=0 ; i<notes.length ; i++){
					if(notes[i] != 0){
						listener.onEvent(off.set(channel, i), 0);
						notes[i] = 0;
					}
				}
				
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
					notes[e.getNoteValue()] = e.getVelocity();
				}else if(nextEvent instanceof NoteOff){
					NoteOff e = ((NoteOff) nextEvent);
					notes[e.getNoteValue()] = 0;
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
			if(!active)
			for(int i=0 ; i<notes.length ; i++){
				if(notes[i] != 0){
					listener.onEvent(off.set(channel, i), 0);
					notes[i] = 0;
				}
			}
		}

		public int getPosition() {
			return (int)(prePos / resolution);
		}

		public void loop(boolean b) {
			loop = b;
			setLoop(0, (int)(trackEnd / resolution));
		}
	}
	
	private Array<Track> tracks = new Array<Track>();
	
	boolean shouldPlay = true;
	float timePos = 0;
	MidiFile file;
	public void load(MidiFile file)
	{
		this.file = file;
		MidiEventListener listener = new MidiEventListener() {
			
			@Override
			public void onStop(boolean finished) {
				System.out.println("onStop");
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
				System.out.println("onStart");
				
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
				if(event instanceof NoteOn){
					NoteOn no = (NoteOn)event;
					PdBase.sendNoteOn(no.getChannel(), no.getNoteValue(), no.getVelocity());
				}
				else if(event instanceof NoteOff){
					NoteOff no = (NoteOff)event;
					PdBase.sendNoteOn(no.getChannel(), no.getNoteValue(), 0);
				}
				else if(event instanceof ProgramChange){
					ProgramChange no = (ProgramChange)event;
					PdBase.sendProgramChange(no.getChannel(), no.getProgramNumber());
				}
				
			}
		};
		
		MidiEventListener debugListener = new MidiEventListener() {
			
			@Override
			public void onStop(boolean finished) {
				System.out.println("onStop");
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
				System.out.println("onStart");
				
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
				System.out.println(event);
			}
		};
		
		// nombre de tick à la noire !
		file.getResolution();
		int c = 0;
		for(MidiTrack track : file.getTracks()){
			Track t = new Track(file, track, listener);
			// t.setLoop(42, 43);
			tracks.add(t); // XXX first track
			c++;
		}
	}

	long prevTimeNS, timeNS;
	
	public void play() 
	{
		shouldPlay = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				prevTimeNS = System.nanoTime();
				timeNS = 0;
				int BPM = 100;
				float ticksPerSec = (BPM / 60f) * file.getResolution();
				for(Track track : tracks){
					track.sendMeta();
				}
				while(shouldPlay){
					timeNS = System.nanoTime() - prevTimeNS;
					
					float timeS = (float)timeNS / 1e9f;
					long posTick = (long)(timeS * ticksPerSec);
					// timePos
					for(Track track : tracks){
						if(track.active) track.update(posTick);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}

	public void sched(int chan, int start, int end) 
	{
		if(chan >= 0 && chan < tracks.size) tracks.get(chan).setLoop(start, end);
	}
	public void sched(int chan, int start, int end, int modulus) 
	{
		if(chan >= 0 && chan < tracks.size) tracks.get(chan).setLoop(start, end, modulus);
	}
	
	public void mute(int chan, boolean on){
		if(chan >= 0 && chan < tracks.size) tracks.get(chan).mute(on);
	}

	public int getPosition(int chan) {
		if(chan >= 0 && chan < tracks.size) return tracks.get(chan).getPosition();
		return 0;
	}

	public void loop(int chan, boolean b) {
		if(chan >= 0 && chan < tracks.size) tracks.get(chan).loop(b);
	}
}
