package net.mgsx.midi.playback;

public interface Sequencer {

	public void play();
	public void stop();
	public void reset();
	public boolean isRunning();
	public void setBPM(float bpm);
	public float getBPM();
}
