package net.mgsx.pd;

import java.io.IOException;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import net.mgsx.pd.patch.PdPatch;

public class SoundManagerTest {

	private static class Voice{
		public String id;
		public long index;
		public Voice set(long index){
			this.index = index;
			this.id = "snd-" + index;
			return this;
		}
		public void stop() {
			Pd.audio.sendMessage(id, "stop");
		}
		public void pause() {
			// TODO Auto-generated method stub
			
		}
		public void resume() {
			// TODO Auto-generated method stub
			
		}
		public void setLooping(boolean looping) {
			// TODO Auto-generated method stub
			
		}
		public void setPitch(float pitch) {
			// TODO Auto-generated method stub
			
		}
		public void setVolume(float volume) {
			// TODO Auto-generated method stub
			
		}
		public void setPan(float pan, float volume) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class PdSoundManager implements Audio, Disposable {
		
		private Audio audio;
		private PdPatch patch;
		private Array<Voice> usedVoices, unusedVoices, allVoices;
		private int poly = 16;
		
		public PdSoundManager(Audio audio) {
			super();
			this.audio = audio;
			unusedVoices = new Array<Voice>(poly);
			usedVoices = new Array<Voice>(poly);
			allVoices = new Array<Voice>(poly);
			for(int i=0 ; i<poly ; i++){
				allVoices.add(new Voice().set(i));
			}
			unusedVoices.addAll(allVoices);
			
			patch = Pd.audio.open(Gdx.files.local("resources/sound-manager.pd"));
			Pd.audio.sendMessage("sound-manager-samples", "clear");
			Pd.audio.sendMessage("sound-manager-voices", "clear");
			
			for(int i=0 ; i<poly ; i++){
				Pd.audio.sendMessage("sound-manager-voices", "add", i);
			}
			
		}

		@Override
		public Sound newSound(FileHandle fileHandle) 
		{
			
			// TODO read data as wav (float)
			WavInputStream input = new WavInputStream(fileHandle);
			try {
				byte[] bytes = StreamUtils.copyStreamToByteArray(input, input.dataRemaining);
				int channels = input.channels;
				int samplerate = input.sampleRate;
				float [] floatBuffer = new float[(bytes.length / channels)/2];
				// TODO create table in pd
				for(int i=0 ; i<floatBuffer.length ; i++){
					short value = (short)((bytes[i * channels * 2 + 1] & 0xFF)<<8 | bytes[i * channels * 2 + 0] & 0xFF);
					floatBuffer[i] = value / (float)(1<<15);
				}
				// TODO fill table
				Pd.audio.sendMessage("sound-manager-samples", "add", 0, floatBuffer.length);
				Pd.audio.writeArray("smp-0", 0, floatBuffer, 0, floatBuffer.length);
				return new PdSound(this, 0, floatBuffer.length, samplerate);
			} catch (IOException e) {
				throw new GdxRuntimeException(e);
			}
			
		}
		
		@Override
		public Music newMusic(FileHandle file) {
			// TODO stream to pd array ?
			return null;
		}
		
		@Override
		public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
			// could be replaced by file stream
			return audio.newAudioRecorder(samplingRate, isMono);
		}
		
		@Override
		public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
			// could be replaced by file stream
			return audio.newAudioDevice(samplingRate, isMono);
		}

		@Override
		public void dispose() {
			patch.dispose();
		}

		public Voice obtain() {
			Voice voice;
			if(unusedVoices.size > 0){
				voice = unusedVoices.pop();
				usedVoices.add(voice);
			}else{
				voice = usedVoices.removeIndex(0);
				usedVoices.add(voice);
			}
			return voice;
		}

		public Voice voice(long soundId) {
			return allVoices.get((int)soundId);
		}

		public void free(Voice voice) {
			usedVoices.removeValue(voice, true);
			unusedVoices.add(voice);
		}
	}
	
	private static class PdSound implements Sound
	{
		private String id;
		private int index;
		private long samples;
		private PdSoundManager manager;
		private Array<Voice> voices = new Array<Voice>();
		private int samplerate;
		
		public PdSound(PdSoundManager manager, int index, long samples, int samplerate) {
			this.manager = manager;
			this.index = index;
			this.id = "smp-" + index;
			this.samples = samples;
			this.samplerate = samplerate;
		}

		@Override
		public long play() {
			return play(1);
		}

		@Override
		public long play(float volume) {
			return play(1, 1, 0);
		}

		@Override
		public long play(float volume, float pitch, float pan) {
			return play(volume, pitch, pan, false);
		}

		@Override
		public long loop() {
			return loop(1);
		}

		@Override
		public long loop(float volume) {
			return loop(1, 1, 0);
		}

		@Override
		public long loop(float volume, float pitch, float pan) {
			return play(volume, pitch, pan, true);
		}

		private long play(float volume, float pitch, float pan, boolean looping) {
			// TODO allocate id
			Voice voice = manager.obtain();
			// TODO send messsage note on
			Pd.audio.sendMessage(voice.id, "play", index, (float)samples, (float)samplerate, volume, pitch, pan, looping ? 1 : 0);
			// return voice
			return voice.index;
		}

		@Override
		public void stop() {
			for(Voice voice : voices){
				stop(voice.index);
			}
		}

		@Override
		public void pause() {
			for(Voice voice : voices){
				pause(voice.index);
			}
		}

		@Override
		public void resume() {
			for(Voice voice : voices){
				resume(voice.index);
			}
		}

		@Override
		public void dispose() {
			// TODO send message remove table ... etc
			stop();
			// TODO resize array 0 !
		}

		@Override
		public void stop(long soundId) {
			// TODO desallocate id from poly
			// TODO send message note off for id
			Voice voice = manager.voice(soundId);
			voice.stop();
			manager.free(voice);
		}

		@Override
		public void pause(long soundId) {
			manager.voice(soundId).pause();
		}

		@Override
		public void resume(long soundId) {
			manager.voice(soundId).resume();
		}

		@Override
		public void setLooping(long soundId, boolean looping) {
			manager.voice(soundId).setLooping(looping);
		}

		@Override
		public void setPitch(long soundId, float pitch) {
			manager.voice(soundId).setPitch(pitch);
		}

		@Override
		public void setVolume(long soundId, float volume) {
			manager.voice(soundId).setVolume(volume);
		}

		@Override
		public void setPan(long soundId, float pan, float volume) {
			manager.voice(soundId).setPan(pan, volume);
			
		}
		
	}
	
	public static void main(String[] args) 
	{
	
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.audioDeviceBufferSize = 512 * 4;
		config.audioDeviceBufferCount = 8;
		
		new LwjglApplication(new Game(){
			
//			private Pool<Integer> voices = new Pool<Integer>(){};
//			private void allocate(){
//				if(voices.size)
//			}
			
			@Override
			public void create() {
				
				// play a pd patch
				Pd.audio.create(new PdConfiguration());
				Gdx.audio = new PdSoundManager(Gdx.audio);
				
				// and sounds at the same time
				final Sound snd = Gdx.audio.newSound(Gdx.files.classpath("shotgun.wav"));
				snd.play();
				Gdx.input.setInputProcessor(new InputAdapter(){
					@Override
					public boolean touchDown(int screenX, int screenY, int pointer, int button) {
						snd.play();
						return true;
					}
				});
				
			}}, config);
		
	}
}
