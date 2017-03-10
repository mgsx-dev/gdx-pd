package net.mgsx.gdx.pd;

import org.puredata.core.PdListener;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudioRemote;

public class PdAudioRemoteTest extends ApplicationAdapter
{
	private Stage stage;
	private Skin skin;
	private Table table;
	
	@Override
	public void create() {
		
		Pd.audio.create(new PdConfiguration());
		
		skin = new Skin(Gdx.files.classpath("skins/uiskin.json"));
		
		stage = new Stage(new ScreenViewport());
		
		table = new Table(skin);
		
		buildUI();
		
		ScrollPane scroll = new ScrollPane(table, skin);
		scroll.setFillParent(true);
		stage.addActor(scroll);
		
		Gdx.input.setInputProcessor(stage);
	}
	
	private void buildUI()
	{
		// TODO test construct with host / port arguments ...
		
		// TODO test arrays
		
		actionTest("Send Bang on recv_bang", new ActionHandler<Object>() {
			@Override
			public void run(Object value) {
				Pd.audio.sendBang("recv_bang");
			}
		});
		
		valueTest("Send Float on recv_float", new ActionHandler<Float>() {
			@Override
			public void run(Float value) {
				Pd.audio.sendFloat("recv_float", value);
			}
		});
		
		actionTest("Send List on recv_list", new ActionHandler<Object>() {
			@Override
			public void run(Object value) {
				Pd.audio.sendList("recv_list", "some text", 42, 4.45f, 3.12f, 67, 59);
			}
		});
		
		testSendList("Integers", 12, -57);
		testSendList("Integers limits", Integer.MIN_VALUE, Integer.MAX_VALUE);
		testSendList("Longs", 12L, -57L);
		testSendList("Longs limits", Long.MIN_VALUE, Long.MAX_VALUE);
		testSendList("Floats", 12.3f, -57.3456f);
		testSendList("Floats limits", Float.MIN_VALUE, Float.MAX_VALUE);
		testSendList("Doubles", 12.3, -57.3456);
		testSendList("Doubles limits", Double.MIN_VALUE, Double.MAX_VALUE);
		testSendList("Strings", "foo", "bar");
		testSendList("Strings with space", "foo bar hello world");
		testSendList("Strings empty", "", "", "");
		testSendList("Strings special", "\n", "$", "\\", "\u2834");
		
		
		testSendMessage("Integers", 12, -57);
		testSendMessage("Integers limits", Integer.MIN_VALUE, Integer.MAX_VALUE);
		testSendMessage("Longs", 12L, -57L);
		testSendMessage("Longs limits", Long.MIN_VALUE, Long.MAX_VALUE);
		testSendMessage("Floats", 12.3f, -57.3456f);
		testSendMessage("Floats limits", Float.MIN_VALUE, Float.MAX_VALUE);
		testSendMessage("Doubles", 12.3, -57.3456);
		testSendMessage("Doubles limits", Double.MIN_VALUE, Double.MAX_VALUE);
		testSendMessage("Strings", "foo", "bar");
		testSendMessage("Strings with space", "foo bar hello world");
		testSendMessage("Strings empty", "", "", "");
		testSendMessage("Strings special", "\n", "$", "\\", "\u2834");
		
		testSendSymbol("Symbol path", "/home/me/myFile.txt");
		
		testSendArray("Simple float array", "array_to_write", 0, 32.5f, 54.7f, 62.12f, 99.456f);
		
		
		// One second of 441 Hz sinusoidal signal.
		float [] sampleData = new float[44100];
		for(int i=0 ; i<sampleData.length ; i++){
			sampleData[i] = (float)Math.sin(Math.PI * 2 * 441 * (float)i / 44100f);
		}
		
		testSendArray("Big float array", "big_array", 0, sampleData);

		
		testListener("send_any");
	}
	
	private void testSendArray(String label, final String name, final int position, final float...values)
	{
		String valText = "";
		if(values.length < 8)
			for(float v : values) valText += "," + String.valueOf(v);
		else
			valText = ",...";
		if(valText.length()>0) valText = "(" + valText.substring(1) + ")";
		valText = "at " + String.valueOf(position) + " " + valText;
		TextButton bt = new TextButton(label + " " + valText, skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.writeArray(name, position, values, 0, values.length);
			}
		});
		table.add("Write array to " + name);
		table.add(bt);
		table.row();
	}
	
	private void testListener(final String recv)
	{
		final Label content = new Label("", skin);
		table.add("Test Listeners");
		
		Table stable = new Table(skin);
		table.add(stable);
		table.row();
		
		final TextButton btListen = new TextButton("Listen", skin, "toggle");
		stable.add(btListen);
		stable.add(content);
		
		final PdListener listener = new PdListener(){

			@Override
			public void receiveBang(String source) {
				content.setText("receive bang on " + source);
			}

			@Override
			public void receiveFloat(String source, float x) {
				content.setText("receive float on " + source + " : " + String.valueOf(x));
			}

			@Override
			public void receiveSymbol(String source, String symbol) {
				content.setText("receive symbol on " + source + " : " + symbol);
			}

			@Override
			public void receiveList(String source, Object... args) {
				String text = "";
				for(Object arg : args) text += "," + arg.toString();
				if(text.length() > 0) text = text.substring(1);
				content.setText("receive list on " + source + " : " + text);
			}

			@Override
			public void receiveMessage(String source, String symbol, Object... args) {
				String text = symbol;
				for(Object arg : args) text += "," + arg.toString();
				content.setText("receive message on " + source + " : " + text);
			}
			
		};
		
		btListen.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btListen.isChecked()){
					Pd.audio.addListener(recv, listener);
				}else{
					Pd.audio.removeListener(recv, listener);
				}
			}
		});
		
	}
	
	private void testSendList(String label, final Object...values)
	{
		String valText = "";
		for(Object v : values) valText += "," + v.toString();
		valText = "(" + valText.substring(1) + ")";
		
		TextButton bt = new TextButton(label + " " + valText, skin);
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList("recv_list", values);
			}
		});
		table.add("Send List on recv_list");
		table.add(bt);
		table.row();
	}
	
	private void testSendMessage(String label, final Object...values)
	{
		String valText = "";
		for(Object v : values) valText += "," + v.toString();
		valText = "(" + valText.substring(1) + ")";
		
		TextButton bt = new TextButton(label + " " + valText, skin);
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendMessage("recv_msg", "test", values);
			}
		});
		table.add("Send Message on recv_msg");
		table.add(bt);
		table.row();
	}

	private void testSendSymbol(String label, final String symbol)
	{
		TextButton bt = new TextButton(label + " (" + symbol + ")", skin);
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendSymbol("recv_sym", symbol);
			}
		});
		table.add("Send Symbol on recv_sym");
		table.add(bt);
		table.row();
	}

	
	private void actionTest(String label, final ActionHandler<?> runnable) {
		TextButton bt = new TextButton("test", skin);
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				runnable.run(null);
			}
		});
		table.add(label);
		table.add(bt);
		table.row();
	}
	
	private void valueTest(String label, final ActionHandler<Float> runnable) {
		final Slider sl = new Slider(-100, 100, 0.1f, false, skin);
		sl.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				runnable.run(sl.getValue());
			}
		});
		table.add(label);
		table.add(sl);
		table.row();
	}
	
	private static interface ActionHandler<T>
	{
		public void run(T value);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		PdConfiguration.remoteEnabled = true;
		
		new LwjglApplication(new PdAudioRemoteTest(), config);
		
	}
}
