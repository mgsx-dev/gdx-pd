package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.midi.MidiMusicLoader;

public class MidiMusicLoaderTest {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			
			AssetManager assets;
			Music midiMusic;
			
			@Override
			public void create() {
				
				Pd.audio.create(new PdConfiguration());
				
				Pd.audio.open(Gdx.files.internal("resources/midi-logger.pd"));
				
				assets = new AssetManager();
				
				assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
				
				assets.load("resources/ppb64mgb.mid", Music.class);
			}
			
			@Override
			public void render() {
				if(assets.update() && midiMusic == null){
					midiMusic = assets.get("resources/ppb64mgb.mid", Music.class);
					Gdx.app.log("Pd", "midi music loaded.");
					
					midiMusic.play();
				}
			}
			
		}, config);
		
	}

}
