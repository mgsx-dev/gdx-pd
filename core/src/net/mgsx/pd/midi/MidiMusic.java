package net.mgsx.pd.midi;

import com.badlogic.gdx.audio.Music;

public interface MidiMusic extends Music
{
	public void setBPM(float bpm);
	public float getBPM();
	
	/**
	 * @return sequence duration in seconds.
	 */
	public float getDuration();
}
