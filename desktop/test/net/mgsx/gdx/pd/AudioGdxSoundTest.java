package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class AudioGdxSoundTest {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				
				final Sound snd = Gdx.audio.newSound(Gdx.files.local("test-resources/shotgun.wav"));
				
				snd.play();
				
				Gdx.input.setInputProcessor(new InputAdapter(){
					@Override
					public boolean touchDown(int screenX, int screenY, int pointer, int button) {
						snd.play();
						return true;
					}
				});
				
			}}, config);
		
	}

}
