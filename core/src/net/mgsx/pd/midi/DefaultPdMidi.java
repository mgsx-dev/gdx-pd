package net.mgsx.pd.midi;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.ChannelAftertouch;
import net.mgsx.midi.sequence.event.Controller;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteAftertouch;
import net.mgsx.midi.sequence.event.NoteOff;
import net.mgsx.midi.sequence.event.NoteOn;
import net.mgsx.midi.sequence.event.PitchBend;
import net.mgsx.midi.sequence.event.ProgramChange;
import net.mgsx.midi.sequence.util.MidiEventListener;
import net.mgsx.midi.sequence.util.MidiProcessor;

public class DefaultPdMidi implements PdMidi
{

	@Override
	public MidiMusic createMidiMusic(FileHandle file) 
	{
		return createMidiMusic(new MidiSequence(file));
	}

	@Override
	public MidiMusic createMidiMusic(MidiSequence sequence) 
	{
		MidiProcessor sequencer = new MidiProcessor(sequence);
		sequencer.registerEventListener(new MidiEventListener() {
			
			@Override
			public void onStop(boolean finished) {
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
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
				
			}
		}, MidiEvent.class);
		
		return new DefaultMidiMusic(sequencer, sequence);
	}

}
