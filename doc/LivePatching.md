## Take advantages of Live coding

Designing a pd patch for a game could be cumbersome : you have to modify your patch in pd, launch your app and
get to the context and restart again.

With LibGDX you can already live code with JVM hot code swapping. With pd, you can use the OSC implementation which
send all message to network in OSC format. You can then open your patch in Puredata and modify it directly during audio design phase.

To do so, you need to configure remote mode in your launcher(s) :
```
PdConfiguration.remoteEnabled = true;
```

see [Full Live Patching Documentation](LivePatching.md)

There is currently some limitations working with arrays. You can write to array throw network but not read them or
get their size (see #5)

# Live code with Puredata

Tweaking your Puredata patches directly in Puredata application when your game is running is crucial. You can
always simulate game events in Puredata but it's better to get events directly from running game.

With Gdx-Pd you can run your game and interact (in both ways) with your patch opened in Puredata application.
All messages (sends, midi stream) are transmit throw network. You can even run your game and Puredata on different
machines in the same network.

## Messaging

All calls like `Pd.audio.sendFloat(...)` will be sent through OSC protocole to your patch.
Make sure you have `gdx-pd-network` abstraction in your Puredata context listeneing on port 3000.

Open your patch in Puredata and you can live code your patch now.

## Midi sequences

TODOC already included in remote mode, just talk about some alternatives ... (aplaymidi, ...etc) !

There is several ways to play midi sequence with puredata application : ALSA midi player (aplaymidi).
You can play sequence from your gdx game as well (with gdx-pd) which is very useful to tweak your synth directly
in Puredata.

### IP Midi

On Ubuntu, you'll need `qmidinet` and `qjackctl`.

First configure PdMidi implementation :

```
Pd.midi = new MidiRemote(MidiRemote.BROADCAST, "255.0.0.37", 21928);
```

It will send midi stream on default qmidinet broadcast address/port.

Then run qmidinet : `qmidinet`

Then connect qmidinet and pd in qjackctrl (connect / ALSA)

### Raw Midi

First configure PdMidi implementation :

```
Pd.midi = new MidiRemote(MidiRemote.UNICAST, "localhost", 3001);
```

Then in Puredata, listen on port 3001 to midi stream with `midi-ip` abstraction. 
See `midi-ip-help.pd` for more information.

