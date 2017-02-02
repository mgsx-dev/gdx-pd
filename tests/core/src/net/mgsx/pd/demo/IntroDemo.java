package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class IntroDemo implements Demo {

	@Override
	public Actor create(Skin skin) 
	{
		VerticalGroup list = new VerticalGroup();
		list.addActor(new Label("This is a gdx-pd demo, see http://github.com/... for further details", skin));
		list.addActor(link("https://github.com/mgsx-dev/gdx-pd", skin));
		return list;
	}

	private Actor link(final String url, Skin skin) 
	{
		Label button = new Label(url, skin, "link");
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.net.openURI(url);
			}
		});
		return button;
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public String toString() {
		return "";
	}

}
