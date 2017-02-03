package net.mgsx.pd.midi;

import net.mgsx.midi.playback.Sequencer;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.util.MidiUtil;

public class DefaultMidiMusic implements MidiMusic
{
	private Sequencer sequencer;
	public MidiSequence sequence; // XXX
	
	public DefaultMidiMusic(Sequencer sequencer, MidiSequence sequence) {
		this.sequencer = sequencer;
		this.sequence = sequence;
	}
	
	@Override
	public void play() {
		sequencer.play();
	}

	@Override
	public void pause() {
		sequencer.stop();
	}

	@Override
	public void stop() {
		sequencer.stop();
		sequencer.reset();
	}

	@Override
	public boolean isPlaying() {
		return sequencer.isRunning();
	}

	@Override
	public void setLooping(boolean isLooping) {
		// not supported yet
	}

	@Override
	public boolean isLooping() {
		// not supported yet
		return false;
	}

	@Override
	public void setVolume(float volume) {
		// not supported yet
	}

	@Override
	public float getVolume() {
		// not supported yet
		return 0;
	}

	@Override
	public void setPan(float pan, float volume) {
		// not supported yet
	}

	@Override
	public void setPosition(float position) {
		sequencer.setPositionInTicks((long)MidiUtil.msToTicks((long)(position * 1000), sequencer.getBPM(), sequence.getResolution()));
	}

	@Override
	public float getPosition() {
		return MidiUtil.ticksToMs(sequencer.getPositionInTicks(), sequencer.getBPM(), sequence.getResolution()) / 1000f;
	}

	@Override
	public void dispose() {
		sequencer.stop();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		// not supported yet
	}

	@Override
	public void setBPM(float bpm) {
		sequencer.setBPM(bpm);
	}

	@Override
	public float getBPM() {
		return sequencer.getBPM();
	}

	@Override
	public float getDuration() {
		return MidiUtil.ticksToMs(sequence.getLengthInTicks(), sequencer.getBPM(), sequence.getResolution()) / 1000f;
	}

}
