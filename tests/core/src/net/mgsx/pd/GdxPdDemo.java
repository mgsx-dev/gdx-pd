package net.mgsx.pd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.mgsx.pd.demo.AtmosphereDemo;
import net.mgsx.pd.demo.Demo;
import net.mgsx.pd.demo.IntroDemo;
import net.mgsx.pd.demo.MicAnalysisDemo;
import net.mgsx.pd.demo.MicProcessingDemo;
import net.mgsx.pd.demo.MidiMusicDemo;
import net.mgsx.pd.demo.MidiSequencerDemo;
import net.mgsx.pd.demo.SoundEffectsDemo;

public class GdxPdDemo extends ApplicationAdapter 
{
	private Stage stage;
	private Skin skin;
	private Demo demo;
	private Table demoPlaceholder;
	
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
		
		Table header = new Table(skin);
		Image gdxIcon = new Image(new Texture(Gdx.files.internal("libgdx-logo.png")));
		Image pdIcon = new Image(new Texture(Gdx.files.internal("pd-icon.png")));
		
		header.add(gdxIcon);
		header.add(pdIcon);
		
		final SelectBox<Demo> demoSelector = new SelectBox<Demo>(skin);
		
		demoSelector.setItems(new Demo[]{
			new IntroDemo(),
			new MidiMusicDemo(),
			new MidiSequencerDemo(),
			new AtmosphereDemo(),
			new SoundEffectsDemo(),
			new MicAnalysisDemo(),
			new MicProcessingDemo()
		});
		
		demoPlaceholder = new Table(skin);
		
		header.add(demoSelector);
		root.add(header).row();
		root.add(demoPlaceholder).expand();
		
		stage.addActor(root);
		
		root.setFillParent(true);
		
		demoSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setDemo(demoSelector.getSelected());
			}
		});
		
		setDemo(demoSelector.getSelected());
		
		Gdx.input.setInputProcessor(stage);
	}
	
	private void setDemo(Demo newDemo)
	{
		if(demo != null){
			demo.dispose();
		}
		demoPlaceholder.clear();
		demo = newDemo;
		if(demo != null){
			demoPlaceholder.add(demo.create(skin));
		}
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
		if(demo != null){
			demo.dispose();
		}
		stage.dispose();
		Pd.audio.release();
	}
}
