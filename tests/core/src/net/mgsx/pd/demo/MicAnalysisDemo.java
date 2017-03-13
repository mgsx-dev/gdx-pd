package net.mgsx.pd.demo;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import net.mgsx.pd.Pd;
import net.mgsx.pd.events.PdAdapter;
import net.mgsx.pd.patch.PdPatch;

public class MicAnalysisDemo implements Demo
{
	private PdPatch patch;
	private PdListener volumelistener;
	private PdListener pitchListener;
	
	@Override
	public Actor create(Skin skin) {
		patch  = Pd.audio.open(Gdx.files.internal("pd/breath.pd"));
		
		Table root = new Table(skin);
		
		root.add("Microphone Demo").row();
		VerticalGroup list = new VerticalGroup();
		
		final Label volumeLabel = new Label("", skin);
		list.addActor(pdLabel("level", skin, "mode", 0, volumeLabel));
		final Label pitchLabel = new Label("", skin);
		list.addActor(pdLabel("pitch", skin, "mode", 0, pitchLabel));
		
		
		root.add(list);
			
		
		return root;
	}

	@Override
	public void dispose() {
		Pd.audio.removeListener("level", volumelistener);
		Pd.audio.removeListener("level", pitchListener);
		patch.dispose();
	}

	@Override
	public String toString() {
		return "Mic Controller";
	}
	
	private Actor pdButton(String label, Skin skin, final String recv, final int msg)
	{
		TextButton button = new TextButton(label, skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(recv, msg);
			}
		});
		return button;
	}
	
	private Actor pdLabel(final String label, Skin skin, final String recv, final int msg, final Label dlabel)
	{
		PdListener listener;	
		listener = new PdAdapter(){
		@Override
		public void receiveFloat(String source, float x) {
				dlabel.setText(label+" : " + String.valueOf(Math.round(x)));
			}
		};
		Pd.audio.addListener(label, listener);
		return dlabel;
	}


}
