package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.meta.CopyrightNotice;
import net.mgsx.midi.sequence.event.meta.Tempo;
import net.mgsx.midi.sequence.event.meta.Text;
import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.MidiMusic;
import net.mgsx.pd.patch.PdPatch;

public class MidiMusicDemo implements Demo
{
	private PdPatch patch;
	private MidiMusic music;
	
	private Label copyrightPlaceholder;
	private Slider tempoController;
	 
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
		
		tempoController = new Slider(60, 240, 1, false, skin);
		tempoController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(music != null){
					music.setBPM(tempoController.getValue());
				}
			}
		});
		
		ProgressBar songProgress = new ProgressBar(0, 1, .01f, false, skin){
			@Override
			public void act(float delta) {
				super.act(delta);
				setValue(music.getPosition() / music.getDuration());
			}
		};
		
		final Slider positionController = new Slider(0, 1, .01f, false, skin);
		positionController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				music.setPosition(positionController.getValue() * music.getDuration());
			}
		});
		
		final Slider reverbController = new Slider(0, 1, .01f, false, skin);
		reverbController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendFloat("reverb", reverbController.getValue());
			}
		});
		
		final TextButton btPlayStop = new TextButton("Stop", skin);
		btPlayStop.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(music.isPlaying()){
					btPlayStop.setText("Play");
					music.stop();
				}else{
					btPlayStop.setText("Stop");
					music.play();
				}
				
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
		
		root.add("Progress");
		root.add(songProgress);
		root.row();
		
		
		root.add("Reverb");
		root.add(reverbController);
		root.row();
		
		root.add("Playback");
		root.add(btPlayStop);
		root.row();
		
		root.add("Copyright Notice");
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
		MidiSequence sequence = new MidiSequence(file);
		
		// display song meta (copyright notice and text)
		String copyrightText = "";
		for(CopyrightNotice event : sequence.findEvents(new Array<CopyrightNotice>(), CopyrightNotice.class)){
			copyrightText += event.getNotice() + "\n";
		}
		for(Text event : sequence.findEvents(new Array<Text>(), Text.class)){
			copyrightText += event.getText() + "\n";
		}
		copyrightPlaceholder.setText(copyrightText.trim());
		
		// get first tempo change
		for(Tempo event : sequence.findEvents(new Array<Tempo>(), Tempo.class)){
			tempoController.setValue(event.getBpm());
			break;
		}
		
		// create and play music with default sequencer
		music = Pd.midi.createMidiMusic(sequence);
		music.play();
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
