# Getting Started

## Initialize in your game

Most of the time no additionnal code is required in your launchers in order to enable Pd.

You always need to initialize audio in your game. This is the right place to configure audio
channels (enable microphone), tweak audio buffer settings, change sample rate...


```
	@Override
	public void create () 
	{
		PdConfiguration config = new PdConfiguration();
		Pd.audio.create(config);
	}
	...
	@Override
	public void dispose () 
	{
		Pd.audio.release();
	}
```

Using microphone eats more CPU and is disabled by default. To enable microphone :
```
config.inputChannels = 1; // enable mono microphone
config.inputChannels = 2; // enable stereo microphone
```

## Play with it

### open/close patches

```
PdPatch patch = Pd.audio.open(file); // open a patch
... play with patch ...
Pd.audio.close(patch);
```

### Interact with patch

Most of PdAudio methods are same as LibPD PdBase class (send, read/write arrays), see PdAudio javadoc for details.

You can register listeners to receive message from pd :

```
Pd.audio.addListener("symbol", new PdListener(){ ... })
```

### Playing music

In order to play midi streams, your have to implement synthetizers in Pd patches following some General MIDI specification. 
You can start with the provided example "pd/midiplayer.pd".

To play a music, first open the patch and then load the midi file (.mid extension is automatically recognized).
You can then use LibGDX Music API to play it (Music.play())


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

## Disable Pd

In some rare cases you want to disable all Pd stuff (profiling your game without Pd for example).
To do so, you need to configure Pd in your launcher(s) :

```
PdConfiguration.disabled = true;
```