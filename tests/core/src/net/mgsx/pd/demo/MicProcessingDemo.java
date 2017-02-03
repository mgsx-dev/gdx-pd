package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class MicProcessingDemo implements Demo
{
	private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) 
	{
		patch = Pd.audio.open(Gdx.files.internal("pd/pitchshift.pd"));
		
		Table root = new Table(skin);
		
		root.add("Microphone Demo").row();
		
		VerticalGroup list = new VerticalGroup();
		list.addActor(pdButton("Realtime", skin, "mode", 0));
		list.addActor(pdButton("Record", skin, "mode", 1));
		list.addActor(pdButton("Play", skin, "mode", 2));
		list.addActor(pdButton("Stop", skin, "mode", 3));
		list.addActor(pdToggle("Vador", skin, "vador"));
		
		root.add(list);
		
		final Slider pitchSlider = new Slider(0, 2, .01f, false, skin);
		
		root.add(pitchSlider);
		
		pitchSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendFloat("pitch", pitchSlider.getValue());
			}
		});
		
		return root; // new Label("Microphone Demo", skin);
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

	private Actor pdToggle(String label, Skin skin, final String recv)
	{
		final TextButton button = new TextButton(label, skin, "toggle");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(recv, button.isChecked() ? 1 : 0);
			}
		});
		return button;
	}

	@Override
	public void dispose() {
		Pd.audio.close(patch);
	}
	
	@Override
	public String toString() {
		return "Mic Processing";
	}
	
}
