package net.mgsx.midi.playback;

import net.mgsx.midi.sequence.util.MidiEventListener;

public abstract class BaseSequencer
{
	protected final MidiEventListener listener;

	public BaseSequencer(MidiEventListener listener) {
		super();
		this.listener = listener;
	}

	
}
