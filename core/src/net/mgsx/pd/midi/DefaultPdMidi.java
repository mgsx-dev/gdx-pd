package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.playback.PdMidiSynth;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.util.MidiProcessor;

public class DefaultPdMidi implements PdMidi
{

	@Override
	public MidiMusic createMidiMusic(FileHandle file) 
	{
		return createMidiMusic(new MidiSequence(file));
	}

	@Override
	public MidiMusic createMidiMusic(MidiSequence sequence) 
	{
		MidiProcessor sequencer = new MidiProcessor(sequence);
		sequencer.registerEventListener(PdMidiSynth.instance, MidiEvent.class);
		
		return new DefaultMidiMusic(sequencer, sequence);
	}

}
