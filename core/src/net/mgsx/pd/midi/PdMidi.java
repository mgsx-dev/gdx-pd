package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

public interface PdMidi {

	MidiMusic createMidiMusic(FileHandle file);

}
