package net.mgsx.pd.midi;

import org.puredata.core.PdBase;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteOff;
import net.mgsx.midi.sequence.event.NoteOn;
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
		}, MidiEvent.class);
		
		return new DefaultMidiMusic(sequencer, sequence);
	}

}
