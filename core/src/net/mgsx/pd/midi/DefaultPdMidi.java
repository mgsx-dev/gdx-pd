package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.util.MidiEventListener;
import net.mgsx.midi.sequence.util.MidiProcessor;

public class DefaultPdMidi implements PdMidi
{
	private final static DefaultPdMidiSynth synth = new DefaultPdMidiSynth();
	
	@Override
	public MidiMusic createMidiMusic(FileHandle file) 
	{
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
