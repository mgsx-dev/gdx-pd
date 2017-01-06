package net.mgsx.pd.midi;

import com.leff.midi.util.MidiEventListener;

public abstract class BaseSequencer
{
	protected final MidiEventListener listener;

	public BaseSequencer(MidiEventListener listener) {
		super();
		this.listener = listener;
	}

	
}
