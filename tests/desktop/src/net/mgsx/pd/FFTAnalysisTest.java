package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.audio.Ogg;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.pd.utils.PdAdapter;

/**
 * Demonstrate FFT rendering from OGG file (Desktop application only)
 * 
 * @author mgsx
 *
 */
public class FFTAnalysisTest  {

	public static void main(String[] args) 
	{
		new LwjglApplication(new Game() {
			
			// configuration
			private final int blocksize = 64 * 4; // from 64 to 4096 max !

			// data
			private float [] input, output;
			
			// music
			private byte[] musicBuffer;
			private Music music;
			
			// rendering
			private ShapeRenderer renderer;
			private OrthographicCamera camera;
			private float [] vertices;
			
			@Override
			public void create() {
				// graphics
				camera = new OrthographicCamera();
				renderer = new ShapeRenderer();
				vertices = new float[blocksize * 2 * 2]; // 2d vector for input and output curves
				
				// audio
				Pd.audio.create(new PdConfiguration());
				Pd.audio.open(Gdx.files.local("resources/fft.pd"));
				
				input = new float[blocksize];
				output = new float[blocksize];
				
				Pd.audio.sendFloat("fft-size", blocksize);
				Pd.audio.addListener("fft-completed", new PdAdapter(){
					@Override
					public void receiveBang(String source) {
						processOutput();
						processInput(); // rescheduling next FFT
					}
				});
				
				// music
				music = Gdx.audio.newMusic(Gdx.files.local("resources/cloudconnected.ogg"));
				musicBuffer = new byte[blocksize * 2 * 2]; // stereo 16 bits
				
				// launch process loop
				processInput();
			}
			
			private void processInput()
			{
				// read music
				if(music instanceof Ogg.Music){
					Ogg.Music ogg = (Ogg.Music)music;
					ogg.read(musicBuffer);
					for(int i=0 ; i<input.length ; i++){
						short sample = (short)((musicBuffer[i * 4 + 0] & 0xFF) | (musicBuffer[i * 4 + 1] & 0xFF) << 8);
						input[i] = (float)sample / (1 << 15);// XXX mono only
					}
				}
				
				// schedule new FFT
				Pd.audio.writeArray("fft-in", 0, input, 0, input.length);
				Pd.audio.sendFloat("fft-start", blocksize);
			}
			
			private void processOutput()
			{
				// read FFT data
				Pd.audio.readArray(output, 0, "fft-out-r", 0, blocksize/2);
				Pd.audio.readArray(output, blocksize/2, "fft-out-i", 0, blocksize/2);
			}
			
			@Override
			public void render() {
				
				// update vertices
				for(int i=0 ; i<input.length ; i++){
					float x = (float)i/(float)(blocksize * 2);
					float y = (input[i] + 1) / 2;
					vertices[i * 2 + 0] = x;
					vertices[i * 2 + 1] = y;
				}
				for(int i=0 ; i<output.length ; i++){
					float x = .5f + (float)i/(float)(blocksize * 2);;
					float y = Math.abs(output[i]);
					
					float f = .05f;
					y = vertices[(input.length + i) * 2 + 1] * (1 - f) + y * f;
					
					vertices[(input.length + i) * 2 + 0] = x;
					vertices[(input.length + i) * 2 + 1] = y;
				}
				
				// draw vertices
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				renderer.setProjectionMatrix(camera.combined);
				renderer.begin(ShapeType.Line);
				renderer.setColor(Color.BLUE);
				renderer.polyline(vertices, 0, vertices.length/2);
				renderer.setColor(Color.RED);
				renderer.polyline(vertices, vertices.length/2, vertices.length/4);
				renderer.setColor(Color.GREEN);
				renderer.polyline(vertices, vertices.length/2+vertices.length/4, vertices.length/4);
				renderer.end();
			}
			
			@Override
			public void resize(int width, int height) {
				camera.setToOrtho(false, 1, 1);
			}

		}, new LwjglApplicationConfiguration());
		
	}

}
