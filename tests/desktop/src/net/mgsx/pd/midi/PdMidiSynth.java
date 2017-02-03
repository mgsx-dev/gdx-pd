package net.mgsx.pd.midi;

import org.puredata.core.PdBase;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.util.MidiEventListener;

public class PdMidiSynth implements MidiEventListener 
{
	public static final MidiEventListener instance = new PdMidiSynth();

	@Override
	public void onStop(boolean finished) {
		System.out.println("onStop");
	}

	@Override
	public void onStart(boolean fromBeginning) {
		System.out.println("onStart");
	}

	@Override
	public void onEvent(MidiEvent event, long ms) {
		if(event instanceof NoteOn){
			NoteOn no = (NoteOn)event;
			PdBase.sendNoteOn(no.getChannel(), no.getNoteValue(), no.getVelocity());
		}
		else if(event instanceof NoteOff){
			NoteOff no = (NoteOff)event;
			PdBase.sendNoteOn(no.getChannel(), no.getNoteValue(), 0);
		}
		else if(event instanceof ProgramChange){
			ProgramChange no = (ProgramChange)event;
			PdBase.sendProgramChange(no.getChannel(), no.getProgramNumber());
		}
		
	}
}