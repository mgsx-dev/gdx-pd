package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class AudioGdxPatchTest {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				
				// just play a patch
				Pd.audio.create(new PdConfiguration());
				Pd.audio.open(Gdx.files.local("test-resources/test.pd"));
				
			}
			
		}, config);
	}
}
