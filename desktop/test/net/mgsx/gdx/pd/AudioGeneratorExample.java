package net.mgsx.gdx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;

public class AudioGeneratorExample {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			@Override
			public void create() {
				int channels = 2;
				
				int sampleRate = 44100;
				AudioDevice device = Gdx.audio.newAudioDevice(sampleRate, channels < 2);
				
				// simple sinus
				float duration = 4.f;
				float pitch = 440;
				
				int samples = (int)(duration * sampleRate) * channels;
				float [] data = new float[samples];
				int stride = samples/channels;
				for(int i=0 ; i<stride ; i+=1){
					float s = (float)i/(float)stride;
					float t = s * duration * pitch;
					float value = MathUtils.sin(MathUtils.PI2 * t);
					for(int j=0 ; j<channels ; j++)
						data[i+j*stride] = value;
				}
				device.writeSamples(data, 0, data.length);
				
				device.dispose();
				
				Gdx.app.exit();
			}}, config);
		
	}

}
