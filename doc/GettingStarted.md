# Initialize in your game

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
		Pd.audio.dispose();
	}
```

Using microphone eats more CPU and is disabled by default. To enable microphone :
```
config.inputChannels = 1; // enable mono microphone
config.inputChannels = 2; // enable stereo microphone
```

# Play with your patch

## open/close patches

You can load a patch directly :

```
PdPatch patch = Pd.audio.open(Gdx.files.internal("pd/my-patch.pd"));
```

Or load it with the asset loader (recommanded) :
```
assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
...
assets.load("resources/test.pd", PdPatch.class);
...
PdPatch patch = assets.get("pd/my-patch.pd", PdPatch.class);
```

When you're done, you may close the patch :
```
Pd.audio.close(patch);
```

## Interact with patches

Most of PdAudio methods are same as LibPD PdBase class (send, read/write arrays), see PdAudio javadoc for details.

You can register listeners to receive message from pd :

```
Pd.audio.addListener("symbol", new PdListener(){ ... })
```

## Get events from patches

You can register listeners to receive message from pd :

```
Pd.audio.addListener("symbol", new PdListener(){ ... })
```


# Disable Pd

In some rare cases you want to disable all Pd stuff (profiling your game without Pd for example).
To do so, you need to configure Pd in your launcher(s) :

```
PdConfiguration.disabled = true;
```