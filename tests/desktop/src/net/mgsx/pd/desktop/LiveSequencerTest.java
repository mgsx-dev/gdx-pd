package net.mgsx.pd.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leff.midi.MidiFile;

import net.mgsx.gdx.pd.PdAudioOpenAL;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.midi.DefaultMidiMusic;
import net.mgsx.pd.midi.DefaultPdMidi;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

public class LiveSequencerTest {

	public static class Division{
		public static Array<Division> all(){
			 Array<Division> a = new Array<Division>();
			 a.addAll(new Division("Quarter", 1), 
					 new Division("Half", 2), 
					 new Division("Full", 4), 
					 new Division("1/2x", 8), 
					 new Division("Bar", 16), 
					 new Division("2x", 32), 
					 new Division("4x", 64));
			 return a;
		}
		public String name;
		public int value;
		public Division(String name, int value) {
			super();
			this.name = name;
			this.value = value;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 16;
		
		new LwjglApplication(new Game(){
			
			Stage stage;
			
			private DefaultMidiMusic song;
			
			private LiveSequencer seq;
			
			@Override
			public void create() {
				
				stage = new Stage(new ScreenViewport());
				Gdx.input.setInputProcessor(stage);
				Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
				
				Table table = new Table(skin);
				table.setFillParent(true);
				stage.addActor(table);
				
				buildGUI(table, skin);
				
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
				
				
				
				seq.play();
				
//				song.play();
//				
//				song.setOnCompletionListener(new OnCompletionListener() {
//					
//					@Override
//					public void onCompletion(Music music) {
//						Gdx.app.log("Test", "midi complete");
//					}
//				});
				
			}
			
			
			
			private void buildGUI(Table table, Skin skin) 
			{
				final SelectBox<Division> trigBox = new SelectBox<>(skin);
				trigBox.setItems(Division.all());
				table.add("trigger");
				table.add(trigBox);
				trigBox.setSelectedIndex(0);
				
				final SelectBox<Division> lenBox = new SelectBox<>(skin);
				lenBox.setItems(Division.all());
				table.add("size");
				table.add(lenBox);
				lenBox.setSelectedIndex(4);

				table.row();
				
				int NCLIP = 20;
				for(int x=0 ; x<16 ; x++){
					table.add("Chan " + (x+1));
				}
				table.row();
				
				for(int x=0 ; x<16 ; x++){
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
				
				
				for(int x=0 ; x<16 ; x++){
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
					for(int x=0 ; x<16 ; x++){
						final int chan = x;
						TextButton btClip = new TextButton("Clip " + (x+1) + "-" + (y+1), skin){
							public void act(float delta) {
								super.act(delta);
								
								int p = seq.getPosition(chan);
								int len = lenBox.getSelected().value;
								boolean in = p >= clip * len && p < (clip+1) * len;
								setColor(in ? Color.GOLD : Color.WHITE);
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
			
		
		}, config);
	}
}
