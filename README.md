
[![Build Status](https://travis-ci.org/mgsx-dev/gdx-pd.svg?branch=master)](https://travis-ci.org/mgsx-dev/gdx-pd)

Pure Data extension for LibGDX.

Work in progress :

| Platform   | Scheduled | implemented | Tested |
|------------|-----------|-------------|--------|
| Linux 64   |     y     |      y      |   y    |
| Linux 32   |     y     |      y      |   y    |
| Android    |     y     |      y      |   y    |
| Windows 64 |     y     |      y      |   y    |
| Windows 32 |     y     |      y      |        |
| MacOSX 64  |     -     |      -      |        |
| MacOSX 32  |     -     |      -      |        |
| iOS        |     -     |      -      |        |
| Web        |     -     |      -      |        |

# Introduction

## What is it ?

LibGDX is a cross platform game framework. If you don't know, please visit : http://www.badlogicgames.com/

Puredata (Pd) is an audio synthesis application coded in C providing graphical programming.
gdx-pd is based on LibPd java bindings which. If you don't know, please visit : https://github.com/libpd/libpd

This extension enables audio synthesis in games with pd patches and provides some usefull tools for audio design.

## Features

* Wraps/abstracts libpd in a libGDX fashion.
* Dedicated audio processing thread fully integrated with LibGDX audio implementation : you can use both Pd and Sounds/Musics.
* Pd patch loader for AssetManager
* Midi sequencers (including "à la live" sequencer)
* Midi file reader/writer and loader for AssetManager
* Live patching in Pd throw network/OSC
* LibGDX audio friendly ()
* Full Pd Vanilla support.
* Easy custom Pd externals build with docker.

## Limitations

* Apple platforms not supported yet.
* Web platform not supported yet.

## Futur works

* Audio 3D spatialization / VR.
* Add support for all platforms.

## Documentation

Full documentation is available in this repository :

* [Assets organization](doc/AssetsOrganization.md)
* [Pd patch libraries](doc/PatchLibraries.md)
* [Remote live patching](doc/LivePatching.md)


# How to use

## Configure your LibGDX project

Just add gradle dependencies as usual :

```
project(":core") {
    apply plugin: "java"
    dependencies {
        ...
        compile "net.mgsx.gdx:gdx-pd:$pdVersion"
        ...
    }
}

project(":desktop") {
    apply plugin: "java"
    dependencies {
    	compile project(":core")
        ...
        compile "net.mgsx.gdx:gdx-pd-platform:$pdVersion:desktop"
        compile "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-desktop"
        ...
    }
}

project(":android") {
    apply plugin: "android"

    configurations { natives }

    dependencies {
        compile project(":core")
        ...
        compile "net.mgsx.gdx:gdx-pd-backend-android:$pdVersion"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-armeabi"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-armeabi-v7a"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-arm64-v8a"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-x86"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-x86_64"
		 ...        
    }
}

```

**Note** : because of [#3](https://github.com/mgsx-dev/gdx-pd/issues/3), you need to add jitpack as repository 
(this workaround is necessary while OSC release 0.4 is not in Maven Central yet) :

```
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```


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

see [Full Live Patching Documentation](doc/LivePatching.md)

There is currently some limitations working with arrays. You can write to array throw network but not read them or
get their size (see #5)

## Disable Pd

In some rare cases you want to disable all Pd stuff (profiling your game without Pd for example).
To do so, you need to configure Pd in your launcher(s) :

```
PdConfiguration.disabled = true;
```

# Build from sources

Only require java and docker environement for natives.
Tested on Ubuntu 16.04 x64.

First setup your local git clone :

```
git clone https://github.com/mgsx-dev/gdx-pd.git
cd gdx-pd

git submodule init
git submodule update

cd libpd
git submodule init
git submodule update
cd ..
```

You have to tell gradle about your Android sdk location by creating a local.properties file :

```
echo 'sdk.dir=[absolute path to Android SDK location]' > local.properties
```

Build libpd natives with docker :


```
docker run --rm -v $(pwd):/work -w /work/native -it mgsx/libgdx ../gradlew buildNative

sudo chown -R $USER:$USER .
```

And then publish locally in order to use it in your local projects :

```
./gradlew publishToMavenLocal

```

## Run examples

A demo application is available in sources and will be published soon on Android store.

# Credits

Demo application is shipped with some midi files kindly authorized by the author : Jason "Jay" Reichard who published a lot of
nice old school game music covers, please take a look at his website : http://zorasoft.net/midi.html



