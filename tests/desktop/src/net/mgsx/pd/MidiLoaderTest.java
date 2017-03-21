package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.MidiSequenceLoader;

public class MidiLoaderTest {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			
			AssetManager assets;
			MidiSequence sequence;
			
			@Override
			public void create() {
				
				assets = new AssetManager();
				
				assets.setLoader(MidiSequence.class, "mid", new MidiSequenceLoader(assets.getFileHandleResolver()));
				
				assets.load("resources/ppb64mgb.mid", MidiSequence.class);
			}
			
			@Override
			public void render() {
				if(assets.update() && sequence == null){
					sequence = assets.get("resources/ppb64mgb.mid", MidiSequence.class);
					Gdx.app.log("Pd", "sequence loaded.");
				}
			}
			
		}, config);
		
	}

}
