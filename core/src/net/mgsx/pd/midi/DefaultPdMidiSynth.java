package net.mgsx.pd.midi;

import org.puredata.core.PdBase;

import net.mgsx.midi.sequence.event.ChannelAftertouch;
import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteAftertouch;
import net.mgsx.midi.sequence.event.NoteOff;
import net.mgsx.midi.sequence.event.NoteOn;
import net.mgsx.midi.sequence.event.PitchBend;
import net.mgsx.midi.sequence.event.ProgramChange;
import net.mgsx.midi.sequence.event.SystemExclusiveEvent;
import net.mgsx.midi.sequence.util.MidiEventListener;

public class DefaultPdMidiSynth implements MidiEventListener 
{
	@Override
	public void onStop(boolean finished) {
		System.out.println("onStop");// TODO send all notes off ?
	}

	@Override
	public void onStart(boolean fromBeginning) {
		System.out.println("onStart"); // TODO send something to Pd ?
	}

	@Override
	public void onEvent(MidiEvent event, long ms) 
	{
		if(event instanceof NoteOn){
			NoteOn e = (NoteOn)event;
			PdBase.sendNoteOn(e.getChannel(), e.getNoteValue(), e.getVelocity());
		}
		else if(event instanceof NoteOff){
			// pd process note off as note on with zero velocity
			NoteOff e = (NoteOff)event;
			PdBase.sendNoteOn(e.getChannel(), e.getNoteValue(), 0);
		}
		else if(event instanceof Controller){
			Controller e = (Controller)event;
			PdBase.sendControlChange(e.getChannel(), e.getControllerType(), e.getValue());
		}
		else if(event instanceof NoteAftertouch){
			NoteAftertouch e = (NoteAftertouch)event;
			PdBase.sendPolyAftertouch(e.getChannel(), e.getNoteValue(), e.getAmount());
		}
		else if(event instanceof PitchBend){
			PitchBend e = (PitchBend)event;
			PdBase.sendPitchBend(e.getChannel(), e.getBendAmount());
		}
		else if(event instanceof ChannelAftertouch){
			ChannelAftertouch e = (ChannelAftertouch)event;
			PdBase.sendAftertouch(e.getChannel(), e.getAmount());
		}
		else if(event instanceof ProgramChange){
			ProgramChange e = (ProgramChange)event;
			PdBase.sendProgramChange(e.getChannel(), e.getProgramNumber());
		}
		else if(event instanceof SystemExclusiveEvent){
			// TODO test it ...
			SystemExclusiveEvent e = (SystemExclusiveEvent)event;
			for(byte b : e.getData())
				PdBase.sendSysex(0, b & 0xFF); // TODO don't know what is port in Pd semantic.
		}
		// XXX just skip all other events (mainly meta events)
	}
}