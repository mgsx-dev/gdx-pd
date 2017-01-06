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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

public class LiveSequencerDemoApplication extends Game {
		Stage stage;
		private DefaultMidiMusic song;
		private LiveSequencer seq;

		@Override
		public void create() {
			
			stage = new Stage(new ScreenViewport());
			Gdx.input.setInputProcessor(stage);
			Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
			
			Table table = new Table(skin);
			
			ScrollPane pane = new ScrollPane(table, skin);
			
			pane.setFillParent(true);
			
			stage.addActor(pane);
			
			Pd.midi = new DefaultPdMidi();
			
			Pd.audio = new PdAudioOpenAL();
			Pd.audio.create(new PdConfiguration());
			
			AssetManager assets = new AssetManager();
			
			assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
			assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
			
			AssetDescriptor<PdPatch> patchAsset = new AssetDescriptor<PdPatch>("pdmidi/midiplayer.pd", PdPatch.class);
			AssetDescriptor<Music> songAsset = new AssetDescriptor<Music>("MuteCity.mid", Music.class);
			
			assets.load(patchAsset);
			assets.load(songAsset);
			assets.finishLoading();
			
			song = (DefaultMidiMusic)assets.get(songAsset);
			MidiFile midiFile = song.mfile;
			
			Pd.audio.sendFloat("volume", 0.2f); // XXX
			Pd.audio.sendFloat("pan", 0); // XXX
			Pd.audio.sendFloat("reverb", 0); // XXX
			
			seq = new LiveSequencer();
			seq.load(midiFile);
			
			buildGUI(table, skin);
			
		}

		private void buildGUI(Table table, Skin skin) 
		{
			table.defaults().fill();
			
			final TextButton btPlay = new TextButton("Play", skin, "toggle");
			btPlay.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if(btPlay.isChecked()) seq.play(); else seq.stop();
				}
			});
			table.add(btPlay);
			
			final SelectBox<Division> trigBox = new SelectBox<>(skin);
			trigBox.setItems(Division.all());
			table.add("trigger");
			table.add(trigBox);
			trigBox.setSelectedIndex(0);
			
			final SelectBox<Division> lenBox = new SelectBox<>(skin);
			lenBox.setItems(Division.all());
			table.add("size");
			table.add(lenBox);
			lenBox.setSelectedIndex(3);

			table.row();
			
			int nTracks = seq.getTracks().size;
			
			int NCLIP = 50;
			
			table.add("Omni");
			for(int x=0 ; x<nTracks ; x++){
				table.add("Track " + (x+1)).padLeft(10).padRight(10);
			}
			table.row();
			
			table.add("Omni");
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
				table.add(name == null ? "???" : name);
			}
			table.row();
			
			final TextButton btLoopAll = new TextButton("Loop All", skin, "toggle");
			btLoopAll.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					for(LiveTrack track : seq.getTracks()){
						track.loop(btLoopAll.isChecked());
					}
					
				}});
			table.add(btLoopAll);
			for(int x=0 ; x<nTracks ; x++){
				final TextButton btClip = new TextButton("Loop " + (x+1), skin, "toggle");
				final int chan = x;
				btClip.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						seq.loop(chan, !btClip.isChecked());
					}
				});
				table.add(btClip);
			}
			table.row();
			
			final TextButton btMuteAll = new TextButton("Mute All", skin, "toggle");
			btMuteAll.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					for(LiveTrack track : seq.getTracks()){
						track.mute(!btMuteAll.isChecked());
					}
					
				}});
			table.add(btMuteAll);
			for(int x=0 ; x<nTracks ; x++){
				final TextButton btClip = new TextButton("Mute " + (x+1), skin, "toggle");
				final int chan = x;
				btClip.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						seq.mute(chan, !btClip.isChecked());
					}
				});
				table.add(btClip);
			}
			table.row();
			for(int y=0 ; y<NCLIP ; y++){
				final int clip = y;
				boolean hasMore = false;
				
				final TextButton btPlayAll = new TextButton("Play All", skin);
				btPlayAll.addListener(new ChangeListener(){

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int len = lenBox.getSelected().value;
						
						for(LiveTrack track : seq.getTracks()){
							track.setLoop(clip * len, (clip+1) * len, trigBox.getSelected().value);
						}
						
					}});
				table.add(btPlayAll);
				
				for(int x=0 ; x<nTracks ; x++){
					final int chan = x;
					
					if(seq.getTracks().get(chan).endBeat() < clip * 8){ // XXX BAR
						table.add();
						continue;
					}
					hasMore = true;
					TextButton btClip = new TextButton("Clip " + (x+1) + "-" + (y+1), skin){
						public void act(float delta) {
							super.act(delta);
							
							int p = seq.getPosition(chan);
							int len = lenBox.getSelected().value;
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
							int len = lenBox.getSelected().value;
							seq.sched(chan, clip * len, (clip+1) * len, trigBox.getSelected().value);
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