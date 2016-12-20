package net.mgsx.pd;

import com.badlogic.gdx.Gdx;

import net.mgsx.pd.audio.PdAudio;
import net.mgsx.pd.audio.PdAudioNone;
import net.mgsx.pd.midi.PdMidi;

/**
 * Environment class holding references to the {@link PdAudio} instance. 
 * The references are held in public static fields which allows static access to all sub systems. 
 * Do not use audio in a thread that is not the rendering thread.
 * <p>
 * Implementation must be set in your launcher prior to use it.
 * Known implementations :
 * PdAudioDesktop, PdAudioRemote (throw UDP/OSC) (PdAudioAndroid, PdAudioIOS and PdAudioWeb come soon)
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
	
	/**
	 * None implementation : does not call any audio layers. Usefull for debug purpose.
	 */
	public static final PdAudio none = new PdAudioNone();
}
