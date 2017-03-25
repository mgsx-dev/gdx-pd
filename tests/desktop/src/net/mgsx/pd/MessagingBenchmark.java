package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;

public class MessagingBenchmark {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			
			private int count;
			private float elapsed;
			private FPSLogger fps;
			
			@Override
			public void create() {
				
				Pd.audio.create(new PdConfiguration());
				Pd.audio.open(Gdx.files.local("resources/messaging.pd"));
				
				fps = new FPSLogger();
			}
			
			@Override
			public void render() {
				super.render();
				
				// sendFloat drops at 128k call per frames, with stable GC
				// sendList(1 x float) drops at 86k call per frames, with chaotic GC
				// sendList(10 x float) drops at 60k call per frames, with chaotic GC
				
				// chaotic GC maybe because of primitive to Object converion
				
				elapsed += Gdx.graphics.getDeltaTime();
				if(elapsed > count + 1){
					count = (int)elapsed;
					Gdx.app.log("Perf", String.valueOf(count) + "k");
				}
				for(int i=0 ; i<count * 1000; i++){
					
					Pd.audio.sendFloat("value", 1.f);
					//Pd.audio.sendList("value", 1.f);
					//Pd.audio.sendList("value", 1.f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f);
				}
				
				fps.log();
			}
			
		}, config);
	}
}
