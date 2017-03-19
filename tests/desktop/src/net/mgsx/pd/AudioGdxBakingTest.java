package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.pd.audio.PdAudioBakery;
import net.mgsx.pd.audio.PdAudioBakery.BakingListener;

public class AudioGdxBakingTest {

	public static void main(String[] args) 
	{
		if(args.length > 0 && "remote".equals(args[0])){
			PdConfiguration.remoteEnabled = true;
		}
		new LwjglApplication(new Game() {
			
			private PdAudioBakery bakery;
			private boolean backingComplete = false;
			
			@Override
			public void create() 
			{
				// we first load patch owning destination arrays.
				Pd.audio.create(new PdConfiguration());
				Pd.audio.open(Gdx.files.local("resources/runtime.pd"));
				
				// then we schedule baking of our complex patches.
				bakery = new PdAudioBakery();
				
				bakery.addTask(Gdx.files.local("resources/complex-sound.pd"), "baked-sound", 44100, 3);
				
				bakery.start(new BakingListener() {
					@Override
					public void progress(float percents) {
						Gdx.app.log("Baking", "backing progress : " + String.valueOf(percents));
					}
					
					@Override
					public void complete() {
						backingComplete = true;
					}
				});
				
				// finally we playback backed sound when user touch screen
				Gdx.input.setInputProcessor(new InputAdapter(){
					@Override
					public boolean touchDown(int screenX, int screenY, int pointer, int button) {
						if(backingComplete){
							Pd.audio.sendBang("play-backed");
						}
						return true;
					}
				});
				
			}
			@Override
			public void render() {
				if(backingComplete)
					Gdx.gl.glClearColor(0, 0, 0, 0);
				else
					Gdx.gl.glClearColor(1, 0, 0, 0);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, new LwjglApplicationConfiguration());
		
	}

}
