package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;

public class OpenALPdTest {

	
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		OpenALPd.init(config);
		
		// fillBuffer sleep is rounded to 0 so doesn't wait ...
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				
				((OpenALAudio)Gdx.audio).registerMusic("pd", OpenALPd.class);
				
				
				Music music = Gdx.audio.newMusic(Gdx.files.local("test-resources/test.pd"));
				
				System.out.println(music);
				
				music.play();
				
			}}, config);
		
	}

}
