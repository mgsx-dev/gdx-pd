package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.util.MidiEventListener;

public class JavaSoundMidi implements PdMidi {

	@Override
	public MidiMusic createMidiMusic(FileHandle file) {
		
		JavaMidiMusic music = new JavaPdMidiMusic();
		music.setSequence(file);
		return music;
	}

	@Override
	public MidiMusic createMidiMusic(MidiSequence sequence) {
		// TODO implement ?
		throw new GdxRuntimeException("not implemented");
	}

	@Override
	public MidiEventListener getPdSynth() {
		// TODO implement ?
		throw new GdxRuntimeException("not implemented");
	}

}
