package net.mgsx.pd.midi;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

abstract public class JavaMidiMusic implements MidiMusic
{
	
	private Sequencer sequencer;
	private float volume = 1;
	public JavaMidiMusic(boolean connected)
	{
		try {
			sequencer = MidiSystem.getSequencer(connected);
			sequencer.open();
		} catch (MidiUnavailableException e) {
			throw new GdxRuntimeException("can't get a Java Midi Sequencer", e);
		}
	}
	
	public void setSequence(FileHandle file){
		try {
			sequencer.setSequence(file.read());
		} catch (IOException e) {
			throw new GdxRuntimeException("can't open midi file", e);
		} catch (InvalidMidiDataException e) {
			throw new GdxRuntimeException("invalid midi file", e);
		}
	}
	
	@Override
	public void play() {
		sequencer.start();
	}

	@Override
	public void pause() {
		sequencer.stop();
	}

	@Override
	public void stop() {
		sequencer.stop();
		sequencer.setTickPosition(0);
	}

	@Override
	public boolean isPlaying() {
		return sequencer.isRunning();
	}

	@Override
	public void setLooping(boolean isLooping) {
		sequencer.setLoopCount(isLooping ? Sequencer.LOOP_CONTINUOUSLY : 0);
	}
	
	@Override
	public boolean isLooping() {
		return sequencer.getLoopCount() != 0;
	}

	@Override
	public void setVolume(float volume) {
		this.volume = volume;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public void setPan(float pan, float volume) {
		this.volume = volume;
	}

	@Override
	public void setPosition(float position) {
		sequencer.setMicrosecondPosition((long)(position * 1e6f));
	}

	@Override
	public float getPosition() { 
		return sequencer.getMicrosecondPosition() * 1e-6f;
	}
	
	public float getDuration() {
		return sequencer.getMicrosecondLength() * 1e-6f;
	}
	
	protected long toTicks(float position){
		return (long)(position * sequencer.getTickLength() / getDuration());
	}
	protected float fromTicks(long ticks){
		return ticks * getDuration() / (float)sequencer.getTickLength();
	}
	
	public void setLoopStart(float position) {
		sequencer.setLoopStartPoint(toTicks(position));
	}
	public void setLoopEnd(float position) {
		sequencer.setLoopEndPoint(toTicks(position));
	}
	public float getLoopStart() {
		return fromTicks(sequencer.getLoopStartPoint());
	}
	public float getLoopEnd() {
		return fromTicks(sequencer.getLoopEndPoint());
	}

	@Override
	public void dispose() {
		sequencer.stop();
		sequencer.close();
	}

	@Override
	public void setOnCompletionListener(final OnCompletionListener listener) {
		sequencer.addMetaEventListener(new MetaEventListener() {
			@Override
			public void meta(MetaMessage meta) {
				if(meta.getType() == MidiMetaType.EndOfTrack){
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							listener.onCompletion(JavaMidiMusic.this);
						}
					});
				}
			}
		});
	}
	
	/**
	 * Get access to underlying sequencer for custom use :
	 * change tempo, loop cue points, mute/solo tracks ...
	 * @return
	 */
	public Sequencer getSequencer(){
		return sequencer;
	}

}
