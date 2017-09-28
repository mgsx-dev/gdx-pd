package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ByteArray;

import net.mgsx.pd.utils.PdAdapter;

/**
 * Demonstrate FFT rendering from OGG file (Desktop application only)
 * 
 * @author mgsx
 *
 */
public class ReactiveSongTest  {

	public static void main(String[] args) 
	{
		new LwjglApplication(new Game() {
			
			// configuration
			private final int blocksize = 64; // from 64 to 4096 max !

			// data
			private float [] inputL, inputR, outputL, outputR;
			
			// music
			private Music music;
			
			// rendering
			private ShapeRenderer renderer;
			private OrthographicCamera camera;
			private float [] vertices;
			private float threshold;
			private boolean playing;
			
			@Override
			public void create() {
				// graphics
				camera = new OrthographicCamera();
				renderer = new ShapeRenderer();
				vertices = new float[blocksize * 2]; // 2d vector
				
				// audio
				PdConfiguration config = new PdConfiguration();
				config.bufferCount = 9;
				Pd.audio.create(config);
				Pd.audio.open(Gdx.files.local("resources/song-analysis.pd"));
				
				outputL = new float[blocksize];
				outputR = new float[blocksize];
				
				// decode music data
				music = Gdx.audio.newMusic(Gdx.files.local("resources/8.12.mp3"));
				int sampleRate = 44100;
				
				Mp3.Music mp3 = ( Mp3.Music)music;
				ByteArray array = new ByteArray();
				byte [] buffer = new byte[1 << 16]; // implementation dependant should be large enough 
				for(;;){
					int count = mp3.read(buffer);
					if(count <= 0) break;
					array.ensureCapacity(count);
					array.addAll(buffer, 0, count);
				}
				
				// convert to float samples
				int frames = array.size / 4; // 16 bits stereo is 4 bytes per frames
				inputL = new float[frames];
				inputR = new float[frames];
				
				for(int i=0 ; i<frames ; i++){
					short sampleL = (short)((array.items[i * 4 + 0] & 0xFF) | (array.items[i * 4 + 1] & 0xFF) << 8);
					inputL[i] = (float)sampleL / (1 << 15);
					short sampleR = (short)((array.items[i * 4 + 2] & 0xFF) | (array.items[i * 4 + 3] & 0xFF) << 8);
					inputR[i] = (float)sampleR / (1 << 15);
				}
				
				Pd.audio.addListener("env", new PdAdapter(){
					@Override
					public void receiveFloat(String source, float x) {
						Gdx.app.log("env", "rms with bandbass at 300 Hz: " + x);
					}
					@Override
					public void receiveList(String source, Object... args) {
						threshold = Math.max((Float)args[0], (Float)args[1]);
					}
				});
				
				
				
				Pd.audio.sendList("music-config", frames, sampleRate);
				
				Pd.audio.sendFloat("music-pitch", 1f);
				
				Pd.audio.sendList("filter", 200, 5);
				
				Pd.audio.sendFloat("music-vol", 1f);
				
				Pd.audio.writeArray("music-l", 0, inputL, 0, inputL.length);
				Pd.audio.writeArray("music-r", 0, inputR, 0, inputR.length);
				
				Pd.audio.sendMessage("music", "play");
				playing = true;
			}
			
			
			private void processOutput()
			{
				// read FFT data
				Pd.audio.readArray(outputL, 0, "fft-l", 0, outputL.length);
				Pd.audio.readArray(outputR, 0, "fft-r", 0, outputR.length);
			}
			
			@Override
			public void render() {
				
				processOutput();

				if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
					if(playing){
						Pd.audio.sendMessage("music", "stop");
						playing = false;
					}else{
						Pd.audio.sendMessage("music", "play");
						playing = true;
					}
				}
				
				for(int i=0 ; i<blocksize ; i++){
					float x = 2f * (float)i/(float)blocksize;
					float y = (Math.abs(outputL[i]) + Math.abs(outputR[i])) * 10 ;
					
					vertices[i * 2 + 0] = x;
					vertices[i * 2 + 1] = y;
				}
				
				// draw vertices
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				renderer.setProjectionMatrix(camera.combined);
				renderer.begin(ShapeType.Line);
				renderer.setColor(threshold > .09f ? Color.RED : Color.BLUE);
				renderer.polyline(vertices);
				renderer.end();
			}
			
			@Override
			public void resize(int width, int height) {
				camera.setToOrtho(false, 1, 1);
			}

		}, new LwjglApplicationConfiguration());
		
	}

}
