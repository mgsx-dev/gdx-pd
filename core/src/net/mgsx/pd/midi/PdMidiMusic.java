package net.mgsx.pd.midi;

import javax.sound.midi.MidiUnavailableException;

import org.puredata.core.PdBase;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class PdMidiMusic extends MidiMusic
{
	/**
	 * Note On velocity are scaled by this factor.
	 * A value of 1 disable scaling (saving calculations)
	 */
	public float velocityScale = 1.f;
	
	public PdMidiMusic() {
		super(false);
		try {
			getSequencer().getTransmitter().setReceiver(new PdMidiReceiver(this));
		} catch (MidiUnavailableException e) {
			throw new GdxRuntimeException("can't get a Java Midi Sequencer", e);
		}
	}
	
	@Override
	public void setVolume(float volume) {
		super.setVolume(volume);
		PdBase.sendFloat("volume", volume);
	}
	
	@Override
	public void setPan(float pan, float volume) {
		super.setPan(pan, volume);
		PdBase.sendFloat("pan", pan);
		PdBase.sendFloat("volume", volume);
	}

}
