package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.MidiMusic;
import net.mgsx.pd.patch.PdPatch;

public class MidiDemo implements Demo
{
	private PdPatch patch;
	private MidiMusic music;
	
	@Override
	public Actor create(Skin skin) {
		patch = Pd.audio.open(Gdx.files.internal("pdmidi/midiplayer.pd"));
		music = Pd.midi.createMidiMusic(Gdx.files.internal("MuteCity.mid"));
		music.play();
		return new Label("Song playing ...", skin);
	}
	
	@Override
	public void dispose() {
		music.dispose();
		Pd.audio.close(patch);
	}
	
	@Override
	public String toString() {
		return "Midi";
	}

}
