package net.mgsx.pd.patch;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.pd.Pd;

public class PatchLoader extends AsynchronousAssetLoader<PdPatch, AssetLoaderParameters<PdPatch>>
{
	private PdPatch patch;
	
	public PatchLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<PdPatch> parameter) {
		patch = Pd.audio.open(file);
	}

	@Override
	public PdPatch loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<PdPatch> parameter) {
		return patch;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<PdPatch> parameter) {
		// no deps
		return null;
	}

}
