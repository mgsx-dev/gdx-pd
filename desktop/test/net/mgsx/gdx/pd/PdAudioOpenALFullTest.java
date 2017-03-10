package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdAudioOpenAL;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.events.PdAdapter;

public class PdAudioOpenALFullTest {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 16;
		
		new LwjglApplication(new Game(){
			
			private Sound sound;
			private Music music;
			
			@Override
			public void create() {
				
				Pd.audio = new PdAudioOpenAL();
				PdConfiguration config = new PdConfiguration();
				config.inputChannels = 2;
				Pd.audio.create(config);
				Pd.audio.open(Gdx.files.local("test-resources/test.pd"));
				
				Pd.audio.addListener("event-test", new PdAdapter(){
					@Override
					public void receiveFloat(String source, float x) {
						System.out.println(x);
					}
				});
				Pd.audio.addListener("test-in-left", new PdAdapter(){
				});
				Pd.audio.addListener("test-in-right", new PdAdapter(){
				});
				
				sound = Gdx.audio.newSound(Gdx.files.local("test-resources/shotgun.wav"));
				music = Gdx.audio.newMusic(Gdx.files.local("test-resources/cloudconnected.ogg"));
			}
			
			@Override
			public void render() {
				super.render();
				
				if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
					if(music.isPlaying()) music.pause();
					else music.play();
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
					sound.play();
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
					Pd.audio.sendBang("test-bang");
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
					Pd.audio.sendFloat("test-float", 4.35f);
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
					Pd.audio.sendList("test-list", 4.35f, 8, "foo");
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
					Pd.audio.sendMessage("test-message", "bar", 4.35f, 8, "foo");
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
					Pd.audio.sendSymbol("test-symbol", "John Doe");
				}
				
				
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
					Pd.audio.release();
					Gdx.app.exit();
				}
			}
			
		
		}, config);
	}
}
