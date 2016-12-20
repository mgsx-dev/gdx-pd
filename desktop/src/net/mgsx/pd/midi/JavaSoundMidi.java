package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

public class JavaSoundMidi implements PdMidi {

	@Override
	public MidiMusic createMidiMusic(FileHandle file) {
		
		JavaMidiMusic music = new JavaPdMidiMusic();
		music.setSequence(file);
		return music;
	}

}
