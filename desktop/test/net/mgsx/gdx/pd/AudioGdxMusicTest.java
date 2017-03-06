package net.mgsx.gdx.pd;

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
				
				// TODO include something is data
				Music music = Gdx.audio.newMusic(Gdx.files.absolute("/home/germain/git/puredatapatches/talking.wav"));
				
				music.play();
				
			}}, config);
		
	}

}
