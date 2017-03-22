package net.mgsx.pd;

import org.puredata.core.PdBaseLoader;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.mgsx.pd.audio.PdAudio;
import net.mgsx.pd.audio.PdAudioNone;
import net.mgsx.pd.audio.PdAudioRemote;
import net.mgsx.pd.midi.DefaultPdMidi;
import net.mgsx.pd.midi.PdMidi;
import net.mgsx.pd.midi.PdMidiNone;
import net.mgsx.pd.midi.PdMidiRemote;

/**
 * Environment class holding references to the {@link PdAudio} instance. 
 * The references are held in public static fields which allows static access to all sub systems. 
 * Do not use audio in a thread that is not the rendering thread.
 * <p>
 * Implementation is automatically injected at runtime depending on running platform.
 * {@link Pd} require Gdx context, this class should'nt be acceessed in launchers.
 * You can pre-configure it with {@link PdConfiguration} static fields in launchers in order to disable all or
 * enable remote mode.
 * <p>
 * It is designed as #{@link Gdx}.
 * 
 * @author mgsx
 *
 */
public class Pd 
{
	public static PdAudio audio;

	public static PdMidi midi;
	
	static{
		try {
			initialize();
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	private static void initialize() throws ReflectionException 
	{
		// configure native loader.
		PdBaseLoader.loaderHandler = new PdBaseLoader() {
			@Override
			public void load() {
				SharedLibraryLoader loader = new SharedLibraryLoader();
				if(SharedLibraryLoader.isWindows){
					loader.load("pthread");
				}
				loader.load("gdx-pd");
			}
		};
		
		// configure audio and midi implementation.
		if(PdConfiguration.disabled)
		{
			audio = new PdAudioNone();
			midi = new PdMidiNone();
		}
		else if(PdConfiguration.remoteEnabled)
		{
			audio = new PdAudioRemote(
					PdConfiguration.remoteHost, 
					PdConfiguration.remoteSendPort,
					PdConfiguration.remoteRecvPort);
			midi = new PdMidiRemote(
					PdConfiguration.remoteHost,
					PdConfiguration.remoteMidiPort);
		}
		else
		{
			ApplicationType type = Gdx.app.getType();
			if(type == ApplicationType.Desktop)
			{
				String className = "net.mgsx.pd.PdAudioOpenAL";
				Class<? extends PdAudio> cls = ClassReflection.forName(className);
				audio = ClassReflection.newInstance(cls);
			}
			else if(type == ApplicationType.Android)
			{
				String className = "net.mgsx.pd.PdAudioAndroid";
				Class<? extends PdAudio> cls = ClassReflection.forName(className);
				audio = ClassReflection.newInstance(cls);
			}
			else
			{
				audio = new PdAudioNone();
				Gdx.app.error("Pd", "No Pd implementation available for: " + Gdx.app.getType());
			}
			midi = new DefaultPdMidi();
		}
	}
}
