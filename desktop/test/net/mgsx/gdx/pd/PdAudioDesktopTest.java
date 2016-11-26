package net.mgsx.gdx.pd;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class PdAudioDesktopTest {

	public static void main(String[] args) throws IOException 
	{
		FileHandle file = new FileHandle(new File("test-resources/test.pd"));
		
		Pd.audio = new PdAudioDesktop();
		
		Pd.audio.create(new PdConfiguration());
		
		Pd.audio.open(file);
		
		System.out.println("Press key to stop");
		System.in.read();
		
		Pd.audio.release();

	}

}
