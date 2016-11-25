package net.mgsx.pd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Pd 
{
	public static PdAudio audio;

	public static PdAudio none() 
	{
		return new PdAudio(){
			@Override
			public void create() {
				Gdx.app.debug("audio", "audio create disabled");
			}
			@Override
			public void release() {
				Gdx.app.debug("audio", "audio release disabled");				
			}
			@Override
			public PdPatch open(FileHandle file) {
				Gdx.app.debug("audio", "audio open disabled");
				return new PdPatch();
			}
		};
	}
}
