package net.mgsx.midi.sequence;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import net.mgsx.midi.sequence.event.ChannelAftertouch;
import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteAftertouch;
import net.mgsx.midi.sequence.event.NoteOff;
import net.mgsx.midi.sequence.event.NoteOn;
import net.mgsx.midi.sequence.event.PitchBend;
import net.mgsx.midi.sequence.event.ProgramChange;
import net.mgsx.midi.sequence.event.SystemExclusiveEvent;
import net.mgsx.midi.sequence.event.meta.CopyrightNotice;
import net.mgsx.midi.sequence.event.meta.CuePoint;
import net.mgsx.midi.sequence.event.meta.EndOfTrack;
import net.mgsx.midi.sequence.event.meta.InstrumentName;
import net.mgsx.midi.sequence.event.meta.KeySignature;
import net.mgsx.midi.sequence.event.meta.Lyrics;
import net.mgsx.midi.sequence.event.meta.Marker;
import net.mgsx.midi.sequence.event.meta.MidiChannelPrefix;
import net.mgsx.midi.sequence.event.meta.SequenceNumber;
import net.mgsx.midi.sequence.event.meta.SequencerSpecificEvent;
import net.mgsx.midi.sequence.event.meta.SmpteOffset;
import net.mgsx.midi.sequence.event.meta.Tempo;
import net.mgsx.midi.sequence.event.meta.Text;
import net.mgsx.midi.sequence.event.meta.TimeSignature;
import net.mgsx.midi.sequence.event.meta.TrackName;

public class MidiReadTest {

	private MidiSequence load(String name) throws Exception
	{
		return new MidiSequence(new FileInputStream(new File("test-generator/" + name)));
	}
	
	@Test
	public void testParseSysEx() throws Exception
	{
		MidiSequence seq = load("test-sysex.mid");
		MidiEvent[] events = seq.getTracks().get(0).getEvents().toArray(new MidiEvent[]{});

		assertTrue(events[1] instanceof SystemExclusiveEvent);
		
		SystemExclusiveEvent sysex = (SystemExclusiveEvent)events[1];
		assertEquals(5, sysex.getData().length);
		
		assertEquals(12, sysex.getData()[0]);
		assertEquals(13, sysex.getData()[1]);
		assertEquals(14, sysex.getData()[2]);
		assertEquals(15, sysex.getData()[3]);
		assertEquals(16, sysex.getData()[4]);
	}
	
	@Test
	public void testParseMeta() throws Exception
	{
		MidiSequence seq = load("test-meta.mid");
		MidiTrack track = seq.getTracks().get(0);
		assertEquals(15, track.getEventCount());
		
		MidiEvent[] events = track.getEvents().toArray(new MidiEvent[]{});
		
		assertTrue(events[0] instanceof InstrumentName);
		assertTrue(events[1] instanceof KeySignature);
		assertTrue(events[2] instanceof Marker);
		assertTrue(events[3] instanceof Tempo);
		assertTrue(events[4] instanceof TimeSignature);
		assertTrue(events[5] instanceof SequenceNumber);
		assertTrue(events[6] instanceof Text);
		assertTrue(events[7] instanceof CopyrightNotice);
		assertTrue(events[8] instanceof TrackName);
		assertTrue(events[9] instanceof Lyrics);
		assertTrue(events[10] instanceof CuePoint);
		assertTrue(events[11] instanceof MidiChannelPrefix);
		assertTrue(events[12] instanceof SmpteOffset);
		assertTrue(events[13] instanceof SequencerSpecificEvent);
		assertTrue(events[14] instanceof EndOfTrack);
	}
	
	@Test
	public void testParseBasic() throws Exception
	{
		MidiSequence seq = load("test-basic.mid");
		assertEquals(1, seq.getTrackCount());
		MidiTrack track = seq.getTracks().get(0);
		
		// instrument name + 7 events + end of tracks
		assertEquals(9, track.getEventCount());
		
		MidiEvent[] events = track.getEvents().toArray(new MidiEvent[]{});
		
		assertTrue(events[1] instanceof ChannelAftertouch);
		assertTrue(events[2] instanceof Controller);
		assertTrue(events[3] instanceof NoteOff);
		assertTrue(events[4] instanceof NoteOn);
		assertTrue(events[5] instanceof NoteAftertouch);
		assertTrue(events[6] instanceof PitchBend);
		assertTrue(events[7] instanceof ProgramChange);
		
		NoteOn note = (NoteOn) events[4];
		assertEquals(0, note.getChannel());
		assertEquals(64, note.getNoteValue());
		assertEquals(127, note.getVelocity());
		assertEquals(0, note.getTick());
	}
}
