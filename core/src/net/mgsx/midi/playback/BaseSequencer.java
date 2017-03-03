package net.mgsx.midi.playback;

import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.util.MidiEventListener;

public abstract class BaseSequencer implements Sequencer
{
	protected final MidiEventListener listener;

	public BaseSequencer(MidiEventListener listener) {
		super();
		this.listener = listener;
	}

	protected void sendAllNotesOff()
	{
		Controller noteOff = new Controller(0, 0, 123, 0);
		for(int i=0 ; i<16 ; i++)
		{
			noteOff.setChannel(i);
			listener.onEvent(noteOff, 0);
		}
	}
	
}
