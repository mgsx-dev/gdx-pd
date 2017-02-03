package net.mgsx.midi.sequence;

import java.io.IOException;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MidiSequenceLoader extends AsynchronousAssetLoader<MidiSequence, AssetLoaderParameters<MidiSequence>>
{
	private MidiSequence sequence;
	
	public MidiSequenceLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<MidiSequence> parameter) {
		try {
			sequence = new MidiSequence(file.read());
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public MidiSequence loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<MidiSequence> parameter) {
		MidiSequence sequence = this.sequence;
		this.sequence = null;
		return sequence;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<MidiSequence> parameter) {
		// no dependencies
		return null;
	}

}
