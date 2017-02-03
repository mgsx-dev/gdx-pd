package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class SoundEffectsDemo implements Demo
{

private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) 
	{
		Table root = new Table(skin);
		
		addControl(root, "Explosion", "boom");
		addControl(root, "Shot", "shot");
		addControl(root, "GUI", "gui");
		
		patch = Pd.audio.open(Gdx.files.internal("pd/effects.pd"));
		
		return root;
	}
	
	private void addControl(Table table, String label, final String receiver)
	{
		final TextButton low = new TextButton("low", table.getSkin());
		low.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(receiver, 0.5f);
			}
		});
		final TextButton high = new TextButton("high", table.getSkin());
		high.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(receiver, 1f);
			}
		});
		
		table.add(label);
		table.add(low);
		table.add(high).row();
	}

	@Override
	public void dispose() {
		patch.dispose();
	}

	@Override
	public String toString() {
		return "Sound Effects";
	}
}
