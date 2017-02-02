package net.mgsx.pd.demo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public interface Demo extends Disposable
{
	public Actor create(Skin skin);
	
}
