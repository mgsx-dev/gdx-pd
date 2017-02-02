package net.mgsx.pd;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;
import net.mgsx.pd.midi.DefaultPdMidi;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Pd.audio = new PdAudioAndroid(this);
		Pd.midi = new DefaultPdMidi();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GdxPdDemo(), config);
	}
}
