package net.mgsx.pd.demo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.TrackName;

import net.mgsx.gdx.pd.PdAudioOpenAL;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.midi.DefaultMidiMusic;
import net.mgsx.pd.midi.DefaultPdMidi;
import net.mgsx.pd.midi.LiveSequencer;
import net.mgsx.pd.midi.LiveTrack;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.midi.PdMidiSynth;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

public class LiveSequencerDemoApplication extends Game 
{
	Stage stage;
	private DefaultMidiMusic song;
	private LiveSequencer seq;
	private AssetManager assets;

	@Override
	public void create() {
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		
		Table table = new Table(skin);
		
		
		table.setFillParent(true);
		
		stage.addActor(table);
		
		Pd.midi = new DefaultPdMidi();
		
		Pd.audio = new PdAudioOpenAL();
		Pd.audio.create(new PdConfiguration());
		
		assets = new AssetManager();
		
		assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
		assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
		
		AssetDescriptor<PdPatch> patchAsset = new AssetDescriptor<PdPatch>("pdmidi/midiplayer.pd", PdPatch.class);
		
		assets.load(patchAsset);
		assets.finishLoading();
		
		Pd.audio.sendFloat("volume", 0.2f); // XXX
		Pd.audio.sendFloat("pan", 0); // XXX
		Pd.audio.sendFloat("reverb", 0); // XXX
		
		seq = new LiveSequencer(PdMidiSynth.instance);
		
		buildGUI(table, skin);
	}

	private Table matrix;
	SelectBox<Division> trigBox;
	
	private void buildGUI(Table table, Skin skin) 
	{
		final SelectBox<Division> lenBox = new SelectBox<>(skin);
		
		Table header = new Table(skin);
		
		final SelectBox<String> songBox = new SelectBox<>(skin);
		songBox.setItems("", "MuteCity.mid", "alf.mid", "pd-midi/macross.mid");
		header.add("Song");
		header.add(songBox);
		
		songBox.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String name = songBox.getSelected();
				seq.stop();
				seq.clear();
				if(!name.isEmpty()){
					AssetDescriptor<Music> songAsset = new AssetDescriptor<Music>(name, Music.class);
					
					assets.load(songAsset);
					assets.finishLoading();
					song = (DefaultMidiMusic)assets.get(songAsset);
					MidiFile midiFile = song.mfile;
	
					seq.load(midiFile);
					matrix.clear();
					buildMatrix(matrix, skin, lenBox.getSelected());
				}
				
			}});
		
		
		Label bpmField = new Label("", skin);
		
		header.add("BPM");
		header.add(bpmField).width(60);
		final Slider slider = new Slider(30, 240, .01f, false, skin);
		header.add(slider);
		
		slider.setValue(100);
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seq.bpm = slider.getValue();
				bpmField.setText(String.valueOf((int)seq.bpm));
			}
		});
		
		
		final TextButton btPlay = new TextButton("Play", skin, "toggle");
		btPlay.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btPlay.isChecked()) seq.play(); else seq.stop();
			}
		});
		header.add(btPlay);
		
		trigBox = new SelectBox<>(skin);
		trigBox.setItems(Division.all);
		header.add("trigger");
		header.add(trigBox);
		trigBox.setSelected(Division.quarterNote);
		
		lenBox.setItems(Division.all);
		header.add("size");
		header.add(lenBox);
		lenBox.setSelected(Division.wholeNote);
		
		lenBox.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				matrix.clear();
				buildMatrix(matrix, skin, lenBox.getSelected());
				
			}});
		
		matrix = new Table(skin);
		buildMatrix(matrix, skin, lenBox.getSelected());
		
		table.add(header).expandX().center();
		table.row();
		table.add(new ScrollPane(matrix, skin)).expand().center().top();
	}
	
	private void buildMatrix(Table table, Skin skin, final Division division)
	{
		table.defaults().fill();
		
		table.row();
		
		int nTracks = seq.getTracks().size;
		
		table.add("Track");
		for(int x=0 ; x<nTracks ; x++){
			table.add(String.valueOf(x+1)).padLeft(10).padRight(10);
		}
		table.row();
		
		final Array<Button> muteButtons = new Array<Button>();
		
		table.add("Name");
		for(int x=0 ; x<nTracks ; x++){
			String name = null;
			for(MidiEvent e : seq.getTracks().get(x).getEvents()){
				if(e instanceof TrackName){
					if(name != null){
						Gdx.app.error("Midi", "warning : multiple track names in same track");
						continue;
					}
					name = ((TrackName) e).getTrackName();
				}
			}
			table.add(name == null ? "-" : name);
		}
		table.row();
		
		final TextButton btLoopAll = new TextButton("Unloop All", skin);
		btLoopAll.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for(LiveTrack track : seq.getTracks()){
					track.unloop();
				}
				
			}});
		table.add(btLoopAll);
		for(int x=0 ; x<nTracks ; x++){
			final TextButton btClip = new TextButton("Unloop", skin);
			final int chan = x;
			btClip.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					seq.getTrack(chan).unloop();
				}
			});
			table.add(btClip);
		}
		table.row();
		
		final TextButton btMuteAll = new TextButton("Mute All", skin, "toggle");
		btMuteAll.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for(Button b : muteButtons){
					b.setChecked(btMuteAll.isChecked());
				}
				
			}});
		table.add(btMuteAll);
		for(int x=0 ; x<nTracks ; x++){
			final TextButton btClip = new TextButton("Mute " + (x+1), skin, "toggle");
			muteButtons.add(btClip);
			final int chan = x;
			btClip.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					seq.getTrack(chan).mute(!btClip.isChecked());
				}
			});
			table.add(btClip);
		}
		table.row();
		table.add().height(10).row();
		
		for(int y=0 ; ; y++){
			final int clip = y;
			boolean hasMore = false;
			
			final TextButton btPlayAll = new TextButton("Clip " + (y+1), skin);
			btPlayAll.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					int len = division.value;
					
					for(LiveTrack track : seq.getTracks()){
						track.setLoop(clip * len, (clip+1) * len, trigBox.getSelected().value);
					}
					
				}});
			table.add(btPlayAll);
			
			for(int x=0 ; x<nTracks ; x++){
				final int chan = x;
				
				if(seq.getTracks().get(chan).endBeat() < clip * division.value){
					table.add();
					continue;
				}
				hasMore = true;
				
				int len = division.value;
				int hasIn = 0;
				for(MidiEvent e : seq.getTracks().get(chan).getEvents()){
					long p = e.getTick() / seq.getTracks().get(chan).resolution;
					boolean in = p >= clip * len && p < (clip+1) * len;
					if(in){
						hasIn++;
					}
				}
				
				TextButton btClip = new TextButton(hasIn > 0 ? String.valueOf(hasIn) : "-", skin){
					public void act(float delta) {
						super.act(delta);
						
						int p = seq.getTrack(chan).getPosition();
						int len = division.value;
						boolean in = p >= clip * len && p < (clip+1) * len;
						
						if(seq.getTracks().get(chan).nextLoop){
							if(seq.getTracks().get(chan).nextLoopStart == clip * len &&
									seq.getTracks().get(chan).nextLoopEnd == (clip+1)*len	){
								setColor(Color.CYAN);
							}else{
								setColor(in ? Color.GOLD : Color.WHITE);
							}
						}else{
							setColor(in ? Color.GREEN : Color.WHITE);
						}
						
						
					};
				};
				btClip.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int len = division.value;
						seq.getTracks().get(chan).setLoop(clip * len, (clip+1) * len, trigBox.getSelected().value);
					}
				});
				table.add(btClip);
			}
			table.row();
			if(!hasMore) break;
		}
	}

	@Override
	public void render() {
		super.render();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		super.resize(width, height);
	}
}