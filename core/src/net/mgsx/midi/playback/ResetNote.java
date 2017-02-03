package net.mgsx.midi.playback;

import net.mgsx.midi.sequence.event.NoteOn;

public class ResetNote extends NoteOn
{
	public ResetNote() {
		super(0,0,0,0);
	}
	
	public ResetNote set(int channel, int note){
		this.mChannel = channel;
		this.mValue1 = note;
		return this;
	}
}