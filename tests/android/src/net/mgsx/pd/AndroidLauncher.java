package net.mgsx.pd;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Pd.audio = new PdAudioAndroid(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GdxPdTest(), config);
	}
}
