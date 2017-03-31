package net.mgsx.midi.playback;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.MidiTrack;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.meta.EndOfTrack;
import net.mgsx.midi.sequence.event.meta.Tempo;
import net.mgsx.midi.sequence.util.MidiEventListener;

public class LiveSequencerTest {

	protected Array<MidiEvent> playedEvents = new Array<MidiEvent>();
	protected MidiEventListener listener;
	protected int startCount, stopCount;
	protected LiveSequencer sequencer;
	protected MidiSequence mSeq;
	
	@Before
	public void setup(){
		startCount = 0;
		stopCount = 0;
		playedEvents.clear();
		listener = new MidiEventListener() {
			@Override
			public void onStop(boolean finished) {
				stopCount++;
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
				startCount++;
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
				playedEvents.add(event);
			}
		};
		sequencer = new LiveSequencer(listener);
		mSeq = new MidiSequence();
		mSeq.setResolution(1);
	}
	
	@Test
	public void testEmptyTrack()
	{
		MidiTrack mTrack = new MidiTrack();
		LiveTrack track = new LiveTrack(sequencer, mSeq, mTrack, listener);
		track.update(12);
		assertEquals(0, playedEvents.size);
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
	}
	
	@Test
	public void testOnlyEndOfTrack()
	{
		MidiTrack mTrack = new MidiTrack();
		EndOfTrack eot = new EndOfTrack(0, 0);
		mTrack.insertEvent(eot);
		
		LiveTrack track = new LiveTrack(sequencer, mSeq, mTrack, listener);
		track.update(12);
		assertEquals(1, playedEvents.size);
		assertSame(eot, playedEvents.get(0));
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
	}
	
	@Test
	public void testNoLoopAfterAllEventsWithTempo()
	{
		MidiTrack mTrack = new MidiTrack();
		
		Tempo tempo = new Tempo();
		tempo.setBpm(120);
		mTrack.insertEvent(tempo);
		
		sequencer.setBPM(100);
		LiveTrack track = new LiveTrack(sequencer, mSeq, mTrack, listener);
		
		// first pass => tempo is applied
		
		track.update(20);
		
		assertEquals(1, playedEvents.size);
		assertSame(tempo, playedEvents.get(0));
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
		
		assertEquals(120, sequencer.getBPM(), 1e-10f);
		
		// second pass => tempo shouldn't be applied
		
		playedEvents.clear();
		
		sequencer.setBPM(100);
		
		track.update(21);
		
		assertEquals(0, playedEvents.size);
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
		
		assertEquals(100, sequencer.getBPM(), 1e-10f);
	}
	
	@Test
	public void testLoopAfterAllEventsWithTempo()
	{
		MidiTrack mTrack = new MidiTrack();
		
		Tempo tempo = new Tempo();
		tempo.setBpm(120);
		mTrack.insertEvent(tempo);
		
		sequencer.setBPM(100);
		LiveTrack track = new LiveTrack(sequencer, mSeq, mTrack, listener);
		
		track.setLoop(16, 32, 1);
		
		// first pass => tempo is applied
		
		track.update(20);
		
		
		assertEquals(1, playedEvents.size);
		assertSame(tempo, playedEvents.get(0));
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
		
		assertEquals(120, sequencer.getBPM(), 1e-10f);
		
		// second pass => tempo shouldn't be applied
		
		playedEvents.clear();
		
		sequencer.setBPM(100);
		
		track.update(21);
		
		assertEquals(0, playedEvents.size);
		assertEquals(0, stopCount);
		assertEquals(0, startCount);
		
		assertEquals(100, sequencer.getBPM(), 1e-10f);
	}
}
