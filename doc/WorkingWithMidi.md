
Gdx-pd provides MIDI files support. Lot of things could be done with MIDI files (not only mudic playback).

In all case, you have to implements your own synthetizer in order to have sounds coming out MIDI files. That is you have to handle midi messages in our patch on your own. However, you could use the gdx-pd-demo project "midiplayer.pd" patch (see https://github.com/mgsx-dev/gdx-pd-demo/tree/master/android/assets/pdmidi) which already impelemnts some General MIDI specification and could be a good start for implementing your own synthetizer.

# Midi music

You can load a MIDI music directly :
```
MidiMusic music = Pd.midi.createMidiMusic(Gdx.files.internal("music/my-music.mid"));
```

Or load it with the asset loader (recommanded) :
```
assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
...
assets.load("music/my-music.mid", Music.class);
...
MidiMusic music = assets.get("music/my-music.mid", Music.class);
```

When you're done, you may close the music :
```
music.dispose();
```

Then play your music as usual.

Notes : 
* you have to load a Pd patch before to ear something from your music.
* Not all Music interface methods are supported yet.

# Loading Midi file direcly

You may just want to load midi file to use sequence directly (or implements your own sequencer) :

```
MidiSequence sequence = new MidiSequence(Gdx.files.internal("music/my-sequence.mid"));
```

## Using advanced sequencer

*coming soon*

