package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class SoundEffectsDemo implements Demo
{
	private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) 
	{
		Table root = new Table(skin);
		
		String info = "Demonstrate both procedural and sample based sounds.\n" +
				"Sound design is intentionally simple to help understanding.";
		
		Label infoLabel = new Label(info, skin);
		infoLabel.setAlignment(Align.center);
		
		root.add(infoLabel).padBottom(30).colspan(4).row();
		
		addControl(root, "Explosion (procedural)", "explosion");
		addControl(root, "GUI (procedural)", "gui");
		addControl(root, "Buy (sample based)", "sample");
		
		patch = Pd.audio.open(Gdx.files.internal("pd/effects.pd"));
		
		return root;
	}
	
	private void addControl(Table table, String label, final String receiver)
	{
		table.add(label);
		for(int i=0 ; i<3 ; i++){
			final int value = i;
			final TextButton bt = new TextButton("Variation " + String.valueOf(i+1), table.getSkin());
			bt.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Pd.audio.sendList(receiver, value);
				}
			});
			table.add(bt);
		}
		table.row();
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
