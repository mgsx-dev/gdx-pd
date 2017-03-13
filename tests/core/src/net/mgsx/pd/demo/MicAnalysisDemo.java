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

import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;
import net.mgsx.pd.utils.PdAdapter;

public class MicAnalysisDemo implements Demo
{
	private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) {
		patch  = Pd.audio.open(Gdx.files.internal("pd/breath.pd"));
		
		Table root = new Table(skin);
		
		root.add("Microphone Tracking Demo :").row();
		VerticalGroup list = new VerticalGroup();
		
		final Label volumeLabel = new Label("", skin);
		list.addActor(pdLabel("level", skin, volumeLabel));
		final Label pitchLabel = new Label("", skin);
		list.addActor(pdLabel("pitch", skin, pitchLabel));
		root.add(list);
		root.add(" ").row();
		root.add(" ").row();
		
		root.add("Attack  Tracking Demo :").row();
		root.add("1- click start learning,").row();
		root.add("2- produce a sound 3 times in a row,").row();
		root.add("3- repeat 2 at will,").row();
		root.add("4- click stop learning.").row();
		root.add("The attack tracker is now trained, attackId from mic input  ").row();
		root.add("should be coherent with the data gathered while training.").row();
		root.add(" ").row();
		
		VerticalGroup list2 = new VerticalGroup();
		list2.addActor(pdButton("start learning", skin, "start_learning"));
		list2.addActor(pdButton("stop learning", skin, "stop_learning"));
		list2.addActor(pdButton("forget", skin, "forget"));
		final Label attackId = new Label("", skin);
		list2.addActor(pdLabel("attackId", skin,  attackId));
		root.add(list2);
			
		
		return root;
	}

	@Override
	public void dispose() {
		patch.dispose();
	}

	@Override
	public String toString() {
		return "Mic Controller";
	}
	
	private Actor pdButton(String label, Skin skin, final String receiver)
	{
		final TextButton text = new TextButton(label,skin);
		text.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(receiver, 0.5f);
			}
		});
		return text;
	}
	
	private Actor pdLabel(final String label, Skin skin, final Label dlabel)
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
