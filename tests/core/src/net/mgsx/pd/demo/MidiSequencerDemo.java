package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.midi.playback.LiveSequencer;
import net.mgsx.midi.playback.LiveTrack;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.MidiEvent;
import net.mgsx.midi.sequence.event.NoteOn;
import net.mgsx.midi.sequence.event.meta.TrackName;
import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class MidiSequencerDemo implements Demo
{
	public static class Division{
		
		public static final Division quarterNote = new Division("Quarter", 1); // TODO eighteen ?
		public static final Division halfNote = new Division("Half", 2);
		public static final Division wholeNote = new Division("Whole", 4);
		public static final Division bar2 = new Division("2x", 8);
		public static final Division bar4 = new Division("4x", 16);
		public static final Division bar8 = new Division("8x", 32);
		public static final Division bar16 = new Division("16x", 64);
		
		public static final Array<Division> all = new Array<Division>(new Division[]{
			quarterNote, 
			halfNote, 
			wholeNote, 
			bar2, 
			bar4, 
			bar8, 
			bar16
		});
		
		public String name;
		public int value;
		public Division(String name, int value) {
			super();
			this.name = name;
			this.value = value;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	private LiveSequencer seq;
	private PdPatch patch;
	private Skin skin;

	@Override
	public Actor create(Skin skin) 
	{
		this.skin = skin;
		
		Table table = new Table(skin);
		
		patch = Pd.audio.open(Gdx.files.internal("pdmidi/midiplayer.pd"));
		
		seq = new LiveSequencer(Pd.midi.getPdSynth());
		
		buildGUI(table);
		
		return table;
	}

	private Table matrix;
	private SelectBox<Division> trigBox;
	private SelectBox<Division> lenBox;
	
	private void buildGUI(Table table) 
	{
		lenBox = new SelectBox<Division>(skin);
		
		Table header = new Table(skin);
		
		final SelectBox<FileHandle> songBox = new SelectBox<FileHandle>(skin);
		songBox.setItems(Gdx.files.internal("music").list(".mid"));
		header.add("Song");
		header.add(songBox);
		
		songBox.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeSong(songBox.getSelected());
			}
		});
		
		
		final Label bpmField = new Label("", skin);
		
		header.add("BPM");
		header.add(bpmField).width(60);
		final Slider slider = new Slider(30, 240, .01f, false, skin);
		header.add(slider);
		header.row();
		
		slider.setValue(100);
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seq.bpm = slider.getValue();
				bpmField.setText(String.valueOf((int)seq.bpm));
			}
		});
		
		
		final TextButton btPlay = new TextButton("Play", skin, "toggle");
		btPlay.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btPlay.isChecked()) seq.play(); else seq.stop();
			}
		});
		header.add(btPlay);
		
		trigBox = new SelectBox<Division>(skin);
		trigBox.setItems(Division.all);
		header.add("trigger");
		header.add(trigBox);
		trigBox.setSelected(Division.quarterNote);
		
		lenBox.setItems(Division.all);
		header.add("size");
		header.add(lenBox);
		lenBox.setSelected(Division.wholeNote);
		
		lenBox.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				matrix.clear();
				buildMatrix(matrix, lenBox.getSelected());
				
			}});
		
		matrix = new Table(skin);
		buildMatrix(matrix, lenBox.getSelected());
		
		table.add(header).expandX().center();
		table.row();
		table.add(new ScrollPane(matrix, skin)).expand().center().top();
		
		changeSong(songBox.getSelected());
	}
	
	private void changeSong(FileHandle file)
	{
		seq.stop();
		seq.clear();
		
		MidiSequence sequence = new MidiSequence(file);

		seq.load(sequence);
		matrix.clear();
		buildMatrix(matrix, lenBox.getSelected());
	}
	
	private void buildMatrix(Table table, final Division division)
	{
		table.defaults().fill();
		
		table.row();
		
		int nTracks = seq.getTracks().size;
		
		table.add("Track");
		for(int x=0 ; x<nTracks ; x++){
			table.add(String.valueOf(x+1)).padLeft(10).padRight(10);
		}
		table.row();
		
		final Array<Button> muteButtons = new Array<Button>();
		
		table.add("Name");
		for(int x=0 ; x<nTracks ; x++){
			String name = null;
			for(MidiEvent e : seq.getTracks().get(x).getEvents()){
				if(e instanceof TrackName){
					if(name != null){
						Gdx.app.error("Midi", "warning : multiple track names in same track");
						continue;
					}
					name = ((TrackName) e).getTrackName();
				}
			}
			table.add(name == null ? "-" : name);
		}
		table.row();
		
		final TextButton btLoopAll = new TextButton("Unloop All", skin);
		btLoopAll.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for(LiveTrack track : seq.getTracks()){
					track.unloop();
				}
				
			}});
		table.add(btLoopAll);
		for(int x=0 ; x<nTracks ; x++){
			final TextButton btClip = new TextButton("Unloop", skin);
			final int chan = x;
			btClip.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					seq.getTrack(chan).unloop();
				}
			});
			table.add(btClip);
		}
		table.row();
		
		final TextButton btMuteAll = new TextButton("Mute All", skin, "toggle");
		btMuteAll.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for(Button b : muteButtons){
					b.setChecked(btMuteAll.isChecked());
				}
				
			}});
		table.add(btMuteAll);
		for(int x=0 ; x<nTracks ; x++){
			final TextButton btClip = new TextButton("Mute " + (x+1), skin, "toggle");
			muteButtons.add(btClip);
			final int chan = x;
			btClip.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					seq.getTrack(chan).mute(!btClip.isChecked());
				}
			});
			table.add(btClip);
		}
		table.row();
		table.add().height(10).row();
		
		for(int y=0 ; ; y++){
			final int clip = y;
			boolean hasMore = false;
			
			final TextButton btPlayAll = new TextButton("Clip " + (y+1), skin);
			btPlayAll.addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					int len = division.value;
					
					for(LiveTrack track : seq.getTracks()){
						track.setLoop(clip * len, (clip+1) * len, trigBox.getSelected().value);
					}
					
				}});
			table.add(btPlayAll);
			
			for(int x=0 ; x<nTracks ; x++){
				final int chan = x;
				
				if(seq.getTracks().get(chan).endBeat() < clip * division.value){
					table.add();
					continue;
				}
				hasMore = true;
				
				int len = division.value;
				int hasIn = 0;
				int notesIn = 0;
				int otherIn = 0;
				for(MidiEvent e : seq.getTracks().get(chan).getEvents()){
					long p = e.getTick() / seq.getTracks().get(chan).resolution;
					boolean in = p >= clip * len && p < (clip+1) * len;
					if(in){
						hasIn++;
						
						if(e instanceof NoteOn){
							notesIn++;
						}else{
							otherIn++;
						}
						
					}
				}
				
				String label;
				if(hasIn > 0){
					label = String.valueOf(notesIn);
					if(otherIn > 0) label += "|" + String.valueOf(otherIn);
				}else{
					label = "-";
				}
				
				TextButton btClip = new TextButton(label, skin){
					public void act(float delta) {
						super.act(delta);
						
						int p = seq.getTrack(chan).getPosition();
						int len = division.value;
						boolean in = p >= clip * len && p < (clip+1) * len;
						
						if(seq.getTracks().get(chan).nextLoop){
							if(seq.getTracks().get(chan).nextLoopStart == clip * len &&
									seq.getTracks().get(chan).nextLoopEnd == (clip+1)*len	){
								setColor(Color.CYAN);
							}else{
								setColor(in ? Color.GOLD : Color.WHITE);
							}
						}else{
							setColor(in ? Color.GREEN : Color.WHITE);
						}
						
						
					};
				};
				btClip.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int len = division.value;
						seq.getTracks().get(chan).setLoop(clip * len, (clip+1) * len, trigBox.getSelected().value);
					}
				});
				table.add(btClip);
			}
			table.row();
			if(!hasMore) break;
		}
	}

	@Override
	public void dispose() {
		seq.dispose();
		patch.dispose();
	}

	@Override
	public String toString() {
		return "Music (advanced)";
	}
}