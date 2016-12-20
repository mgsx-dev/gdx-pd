package net.mgsx.pd;

import java.io.File;
import java.io.IOException;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import android.content.Context;
import net.mgsx.pd.audio.PdAudioDefault;
import net.mgsx.pd.patch.PdPatch;

public class PdAudioAndroid extends PdAudioDefault
{
	private Context context;
	
	public PdAudioAndroid(Context context) {
		super();
		this.context = context;
		
		FileHelper.trimCache(context);
	}

	@Override
	public PdPatch open(FileHandle file) 
	{
		File cachePatchFile = new File(context.getCacheDir(), file.path());
		if(!cachePatchFile.exists()){
	        String patchFolder = new File(file.path()).getParent();
	        try {
		        if(patchFolder == null){
		        	//FileHelper.copyAssetFolder(context.getAssets(), "", context.getCacheDir().getAbsolutePath());
		        	throw new GdxRuntimeException("can't copy patch from root directory");
		        }else{
					File cachePatchFolder = new File(context.getCacheDir(), patchFolder);
			        	FileHelper.copyAssetFolder(context.getAssets(), patchFolder, 
								cachePatchFolder.getAbsolutePath());
		        }
	        } catch (IOException e) {
	        	throw new GdxRuntimeException("unable to copy patch", e);
	        }
		}
        try {
			int handle = PdBase.openPatch(cachePatchFile);
			return new PdPatch(handle);
		} catch (IOException e) {
			throw new GdxRuntimeException("unable to open patch", e);
		}
	}
}
