package net.mgsx.pd.midi;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class MidiMusicLoader extends AsynchronousAssetLoader<Music, AssetLoaderParameters<Music>>
{
	private MidiMusic music;
	
	public MidiMusicLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<Music> parameter) {
		music = new PdMidiMusic();
		music.setSequence(file);
	}

	@Override
	public Music loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<Music> parameter) {
		return music;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<Music> parameter) {
		// no dependencies
		return null;
	}

}
