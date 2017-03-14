package net.mgsx.gdx.pd;

import org.puredata.core.PdBase;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudio;
import net.mgsx.pd.audio.PdAudioBase;
import net.mgsx.pd.audio.PdAudioThread;
import net.mgsx.pd.patch.PdPatch;

public class AudioGdxBakingTest {

	private static volatile boolean baked = false;
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 17;
		new LwjglApplication(new Game(){
			@Override
			public void create() 
			{
				// first we want to bake some sounds
				final PdAudio audio = Pd.audio;
				Pd.audio = new PdAudioBase() {
					@Override
					protected PdAudioThread createThread(PdConfiguration config) {
						return new PdAudioThread(config){
							@Override
							public void run() {
								// bake !
								PdPatch complexPatch = Pd.audio.open(Gdx.files.local("test-resources/complex-sound.pd"));
								PdBase.openAudio(0, 1, 44100);
								PdBase.computeAudio(true);
								
								float time = 3;
								int frames = (int)(time * 44100);
								int samples = frames;
								float[] data = new float[samples];
								int ticks = samples / PdBase.blockSize();
								int perr = PdBase.process(ticks, new float[]{}, data);
								if(perr != 0) Gdx.app.error("Pd", "process error ....");
								complexPatch.dispose();
								PdBase.release();
								
								Pd.audio = audio;
								Pd.audio.create(new PdConfiguration());
								Pd.audio.open(Gdx.files.local("test-resources/runtime.pd"));
								int err = PdBase.writeArray("baked-sound", 0, data, 0, data.length);
								if(err != 0) Gdx.app.error("Pd", "write array error ....");
								baked = true;
							}
						};
					}
				};
				Pd.audio.create(new PdConfiguration());
				
				Gdx.input.setInputProcessor(new InputAdapter(){
					@Override
					public boolean touchDown(int screenX, int screenY, int pointer, int button) {
						if(baked){
							Pd.audio.sendBang("play-backed");
						}
						return true;
					}
				});
				
			}
			@Override
			public void render() {
				if(baked)
					Gdx.gl.glClearColor(0, 0, 0, 0);
				else
					Gdx.gl.glClearColor(1, 0, 0, 0);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, config);
		
	}

}
