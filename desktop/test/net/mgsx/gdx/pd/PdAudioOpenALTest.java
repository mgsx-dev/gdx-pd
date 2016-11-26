package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.Pd;

public class PdAudioOpenALTest {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 16;
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				
				Pd.audio = new PdAudioOpenAL();
				Pd.audio.create();
				Pd.audio.open(Gdx.files.local("test-resources/test.pd"));
				
			}
			
			@Override
			public void render() {
				super.render();
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
					Pd.audio.release();
					Gdx.app.exit();
				}
			}
			
		
		}, config);
	}
}
