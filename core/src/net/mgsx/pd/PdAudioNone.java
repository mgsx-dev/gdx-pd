package net.mgsx.pd;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Disabled PdAudio implementation
 * 
 * @author mgsx
 *
 */
class PdAudioNone implements PdAudio 
{
	@Override
	public void create() {
		Gdx.app.debug("audio", "audio create disabled");
	}

	@Override
	public void release() {
		Gdx.app.debug("audio", "audio release disabled");				
	}

	@Override
	public PdPatch open(FileHandle file) {
		return new PdPatch(0);
	}

	public void close(PdPatch patch) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public int arraySize(String name) {
		return 0;
	}

	@Override
	public void sendBang(String recv) {
	}

	@Override
	public void sendFloat(String recv, float x) {
	}

	@Override
	public void sendList(String recv, Object... args) {
	}

	@Override
	public void sendMessage(String recv, String msg, Object... args) {
	}

	@Override
	public void sendSymbol(String recv, String sym) {
	}

	@Override
	public void readArray(float[] destination, int destOffset, String source, int srcOffset, int n) {}

	@Override
	public void writeArray(String destination, int destOffset, float[] source, int srcOffset, int n) {}

	@Override
	public void addListener(String source, PdListener listener) {
	}

	@Override
	public void removeListener(String source, PdListener listener) {
	}
}