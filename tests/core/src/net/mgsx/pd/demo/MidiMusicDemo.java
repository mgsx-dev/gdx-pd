package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.MidiMusic;
import net.mgsx.pd.patch.PdPatch;

public class MidiMusicDemo implements Demo
{
	private PdPatch patch;
	private MidiMusic music;
	
	private Label copyrightPlaceholder;
	
	@Override
	public Actor create(Skin skin) 
	{
		Table root = new Table(skin);
		
		final SelectBox<FileHandle> songSelector = new SelectBox<FileHandle>(skin);
		songSelector.setItems(Gdx.files.internal("music").list(".mid"));
		
		songSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeSong(songSelector.getSelected());
			}
		});
		
		Slider tempoController = new Slider(40, 180, 100, false, skin);
		tempoController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO change tempo
			}
		});
		
		Slider positionController = new Slider(0, 1, 100, false, skin){
			@Override
			public void act(float delta) {
				super.act(delta);
				// TODO get music length setValue(music.getPosition() / music.)
			}
		};
		positionController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO change position
			}
		});
		
		TextButton btPlayStop = new TextButton("Stop", skin);
		btPlayStop.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO change position
			}
		});
		
		root.add("Song");
		root.add(songSelector);
		root.row();
		
		root.add("Tempo");
		root.add(tempoController);
		root.row();
		
		root.add("Position");
		root.add(positionController);
		root.row();
		
		root.add("Playback");
		root.add(btPlayStop);
		root.row();
		
		root.add("Copyright");
		root.add(copyrightPlaceholder = new Label("", skin));
		root.row();
		
		patch = Pd.audio.open(Gdx.files.internal("pdmidi/midiplayer.pd"));
		
		changeSong(songSelector.getSelected());
		
		return root;
	}
	
	protected void changeSong(FileHandle file) 
	{
		if(music != null){
			music.dispose();
		}
		music = Pd.midi.createMidiMusic(file);
		music.play();
		
		// TODO display song meta (copyright notice and text)
	}

	@Override
	public void dispose() {
		music.dispose();
		Pd.audio.close(patch);
	}
	
	@Override
	public String toString() {
		return "Music";
	}

}
