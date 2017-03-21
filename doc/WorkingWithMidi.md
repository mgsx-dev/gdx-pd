
Gdx-pd provides MIDI files support. Lot of things could be mage with MIDI files (not only mudic playback).

In all case, you have to implements your own synthetizer in order to have sounds coming out MIDI files. That is you have to
handle midi messages in our patch on your own. However, you could use the gdx-pd-demo project "midiplayer.pd" patch (see https://github.com/mgsx-dev/gdx-pd-demo/tree/master/android/assets/pdmidi) which already impelemnts some General MIDI specification and could be a good start for implementing your own synthetizer.

# Midi music

The first recommanded way is to MidiMusic (an implementation of LibGDX Music interface).
First register loader with your AssetManager :
TODO

Then load and play your music (remember, you have to load a Pd patch before to here omething from your music)
TODO

Use Music interface as usual (note all methods are supported yet)
TODO

# Loading Midi file direcly

A second way is to load a midi file directly and load it in a sequencer.
TODO

## Using advanced sequencer
TODO

