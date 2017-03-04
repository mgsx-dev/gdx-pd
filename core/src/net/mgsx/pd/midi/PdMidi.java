package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.util.MidiEventListener;

public interface PdMidi {

	MidiMusic createMidiMusic(FileHandle file);

	MidiMusic createMidiMusic(MidiSequence sequence);

	MidiEventListener getPdSynth();
}
