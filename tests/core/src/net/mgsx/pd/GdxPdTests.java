package net.mgsx.pd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GdxPdTests extends ApplicationAdapter 
{
	private Stage stage;
	private Skin skin;
	
	@Override
	public void create () 
	{
		PdConfiguration config = new PdConfiguration();
		config.inputChannels = 1;
		Pd.audio.create(config);

		// new ScreenViewport()
		stage = new Stage(new FitViewport(800, 600));
		
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
		
		Table root = new Table();
		root.defaults().pad(10);
		
		
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}
	
	@Override
	public void dispose () 
	{
		stage.dispose();
		Pd.audio.release();
	}
}
