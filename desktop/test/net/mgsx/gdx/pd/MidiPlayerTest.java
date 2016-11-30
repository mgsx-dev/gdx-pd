package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.midi.MidiMusic;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.midi.PdMidiMusic;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

public class MidiPlayerTest {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.audioDeviceBufferSize = 512;
		config.audioDeviceBufferCount = 16;
		
		new LwjglApplication(new Game(){
			
			ShapeRenderer shape;
			
			OrthographicCamera camera;

			private PdMidiMusic song, song1, song2, songA, songB;
			private float crossfade;
			
			@Override
			public void create() {
				
				shape = new ShapeRenderer();
				camera = new OrthographicCamera();
				
				Pd.audio = new PdAudioOpenAL();
				Pd.audio.create(new PdConfiguration());
				
				AssetManager assets = new AssetManager();
				
				assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
				assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
				
				AssetDescriptor<PdPatch> patchAsset = new AssetDescriptor<PdPatch>("pd/master.pd", PdPatch.class);
				AssetDescriptor<Music> song1Asset = new AssetDescriptor<Music>("pd/alf.mid", Music.class);
				AssetDescriptor<Music> song2Asset = new AssetDescriptor<Music>("pd/macross.mid", Music.class);
				
				assets.load(patchAsset);
				assets.load(song1Asset);
				assets.load(song2Asset);
				assets.finishLoading();
				
				song1 = (PdMidiMusic)assets.get(song1Asset);
				song2 = (PdMidiMusic)assets.get(song2Asset);
				
				Pd.audio.sendFloat("volume", 0.2f); // XXX
				
				song = song1;
				
				song.play();
				
				song1.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(Music music) {
						Gdx.app.log("Test", "midi complete");
						song = song2;
						song.play();
					}
				});
				
				song2.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(Music music) {
						Gdx.app.log("Test", "midi complete");
						song = song1;
						song.play();
					}
				});
				
				Gdx.input.setInputProcessor(new InputAdapter(){
					@Override
					public boolean touchDragged(int screenX, int screenY, int pointer) {
						MidiMusic mm = (MidiMusic)song; // XXX cast !
						float duration = mm.getDuration();
						float tx = (float)screenX / (float)Gdx.graphics.getWidth();
						float ty = 1 - (float)screenY / (float)Gdx.graphics.getHeight();
						if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
							if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
								mm.setLoopStart(tx * duration);
							}else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)){
								mm.setLoopEnd(tx * duration);
							}else{
								song.setPosition(tx * duration);
							}
						}
						else if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
							if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
								((PdMidiMusic)mm).velocityScale = ty; // XXX cast
							}else{
								song.setPan(2 * tx - 1, ty);
							}
						}
						return super.touchDragged(screenX, screenY, pointer);
					}
				});
			}
			
			@Override
			public void render() {
				super.render();
				
				MidiMusic mm = (MidiMusic)song;
				
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
					Pd.audio.release();
					Gdx.app.exit();
				}else if(Gdx.input.isKeyJustPressed(Input.Keys.F1)){
					if(song1.isPlaying()){
						song1.pause();
					}else{
						song1.play();
					}
				}else if(Gdx.input.isKeyJustPressed(Input.Keys.F2)){
					if(song2.isPlaying()){
						song2.pause();
					}else{
						song2.play();
					}
				}else if(Gdx.input.isKeyJustPressed(Input.Keys.F3)){
					songA = song == song1 ? song1 : song2;
					songB = song == song1 ? song2 : song1;
					crossfade = 0;
					songB.play();
				}else if(Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
					song.setLooping(!song.isLooping());
				}
				for(int i=0 ; i<16 ; i++){
					// XXX Doesnt work (maybe not implemented on some platforms)
					if(Gdx.input.isKeyJustPressed(Input.Keys.A + i)){
						if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
							mm.getSequencer().setTrackSolo(i, !mm.getSequencer().getTrackSolo(i));
						else
							mm.getSequencer().setTrackMute(i, !mm.getSequencer().getTrackMute(i));
					}
				}
				
				if(songA != null && songB != null){
					crossfade += Gdx.graphics.getDeltaTime() / 1; // XXX 3 sec transition
					if(crossfade > 1){
						song.pause();
						song = songB;
						song.velocityScale = (1);
						songA = songB = null;
					}
					else{
						songA.velocityScale = (1 - crossfade);
						songB.velocityScale = (crossfade);
					}
				}
				
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
				
				
				float x = mm.getPosition() / mm.getDuration() * 2 - 1;
				float xl = mm.getLoopStart() / mm.getDuration() * 2 - 1;
				float xr = mm.getLoopEnd() / mm.getDuration() * 2 - 1;
				
				shape.setProjectionMatrix(camera.combined);
				shape.begin(ShapeType.Line);
				shape.setColor(Color.WHITE);
				shape.line(x, -1, x, 1);
				
				shape.setColor(Color.GREEN);
				shape.line(xl, -1, xl, 1);
				
				shape.setColor(Color.RED);
				shape.line(xr, -1, xr, 1);
				
				shape.end();
				
			}
			
		
		}, config);
	}
}
