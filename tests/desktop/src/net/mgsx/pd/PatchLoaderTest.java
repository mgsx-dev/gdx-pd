package net.mgsx.pd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

public class PatchLoaderTest {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){
			
			AssetManager assets;
			PdPatch patch;
			
			@Override
			public void create() {
				
				Pd.audio.create(new PdConfiguration());

				assets = new AssetManager();
				
				assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
				
				assets.load("resources/test.pd", PdPatch.class);
			}
			
			@Override
			public void render() {
				if(assets.update() && patch == null){
					patch = assets.get("resources/test.pd", PdPatch.class);
					Gdx.app.log("Pd", "patch loaded.");
				}
			}
			
		}, config);
		
	}

}
