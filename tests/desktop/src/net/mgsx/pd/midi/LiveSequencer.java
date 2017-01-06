package net.mgsx.pd.midi;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.util.MidiEventListener;

public class LiveSequencer {

	private final Array<LiveTrack> tracks = new Array<LiveTrack>();
	
	private volatile boolean shouldPlay = true;
	
	private final MidiEventListener listener;
	
	private Thread thread;
	
	/** ticks per quarter note */
	private int resolution;
	
	public LiveSequencer(MidiEventListener listener) {
		this.listener = listener;
	}
	
	public void load(MidiFile file)
	{
		resolution = file.getResolution(); // TODO resolution may change between files but not in tracks !
		
		for(MidiTrack track : file.getTracks()){
			tracks.add(new LiveTrack(file, track, listener));
		}
	}
	
	public Array<LiveTrack> getTracks() {
		return tracks;
	}

	public LiveTrack getTrack(int index) {
		return tracks.get(index);
	}

	public void stop() {
		shouldPlay = false;
	}
	
	public void play() 
	{
		// copy tracks reference to avoid concurrent access on list.
		final Array<LiveTrack> runningTracks = new Array<LiveTrack>(tracks);
		shouldPlay = true;
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				long absoluteTimeNS = System.nanoTime();
				long relativeTimeNS = 0;
				int BPM = 100;
				float ticksPerSec = (BPM / 60f) * resolution;
				
				// reset track before a new run
				for(LiveTrack track : runningTracks){
					track.reset();
				}
				while(shouldPlay){
					relativeTimeNS = System.nanoTime() - absoluteTimeNS;
					
					float timeS = (float)relativeTimeNS / 1e9f;
					long posTick = (long)(timeS * ticksPerSec);
					for(LiveTrack track : runningTracks){
						if(track.active) track.update(posTick);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// silent fail.
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
		}, "LiveSequencer");
		thread.start();
	}

}
