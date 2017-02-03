package net.mgsx.pd.demo;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.pd.Pd;
import net.mgsx.pd.events.PdAdapter;
import net.mgsx.pd.patch.PdPatch;

public class MicAnalysisDemo implements Demo
{
	private PdPatch patch;
	private PdListener listener;
	
	@Override
	public Actor create(Skin skin) {
		patch  = Pd.audio.open(Gdx.files.internal("pd/breath.pd"));
		
		final Label levelLabel = new Label("", skin);
		
		listener = new PdAdapter(){
			@Override
			public void receiveFloat(String source, float x) {
				levelLabel.setText(String.valueOf(Math.round(x * 100)));
			}
		};
		
		Pd.audio.addListener("level", listener);
		
		return levelLabel;
	}

	@Override
	public void dispose() {
		Pd.audio.removeListener("level", listener);
		patch.dispose();
	}

	@Override
	public String toString() {
		return "Mic Controller";
	}

}
