package net.mgsx.pd.midi;

import org.puredata.core.PdBase;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.util.MidiEventListener;

public class LiveSequencer {

	private Array<LiveTrack> tracks = new Array<LiveTrack>();
	
	boolean shouldPlay = true;
	float timePos = 0;
	MidiFile file;
	MidiEventListener listener;
	public void load(MidiFile file)
	{
		this.file = file;
		listener = new MidiEventListener() {
			
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
		
		// nombre de tick à la noire !
		file.getResolution();
		for(MidiTrack track : file.getTracks()){
			LiveTrack t = new LiveTrack(file, track, listener);
			tracks.add(t);
		}
	}

	long prevTimeNS, timeNS;
	
	public void stop() {
		shouldPlay = false;
	}
	
	public void play() 
	{
		final Array<LiveTrack> runningTracks = new Array<LiveTrack>(tracks);
		shouldPlay = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				prevTimeNS = System.nanoTime();
				timeNS = 0;
				int BPM = 100;
				float ticksPerSec = (BPM / 60f) * file.getResolution();
				for(LiveTrack track : runningTracks){
					track.sendMeta();
				}
				while(shouldPlay){
					timeNS = System.nanoTime() - prevTimeNS;
					
					float timeS = (float)timeNS / 1e9f;
					long posTick = (long)(timeS * ticksPerSec);
					// timePos
					for(LiveTrack track : runningTracks){
						if(track.active) track.update(posTick);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// XXX force all note off (all note and all channels)
				ResetNote off = new ResetNote();
				for(int i=0 ; i<16 ; i++){
					for(int j=0 ; j<127 ; j++){
						listener.onEvent(off.set(i, j), 0);
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

	public Array<LiveTrack> getTracks() {
		return tracks;
	}

	
}
