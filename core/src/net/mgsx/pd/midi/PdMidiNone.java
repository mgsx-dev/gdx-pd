package net.mgsx.pd.midi;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.midi.playback.Sequencer;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.util.MidiEventListener;

// TODO move none implementations to new class
// TODO repackage all by implementation type (none / remote / default)
public class PdMidiNone implements PdMidi
{

	@Override
	public MidiMusic createMidiMusic(FileHandle file) {
		return createMidiMusic(new MidiSequence(file));
	}

	@Override
	public MidiMusic createMidiMusic(MidiSequence sequence) {
		Sequencer sequencer = new Sequencer(){

			@Override
			public void play() {
			}

			@Override
			public void stop() {
			}

			@Override
			public void reset() {
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public void setBPM(float bpm) {
			}

			@Override
			public float getBPM() {
				return 0;
			}

			@Override
			public long getPositionInTicks() {
				return 0;
			}

			@Override
			public void setPositionInTicks(long ticks) {
			}
		};
		
		return new DefaultMidiMusic(sequencer, sequence);
	}

	@Override
	public MidiEventListener getPdSynth() {
		return new MidiEventListener() {
			@Override
			public void onStop(boolean finished) {
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
			}
		};
	}

}
