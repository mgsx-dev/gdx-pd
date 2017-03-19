package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class AudioGdxMusicTest {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				
				// play a music
				Music music = Gdx.audio.newMusic(Gdx.files.classpath("cloudconnected.ogg"));
				music.setVolume(0.3f);
				music.play();
				
				// and a pd patch at the same time
				Pd.audio.create(new PdConfiguration());
				Pd.audio.open(Gdx.files.local("resources/test.pd"));
				
			}}, config);
		
	}

}
