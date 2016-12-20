package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

public class DefaultPdMidi implements PdMidi
{

	@Override
	public MidiMusic createMidiMusic(FileHandle file) 
	{
		return new DefaultMidiMusic(file);
	}

}
