package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.sequence.MidiSequence;

public interface PdMidi {

	MidiMusic createMidiMusic(FileHandle file);

	MidiMusic createMidiMusic(MidiSequence sequence);

}
